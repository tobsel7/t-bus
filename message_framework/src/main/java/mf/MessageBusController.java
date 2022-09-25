package mf;

import network.Forwarder;
import utilities.*;
import network.Receiver;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MessageBusController class represents the controlling class of the messaging framework. I is used for connecting and configuring all other objects used by the framework.
 * The processing, sending and receiving of messages is outsourced to the other classes.
 *
 * @author Tobias Haider
 * @see MessageConverter
 * @see Forwarder
 * @see Receiver
 * @see MessageDeliverer
 * @see MessageIdStorage
 * @see SubscriptionService
 */
public class MessageBusController implements MessageBus {

    private static final int MESSAGE_STORAGE_TO_ID_STORAGE_RATIO = 10;
    /**
     * Identifier of the messaging bus
     */
    private final String identifier;
    /**
     * Counter variable for the message ids
     */
    private final AtomicInteger messageIdCounter;
    /**
     * Value for any receiver
     */
    private static final String ANY_RECEIVER_STRING = "any";

    /**
     * Networking elements
     */
    private final Receiver receiver;
    private final Forwarder forwarder;

    /**
     * Utility objects
     */
    private final SubscriptionService subscriptions;
    private final MessageConverter messageConverter;
    private final MessageIdStorage messageIdStorage;
    private final MessageDeliverer deliverer;

    /**
     * Standard time to live for a message package
     */
    private final int initialTimeToLive;

    /**
     * Boolean defining whether received messages are forwarded.
     */
    private final boolean forwardsMessages;

    private static Logger logger = LoggerFactory.getLogger(MessageBusController.class);

    /**
     * Constructor for a MessageBus. Nearly all the configuration happens with the constructor call.
     *
     * @param identifier        Unique identifier of the main application
     * @param initialTimeToLive Standard amount of hops the package is allowed to make in total
     * @param serverPort        Desired port used for receiving messages from other applications
     * @param forwardsMessages  This message bus should forward messages to outgoing connections
     * @throws IOException              when the message bus could not be created, because the network configuration is invalid
     * @throws IllegalArgumentException when the Identifier is not allowed
     */
    MessageBusController(String identifier, int initialTimeToLive, int messageCapacity, int serverPort, boolean forwardsMessages) throws IOException, IllegalArgumentException {
        if (identifier.equals(ANY_RECEIVER_STRING)) {
            throw new IllegalArgumentException("The identifier cannot be :" + ANY_RECEIVER_STRING);
        }
        this.identifier = identifier;
        this.messageIdCounter = new AtomicInteger(0);
        this.receiver = new Receiver(serverPort, this);
        this.forwarder = new Forwarder();

        this.subscriptions = new SubscriptionService();
        this.messageConverter = new MessageConverter(subscriptions);
        this.initialTimeToLive = initialTimeToLive;
        this.forwardsMessages = forwardsMessages;

        this.messageIdStorage = new MessageIdStorage(messageCapacity * MESSAGE_STORAGE_TO_ID_STORAGE_RATIO);
        this.deliverer = new MessageDeliverer(messageCapacity);
        deliverer.start();
        receiver.start();
    }

    /**
     * Allows to publish a message and therefore send the message to all outgoing connections. Only the receiver specified with the receiver id will actually read the message.
     *
     * @param receiverId  Unique receiverId of a peer
     * @param messageType Class of the message Object
     * @param message     Message Object. This object has to be an instance of the given messageType class
     * @see Forwarder
     */
    @Override
    public void publishMessageTo(String receiverId, Class<?> messageType, Object message) {
        String messageId = createMessageId();
        messageIdStorage.add(messageId);
        MessagePackage messagePackage = new MessagePackage(messageId, identifier, receiverId, messageType.getSimpleName(), initialTimeToLive, message);
        try {
            String messageJson = messageConverter.convertToJson(messageType, messagePackage);
            forwarder.forwardMessage(identifier, receiverId, messageJson);
        } catch (Exception e) {
            logger.debug("Could not publish message. An Exception occurred while sending.");
            e.printStackTrace();
        }
        logger.debug("Published a message of type {}.", messageType.getSimpleName());
    }

