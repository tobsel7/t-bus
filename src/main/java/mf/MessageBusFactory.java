package mf;

import java.io.IOException;

/**
 * Factory used for configuring and creating a messaging bus. It advised to set all values before creating a MessageBus object.
 *
 * @author Tobias Haider
 */
public class MessageBusFactory {
    // Default values for the message bus configuration
    private static final String DEFAULT_IDENTIFIER = "";
    private static final MessageHandler DEFAULT_MESSAGE_HANDLER = System.out::println;
    private static final int DEFAULT_INITIAL_TIME_TO_LIVE = 5;
    private static final int DEFAULT_SERVER_PORT = 5678;
    private static final int DEFAULT_MESSAGE_CAPACITY = 1000;
    private static final boolean FORWARDS_MESSAGES = true;

    // Configuration parameters for the message bus
    private String identifier;
    private MessageHandler messageHandler;
    private int initialTimeToLive;
    private int serverPort;
    private int messageCapacity;
    private boolean forwardsMessages;

    /**
     * Constructor for a MessageBusFactory. Initializes all configuration values with default variables.
     */
    public MessageBusFactory() {
        this.identifier = DEFAULT_IDENTIFIER;
        this.messageHandler = DEFAULT_MESSAGE_HANDLER;
        this.initialTimeToLive = DEFAULT_INITIAL_TIME_TO_LIVE;
        this.serverPort = DEFAULT_SERVER_PORT;
        this.messageCapacity = DEFAULT_MESSAGE_CAPACITY;
        this.forwardsMessages = FORWARDS_MESSAGES;
    }

    /**
     * Setter for the variable identifier. The identifier should be a unique string identifying a message bus / application.
     *
     * @param identifier Unique identifier used by the message bus
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Setter for the message handler. This functional interface defines the way an application handles incoming message objects.
     *
     * @param messageHandler A message handler implementation
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Setter for the time to live parameter. This should be set to the maximal amount of hops a message could need from one point to the receiver.
     *
     * @param initialTimeToLive Time to live parameter
     */
    public void setInitialTimeToLive(int initialTimeToLive) {
        this.initialTimeToLive = initialTimeToLive;
    }

    /**
     * Setter for the message capacity. This can be set to the maximal amount of messages the framework stores, if messages are received simultaneously.
     * Keep the default value, if you are not sure about the configuration! Setting a too low value can result in lost messages.
     *
     * @param messageCapacity The amount of messages stored internally
     */
    public void setMessageCapacity(int messageCapacity) {
        this.messageCapacity = messageCapacity;
    }

    /**
     * Setter for the server port. Defines the port to which the message bus will listen for incoming messages.
     *
     * @param serverPort Port number of the message bus
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Setter for the boolean defining whether this application should forward incoming messages to peers.
     *
     * @param forwardsMessages This application forwards incoming messages to others.
     */
    public void setForwardsMessages(boolean forwardsMessages) {
        this.forwardsMessages = forwardsMessages;
    }

    /**
     * Creates a configured message bus object. This method should be called, after all parameters are correctly set.
     *
     * @return Message bus object
     * @throws IOException              when the message bus could not be created, because the network configuration is invalid
     * @throws IllegalArgumentException when the Identifier is not allowed
     */
    public MessageBus create() throws IOException, IllegalArgumentException {
        return new MessageBusController(identifier, messageHandler, initialTimeToLive, messageCapacity, serverPort, forwardsMessages);
    }
}
