package utilities;

import mf.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * A thread delivering message objects to the application in a synchronous way.
 *
 * @author Tobias Haider
 * @see MessageHandler
 */
public class MessageDeliverer extends Thread {
    private final MessageHandler handler;
    private final ArrayBlockingQueue<Object> queue;
    private boolean running;

    private static Logger logger = LoggerFactory.getLogger(MessageDeliverer.class);

    /**
     * Constructor for a message deliverer. Configures internal values and injects message handler to the object.
     *
     * @param handler  Message handler defining the way an application handles incoming messages
     * @param capacity Maximal amount of messages to be cached.
     */
    public MessageDeliverer(MessageHandler handler, int capacity) {
        this.handler = handler;
        this.queue = new ArrayBlockingQueue<>(capacity);
        running = false;
    }

    /**
     * Behaviour of the thread. Takes messages from the queue and forwards them to the application synchronously.
     */
    @Override
    public void run() {
        running = true;
        Object message;
        while (running) {
            try {
                message = queue.take();
                handler.receiveMessage(message);
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
    public void deliverMessage(Object message) {
        queue.offer(message);
    }

    public void stopDelivering() {
        running = false;
    }
}
