package app;

import mf.*;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Get scanner for reading command line input
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name.");
        String name = scanner.nextLine(); // Set name of this ms
        System.out.println("Enter port number (usually some number above 4000) to which you want to listen for messages.");
        int serverPort = scanner.nextInt(); // Set the port of this ms

       try {
           // Configure message bus
           MessageBus messageBus = createMessageBus(name, serverPort);
           // Subscribe to message types
           listenToEverything(messageBus);

           // Create and start cli application
           TestApplication application = new TestApplication(name, messageBus);
           application.run();
       } catch (IOException e) {
           System.out.println("Something went wrong creating the messaging framework. Please use other parameters.");
           e.printStackTrace();
       }
    }

    private static MessageBus createMessageBus(String name, int serverPort) throws IOException {
        // Create a message bus using the factory
        MessageBusFactory factory = new MessageBusFactory();
        factory.setIdentifier(name);
        factory.setServerPort(serverPort);
        return factory.create();
    }

    private static void listenToEverything(MessageBus messageBus) {
        // Default test message type
        messageBus.addMessageResponse(new MessageHandler<IntegrationTestMessage>(IntegrationTestMessage.class) {
            @Override
            public void receiveMessage(IntegrationTestMessage message) {
                System.out.println("Received test message: " + message.getIntegrationTestMessage());
            }
        });
    }
}
