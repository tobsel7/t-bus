package mf;

/**
 * The MessageListener interface is provided by the messaging framework and has to be implemented by the application in order for the messaging framework to be useful.
 *
 * @author Tobias Haider
 */
public interface MessageHandler {

    /**
     * This method allows the application to customize its reaction to received messages.
     * The application needs to be able to deal with any message type (Class), that it has subscribed to.
     *
     * @param message Incoming message from other applications
     */
    void receiveMessage(Object message);
}
