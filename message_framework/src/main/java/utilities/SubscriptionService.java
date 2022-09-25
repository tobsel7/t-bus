package utilities;

import mf.MessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class keeping track of subscribed message types. Essentially a wrapper for a list of message types.
 *
 * @author Tobias Haider
 */
public class SubscriptionService {
    private final Map<String, MessageHandler> subscriptions;

    /**
     * Constructor for the subscription service. Creates empty hashmap
     */
    public SubscriptionService() {
        subscriptions = new ConcurrentHashMap<>();
    }

    /**
     * Add new message type to listen to.
     *
     * @param handler The function used to process an incoming message
     */
    public void add(MessageHandler handler) {
        subscriptions.put(handler.getType().getSimpleName(), handler);
    }

    /**
     * Remove message type from subscription.
     *
     * @param messageType Message type the application no longer wants to receive
     */
    public void remove(Class<?> messageType) {
        subscriptions.remove(messageType.getSimpleName());
    }

    /**
     * Deletes all message subscriptions. The application will receive no messages.
     */
    public void clear() {
        subscriptions.clear();
    }

    /**
     * Get the corresponding class to a message typ string, if it exists.
     *
     * @param messageType String corresponding to a java class
     * @return Java class which defines a message type
     */
    public Class<?> getType(String messageType) {
        return this.getHandler(messageType).getType();
    }

    public MessageHandler getHandler(String messageType) {
        return subscriptions.getOrDefault(messageType, MessageHandler.EMPTY_HANDLER);
    }

    /**
     * Asks the service, if it contains a certain message type.
     *
     * @param messageType String corresponding to a message type class.
     * @return The application has subscribed to the given message type.
     */
    public boolean contains(String messageType) {
        return subscriptions.containsKey(messageType);
    }
}
