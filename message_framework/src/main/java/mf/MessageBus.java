package mf;


/**
 * MessageBus is the interface with which the application interacts with the messaging framework.
 *
 * @author Tobias Haider
 */
public interface MessageBus {

    /**
     * Passes a message object to the framework for sending to a specific receiver
     *
     * @param receiver    Id of peer to which the message should be sent
     * @param messageType Class type of which the message object is
     * @param message     Message object to be sent
     */
    void publishMessageTo(String receiver, Class<?> messageType, Object message);

    /**
     * Passes a message object to the framework for publishing the message (everybody receives the message)
     *
     * @param messageType Class type of which the message object is
     * @param message     Message object to be sent
     */
    void publishMessageToAny(Class<?> messageType, Object message);

    /**
     * Subscribes to a message type. You will receive messages of this type from now on.
     *
     * @param handler An object defining the response to a received message
     */
    void addMessageResponse(MessageHandler handler);

    /**
     * Unsubscribe from a message type. You will not receive messages of this type anymore.
     *
     * @param messageType Message type to which the application does not want to receive anymore
     */
    void removeMessageResponse(Class<?> messageType);

    /**
     * Add new outgoing connection. Messages will also be sent/forwarded to this connection from now on.
     *
     * @param identifier Identifier of a communication partner
     * @param ip         IP address of the outgoing connection
     * @param port       Port number of the outgoing connection
     */
    void addConnection(String identifier, String ip, int port);

    /**
     * Check, whether a connection is already present in the message bus
     * @param identifier Identifier of a communication partner
     * @return Found a connection for the give identifier
     */
    boolean hasConnection(String identifier);

    /**
     * Remove outgoing connection. Messages will not be sent/forwarded to this connection anymore.
     *
     * @param identifier Identifier of a communication partner
     */
    void removeConnection(String identifier);

    /**
     * Allows you to stop all threads of the message bus. This operation should be called before terminating the program.
     */
    void stop();
}
