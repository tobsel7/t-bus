package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import message_types.TestMessage;
import mf.MessagePackage;
import utilities.MessageConverter;
import org.junit.Test;


import static org.junit.jupiter.api.Assertions.*;

public class MessageConverterTest {

    @Test
    public void testConvertToJson() {
        MessageConverter mc = new MessageConverter(null);
        ObjectMapper om = new ObjectMapper();

        TestMessage message = new TestMessage("Hi");
        JsonNode jsonNode = null;
        try {
            MessagePackage messagePackage = new MessagePackage("someId", "me", "you", TestMessage.class.getSimpleName(), 3, message);
            String metaDataJson = mc.convertToJson(TestMessage.class, messagePackage);
            jsonNode = om.readTree(metaDataJson);
        } catch (Exception e) {
            fail("Message could not be parsed.");
        }

        assertEquals("me", jsonNode.get("senderId").asText());
        assertEquals("you", jsonNode.get("receiverId").asText());
        assertEquals(3, jsonNode.get("timeToLive").asInt());
        assertEquals("TestMessage", jsonNode.get("messageType").asText());
        assertEquals("Hi", jsonNode.get("message").get("msg").asText());
    }
}
