package mf;

/**
 * The MessageListener interface is provided by the messaging framework and has to be implemented by the application in order for the messaging framework to be useful.
 *
 * @author Tobias Haider
 */
public abstract class MessageHandler<MessageType> {
    private final Class<?> type;

    public static MessageHandler EMPTY_HANDLER = new MessageHandler(Object.class) {
        @Override
        public void receiveMessage(Object message) {}
    };

    protected MessageHandler(Class<?> type) {
        this.type = type;
    }

    /**
     * This method allows the application to customize its reaction to received messages.
     * The application needs to be able to deal with any message type (Class), that it has subscribed to.
     *
     * @param message Incoming message from other applications
     */
    public abstract void receiveMessage(MessageType message);

    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageHandler<?> that = (MessageHandler<?>) o;

        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
