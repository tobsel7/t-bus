package utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class keeping track of received messages.
 *
 * @author Tobias Haider
 */
public class MessageIdStorage {
    private final ConcurrentHashMap<String, Date> receivedMessages;
    private final int capacity;

    private static Logger logger = LoggerFactory.getLogger(MessageIdStorage.class);

    /**
     * Constructor for the message storage. Configures internal values for the message id storage.
     *
     * @param capacity Amount of message ids to keep track of
     */
    public MessageIdStorage(int capacity) {
        this.receivedMessages = new ConcurrentHashMap<>(capacity);
        this.capacity = capacity;
    }

    /**
     * Internal method deleting the oldest message ids, in order to not overload the storage.
     */
    private void deleteMessages() {
        final int size = receivedMessages.size();
        receivedMessages.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).limit(size / 3).map(Map.Entry::getKey).forEach(receivedMessages::remove);
        logger.trace("Deleted " + (size - receivedMessages.size()) + " or more messages");
    }


    /**
     * Add new message id to be kept track off. Delete oldest message, if storage is close to full.
     *
     * @param messageId Id of a received message
     */
    public void add(String messageId) {
        logger.trace("Added messageId " + messageId + " to the storage.");
        if (receivedMessages.size() >= capacity) {
            logger.trace("Storage is approaching limit. Deleting some message ids.");
            deleteMessages();
        }
        receivedMessages.put(messageId, new Date());
    }

    /**
     * Ask the storage whether a certain message was already received previously.
     *
     * @param messageId Message id of a received message
     * @return The message bus already received this message.
     */
    public boolean contains(String messageId) {
        logger.trace("Contains " + messageId + " " + receivedMessages.containsKey(messageId));
        return receivedMessages.containsKey(messageId);
    }
}