package utilities;

import message_types.TestMessage;
import mf.MessageBus;
import mf.MessageBusFactory;
import mf.MessageHandler;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class MessageIdStorageTest {

    @Test
    public void testIgnoreMultipleMessages() throws Exception {
        MessageBusFactory factory = new MessageBusFactory();

        // Create a loop with two message buses
        factory.setIdentifier("mb1");
        factory.setServerPort(5101);
        MessageHandler handler1 = Mockito.mock(MessageHandler.class);
        factory.setMessageHandler(handler1);
        MessageBus mb1 = factory.create();
        mb1.addConnection("mb2", "127.0.0.1", 5102);
        mb1.listenToMessageType(TestMessage.class);
        factory.setIdentifier("mb2");
        factory.setServerPort(5102);
        MessageHandler handler2 = Mockito.mock(MessageHandler.class);
        factory.setMessageHandler(handler2);
        MessageBus mb2 = factory.create();
        mb2.addConnection("mb1", "127.0.0.1", 5101);
        mb2.listenToMessageType(TestMessage.class);

        // Publish message
        TestMessage message = new TestMessage("Hi");
        mb1.publishMessageToAny(TestMessage.class, message);

        verify(handler1, timeout(1000).times(0)).receiveMessage(any(TestMessage.class));
        verify(handler2, timeout(1000).times(1)).receiveMessage(any(TestMessage.class));
    }

    @Test
    public void testMessageStored() {
        MessageIdStorage storage = new MessageIdStorage(10);

        // Add to many message ids
        for (int i = 0; i < 20; i++) {
            storage.add(String.valueOf(i));
        }

        // First messages should be deleted
        for (int i = 0; i < 10; i++) {
            assertFalse(storage.contains(String.valueOf(i)));
        }
        // Last message ids should still be stored
        for (int i = 13; i < 20; i++) {
            assertTrue(storage.contains(String.valueOf(i)));
        }
    }
}
