package mf;

import message_types.TestMessage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.any;

public class MessageBusControllerTest {

    @Test
    public void testPublishMessageTo() throws Exception {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        Mockito.when(handler.getType()).thenReturn(TestMessage.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        MessageBusFactory factory = new MessageBusFactory();

        // Set up sender
        factory.setIdentifier("me");
        factory.setServerPort(3101);
        MessageBus me = factory.create();
        me.addConnection("other", "127.0.0.1", 3102);

        // Set up receiver
        factory.setIdentifier("other");
        factory.setServerPort(3102);
        MessageBus other = factory.create();
        other.addMessageResponse(handler);

        // Create and send message
        TestMessage message = new TestMessage("Hi");
        me.publishMessageTo("other", TestMessage.class, message);

        // Send message and verify
        verify(handler, timeout(2000).times(1)).receiveMessage(messageCaptor.capture());
        assertEquals(message.getMsg(), ((TestMessage) messageCaptor.getValue()).getMsg());
    }

    @Test
    public void testPublishMessageToAny() throws Exception {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        Mockito.when(handler.getType()).thenReturn(TestMessage.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        MessageBusFactory factory = new MessageBusFactory();

        // Set up sender
        factory.setIdentifier("me");
        factory.setServerPort(3201);
        MessageBus me = factory.create();
        me.addConnection("other", "127.0.0.1", 3202);

        // Set up receiver
        factory.setIdentifier("other");
        factory.setServerPort(3202);
        MessageBus other = factory.create();
        other.addMessageResponse(handler);

        // Create and send message
        TestMessage message = new TestMessage("Hi");
        me.publishMessageToAny(TestMessage.class, message);

        // Send message and verify
        verify(handler, timeout(2000).times(1)).receiveMessage(messageCaptor.capture());
        assertEquals(message.getMsg(), ((TestMessage) messageCaptor.getValue()).getMsg());
    }

    @Test
    public void testForwardMessage() throws IOException {
        MessageBusFactory factory = new MessageBusFactory();

        // Set up three message buses for forwarding
        factory.setServerPort(3301);
        factory.setIdentifier("mb1");
        MessageBus mb1 = factory.create();
        mb1.addConnection("mb2", "127.0.0.1", 3302);
        factory.setIdentifier("mb2");
        factory.setServerPort(3302);
        MessageBus mb2 = factory.create();
        mb2.addConnection("mb3", "127.0.0.1", 3303);
        factory.setIdentifier("mb3");
        factory.setServerPort(3303);
        MessageBus mb3 = factory.create();
        mb3.addConnection("receiver", "127.0.0.1", 3304);

        // Set up receiver
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        Mockito.when(handler.getType()).thenReturn(TestMessage.class);
        factory.setServerPort(3304);
        factory.setIdentifier("receiver");
        MessageBus mb4 = factory.create();
        mb4.addMessageResponse(handler);

        // Send message and verify
        TestMessage message = new TestMessage("Hi");
        mb1.publishMessageTo("receiver", TestMessage.class, message);
        verify(handler, timeout(2000).times(1)).receiveMessage(any(TestMessage.class));
    }

    @Test
    public void testPerformance() throws Exception {
        MessageBusFactory factory = new MessageBusFactory();

        // Set up sender
        factory.setServerPort(3401);
        factory.setIdentifier("mb1");
        MessageBus mb1 = factory.create();
        mb1.addConnection("mb2", "127.0.0.1", 3402);

        // Set up receiver
        factory.setServerPort(3402);
        factory.setIdentifier("mb2");
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        Mockito.when(handler.getType()).thenReturn(TestMessage.class);
        MessageBus mb2 = factory.create();
        mb2.addMessageResponse(handler);

        // Send a lot of messages
        TestMessage message = new TestMessage("Hi");
        for (int i = 0; i < 100; i++) {
            mb1.publishMessageTo("mb2", TestMessage.class, message);
        }
        // Verify that all messages have been received
        verify(handler, timeout(4000).times(100)).receiveMessage(any(TestMessage.class));
    }
}