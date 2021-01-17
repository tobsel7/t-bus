package mf;

import org.junit.Test;

import java.io.IOException;

public class MessageBusFactoryTest {

    @Test
    public void testMessageBusConstructor() throws IOException {
        MessageBusFactory factory = new MessageBusFactory();
        factory.setIdentifier("me");
        factory.setInitialTimeToLive(5);
        factory.setMessageHandler(System.out::println);
        factory.setServerPort(4001);
        MessageBus messageBus = factory.create();
    }
}