    /**
     * Allows to publish a message and therefore send the message to all outgoing connections. This message is used, if the message is not addressed to a specific receiver.
     *
     * @param messageType Class of the message Object
     * @param message     Message Object. This object has to be an instance of the given messageType class
     * @see Forwarder
     */
    @Override
    public void publishMessageToAny(Class<?> messageType, Object message) {
        publishMessageTo(ANY_RECEIVER_STRING, messageType, message);
    }

    /**
     * Add a class type to the subscription service. Messages of this type will be processed using the message handler
     *
     * @param handler Message type to which the application wants to listen
     */
    @Override
    public void addMessageResponse(MessageHandler handler) {
        subscriptions.add(handler);
    }

    /**
     * Add a class type to the subscription service. Message of this type will be ignored.
     *
     * @param messageType Message type to which the application wants to listen
     */
    @Override
    public void removeMessageResponse(Class<?> messageType) {
        subscriptions.remove(messageType);
    }

    /**
     * Add new outgoing connection. Messages will also be sent/forwarded to this connection from now on.
     *
     * @param identifier Identifier of a communication partner
     * @param ip         IP address of the outgoing connection
     * @param port       Port number of the outgoing connection
     */
    public void addConnection(String identifier, String ip, int port) {
        forwarder.addConnection(identifier, ip, port);
    }
    /**
     * Remove outgoing connection. Messages will not be sent/forwarded to this connection anymore.
     *
     * @param identifier Identifier of a communication partner
     */
    @Override
    public boolean hasConnection(String identifier) {
        return forwarder.hasConnection(identifier);
    }

    /**
     * Remove outgoing connection. Messages will not be sent/forwarded to this connection anymore.
     *
     * @param identifier Identifier of a communication partner
     */
    @Override
    public void removeConnection(String identifier) {
        forwarder.removeConnection(identifier);
    }

    /**
     * Allows you to stop all threads of the message bus. This operation should be called before terminating the program.
     */
    @Override
    public void stop() {
        receiver.stopReceiving();
        deliverer.stopDelivering();
    }

    /**
     * Internal function with the logic for processing of a received message. This method should only called by a Receiver object.
     *
     * @param receivedMessage Received message in json format.
     * @see Receiver
     */
    public void processMessage(String receivedMessage) {
        logger.trace("Received a message from the receiver for processing.");
        try {
            String messageId = messageConverter.getValue(receivedMessage, "messageId");
            if (!messageIdStorage.contains(messageId)) { // Ignore already received messages
                messageIdStorage.add(messageId);  // Ignore this message in the future
                // Decrement ttl
                int ttl = Integer.parseInt(messageConverter.getValue(receivedMessage, "timeToLive")) - 1;
                // Forward the message
                if (forwardsMessages && ttl > 0) {
                    // Update the message package values
                    String senderId = messageConverter.getValue(receivedMessage, "senderId");
                    String receiverId = messageConverter.getValue(receivedMessage, "receiverId");
                    receivedMessage = messageConverter.setValue(receivedMessage, "timeToLive", String.valueOf(ttl));
                    receivedMessage = messageConverter.setValue(receivedMessage, "senderId", identifier);
                    // Let the forwarder forward this message
                    forwarder.forwardMessage(senderId, receiverId, receivedMessage);
                }
                // Check, if the application is interested in this message type
                String messageType = messageConverter.getValue(receivedMessage, "messageType");
                if (subscriptions.contains(messageType)) {
                    MessagePackage messagePackage = messageConverter.convertToMessagePackage(receivedMessage);
                    if (messagePackage.getReceiverId().equals(ANY_RECEIVER_STRING) || messagePackage.getReceiverId().equals(identifier)) {
                        logger.debug("Letting the message deliverer handle the message from " + messagePackage.getSenderId() + ".");
                        deliverer.deliverMessage(messagePackage.getMessage(), subscriptions.getHandler(messageType));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal function for generating message ids using the counter variable messageIdCounter
     *
     * @return Next message id
     */
    private String createMessageId() {
        return identifier + ":" + messageIdCounter.incrementAndGet();
    }
}
