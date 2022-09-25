package utilities;

import mf.MessageHandler;

/**
 * A class holding relevant data for the delivery of a message to the application.
 *
 * @author Tobias Haider
 */
public class MessageDelivery<MessageType> {
    private final MessageType message;
    private final MessageHandler<MessageType> handler;

    /**
     * Constructor for the Delivery object.
     * @param message The message to be delivered
     * @param handler The mapped function which will be used to process the message
     */
    public MessageDelivery(MessageType message, MessageHandler<MessageType> handler) {
        this.message = message;
        this.handler = handler;
    }

    /**
     * Processing function to actually process the message
     * The MessageDeliverer waits until free threads are available to process the message
     */
    public void deliver() {
        handler.receiveMessage(message);
    }
}
