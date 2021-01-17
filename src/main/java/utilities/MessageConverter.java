package utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mf.MessageBusController;
import mf.MessagePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * The MessageConverter class is a pure utility class used for dealing with Json and conversion between Json and java objects.
 * The provided methods should only be used by the MessageBus, since many operations can throw exception, when called with wrong-structured parameters.
 *
 * @see MessageBusController
 */
public class MessageConverter {
    private final ObjectMapper objectMapper;
    private final SubscriptionService subscriptions;

    private static Logger logger = LoggerFactory.getLogger(MessageConverter.class);

    /**
     * Constructor for the MessageConverter
     * Instantiates an empty Set for the subscribed message types.
     * Instantiated the object mapper using external libraries.
     */
    public MessageConverter(SubscriptionService subscriptions) {
        objectMapper = new ObjectMapper();
        this.subscriptions = subscriptions;
    }

    /**
     * Utility function for the conversion from a message package to its json string representation.
     * Please note, that this function can only work correctly, if the message object inside the message Package is an instance of the given message type class.
     *
     * @param messageType    Message Type class, which is provided inside the message package
     * @param messagePackage Message package object with message meta data and the message object
     * @return The json representation of the given message package
     * @throws Exception Throws an exception, if the the message package is malformed or the given message type is wrong
     */
    public String convertToJson(Class<?> messageType, MessagePackage messagePackage) throws Exception {
        Object message = messagePackage.getMessage();

        if (messageType.isInstance(message)) {
            ObjectNode messageNode = objectMapper.valueToTree(messageType.cast(message));
            ObjectNode root = objectMapper.createObjectNode();
            root.putPOJO("message", messageNode);
            root.put("messageId", messagePackage.getMessageId());
            root.put("senderId", messagePackage.getSenderId());
            root.put("receiverId", messagePackage.getReceiverId());
            root.put("messageType", messagePackage.getMessageType());
            root.put("timeToLive", messagePackage.getTimeToLive());
            return root.toString();
        } else {
            throw new Exception("Wrong message type given: " + messageType.getSimpleName());
        }
    }

    /**
     * Utility function which converts a json string to a message package object.
     * This function works correctly, if the wrapped message is known to the message bus and the whole message is json-parsable.
     *
     * @param json Json string representation of a message package
     * @return A MessagePackage object containing the information given in the json string
     * @throws Exception Throws an exception, if the message type is not known or can not be parsed correctly.
     */
    public MessagePackage convertToMessagePackage(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        String messageId = node.get("messageId").asText();
        String senderId = node.get("senderId").asText();
        String receiverId = node.get("receiverId").asText();
        String messageType = node.get("messageType").asText();
        int timeToLive = node.get("timeToLive").asInt();
        String messageString = node.get("message").toString();

        if (subscriptions.contains(messageType)) {
            Object message = objectMapper.readValue(messageString, subscriptions.get(messageType));
            return new MessagePackage(messageId, senderId, receiverId, messageType, timeToLive, message);
        } else {
            NoSuchElementException exception = new NoSuchElementException("Message type " + messageType + "not known to the messaging bus.");
            logger.error("Could not convert json to message package.", exception);
            throw exception;
        }
    }

    /**
     * Function used for retrieving a value from a json string.
     *
     * @param json      Json Message containing a certain field
     * @param valueName The name (key) of the json field
     * @return The value assigned to a field name
     */
    public String getValue(String json, String valueName) {
        try {
            JsonNode root = objectMapper.readTree(json);
            if (root.has(valueName)) {
                return root.get(valueName).asText();
            }
        } catch (JsonProcessingException e) {
            logger.error("Could not process json", e);
        }
        return "";
    }

    /**
     * Basic function for setting a new value to a json field
     *
     * @param json      Json Message containing a certain field
     * @param valueName The name (key) of the json field
     * @param value     The value assigned to a field name
     * @return Resulting manipulated json string
     * @throws Exception Field name can not be found or json is not parsable
     */
    public String setValue(String json, String valueName, String value) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        if (root.has(valueName)) {
            ((ObjectNode) root).put(valueName, value);
            return root.toString();
        } else {
            IllegalArgumentException exception = new IllegalArgumentException("Field " + valueName + " not found");
            logger.error("Could not find value.", exception);
            throw exception;
        }
    }
}
