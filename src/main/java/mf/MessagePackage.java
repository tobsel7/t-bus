package mf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The MessagePackage class is used to wrap any java object inside a normed package and provide meta data for the propagation between peers.
 *
 * @author Tobias Haider
 */
public class MessagePackage {
    private final String messageId;
    private final String senderId;
    private final String receiverId;
    private final String messageType;
    private final int timeToLive;
    private final Object message;

    /**
     * Constructor for a MessagePackage
     *
     * @param senderId    Unique identifier of the sender
     * @param receiverId  Unique identifier of the sender
     * @param messageType SimpleString representation of the message type
     * @param timeToLive  Amount of hops the package is allowed to make
     * @param message     Actual message, that should be delivered to one or multiple receivers
     */
    @JsonCreator
    public MessagePackage(@JsonProperty("messageId") String messageId, @JsonProperty("senderId") String senderId, @JsonProperty("receiverId") String receiverId,
                          @JsonProperty("messageType") String messageType, @JsonProperty("timeToLive") int timeToLive,
                          @JsonProperty("message") Object message) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.timeToLive = timeToLive;
        this.message = message;
    }

    /**
     * @return Unique identifier of the message package
     */
    @JsonProperty("messageId")
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return Unique identifier of the sender
     */
    @JsonProperty("senderId")
    public String getSenderId() {
        return senderId;
    }

    /**
     * @return Unique identifier of the receiver
     */
    @JsonProperty("receiverId")
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @return messageType SimpleString representation of the message type
     */
    @JsonProperty("messageType")
    public String getMessageType() {
        return messageType;
    }

    /**
     * @return timeToLive Amount of hops the package is allowed to make
     */
    @JsonProperty("timeToLive")
    public int getTimeToLive() {
        return timeToLive;
    }

    /**
     * @return message Actual message, that should be delivered to one or multiple receivers
     */
    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

}
