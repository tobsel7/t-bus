package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The forwarder represents an abstraction for all outgoing network connections from the message bus. It implements the basic routing algorithm. All real networking tasks are forwarded to the sender.
 *
 * @author Tobias Haider
 * @see Sender
 */
public class Forwarder {
    private final Map<String, Sender> senders;

    private static Logger logger = LoggerFactory.getLogger(Forwarder.class);

    /**
     * Constructor for the forwarder. Simply instantiates an empty hash map for internally storing sender objects.
     */
    public Forwarder() {
        senders = new ConcurrentHashMap<>();
    }

    /**
     * Add new outgoing connection. Messages will also be sent/forwarded to this connection from now on.
     *
     * @param identifier Identifier of a communication partner
     * @param ip         IP address of the outgoing connection
     * @param port       Port number of the outgoing connection
     */
    public void addConnection(String identifier, String ip, int port) {
        senders.put(identifier, new Sender(ip, port));

    }

    /**
     * Remove outgoing connection. Messages will not be sent/forwarded to this connection anymore.
     *
     * @param identifier Identifier of a communication partner
     */
    public void removeConnection(String identifier) {
        senders.remove(identifier);
    }

    /**
     * Internal function used for forwarding messages to all outgoing connections.
     *
     * @param senderId   The sender to which the message should not be forwarded
     * @param receiverId The receiver which should get the message
     * @param message    Message received and to be forwarded to outgoing connections.
     * @see Sender
     */
    public void forwardMessage(String senderId, String receiverId, String message) {
        if (senders.containsKey(receiverId)) {
            // Receiver is neighbour. Send message only to one this peer.
            senders.get(receiverId).sendMessage(message);
        } else {
            // Receiver is not a neighbour. Send message to all peers except for sender.
            senders.entrySet().stream().filter(sender -> !sender.getKey().equals(senderId)).forEach(sender -> sender.getValue().sendMessage(message));
        }
        logger.trace("Forwarding a message to all outgoing connections.");
    }
}
