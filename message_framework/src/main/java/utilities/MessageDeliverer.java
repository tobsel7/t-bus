package utilities;

import mf.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A thread delivering message objects to the application in a synchronous way.
 *
 * @author Tobias Haider
 * @see MessageHandler
 */
public class MessageDeliverer extends Thread {
    private final ArrayBlockingQueue<MessageDelivery> queue;
    private boolean running;

    private static Logger logger = LoggerFactory.getLogger(MessageDeliverer.class);

    /**
     * Constructor for a message deliverer. Configures the number of cashed messages.
     *
     * @param capacity Maximal amount of messages to be cached.
     */
    public MessageDeliverer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        running = false;
    }

    /**
     * Behaviour of the thread. Takes messages from the queue and forwards them to the application synchronously.
     */
    @Override
    public void run() {
        running = true;
        MessageDelivery delivery;
        while (running) {
            try {
                delivery = queue.take();
                delivery.deliver();
            } catch (Exception e) {
                logger.debug("Could not deliver a message.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a message to the queue for delivery.
     *
     * @param message Message to be delivered to the application
     */
    public void deliverMessage(Object message, MessageHandler handler) {
        MessageDelivery delivery = new MessageDelivery(message, handler);
        queue.offer(delivery);
    }

    public void stopDelivering() {
        running = false;
    }
}
