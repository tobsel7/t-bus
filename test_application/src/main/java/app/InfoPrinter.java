package app;

public class InfoPrinter {
    public static void printWelcome() {
        System.out.println("Welcome. You successfully set up the messaging framework.");
        System.out.println("Now you can proceed to send messages to other participants running this tool or a microservice.");
        System.out.println("Type help for additional information.");
    }

    public static void printHelp() {
        System.out.println("Following commands are possible:");
        System.out.println("add <name> <port> -> This will add an outgoing connection to localhost and the given port.");
        System.out.println("add <name> <ip> <port> -> This will add an outgoing connection.");
        System.out.println("send <message> -> This will send your string message to anybody.");
        System.out.println("send <receiver> <message> -> This will send your string message to the specified receiver.");
        System.out.println("help <messagetype> -> This will display information on how to send this specific message type.");
    }

    public static void printHelp(String messageType) {
        if(messageType.equals("ContainerLocationUpdate")) {
            System.out.println("send ContainerLocationUpdate <containerId> <position> <state> <timestamp> -> This will send your ContainerLocationUpdate to anybody.");
            System.out.println("send ContainerLocationUpdate <receiverId> <containerId> <position> <state> <timestamp> -> This will send your ContainerLocationUpdate to the specified receiver.");
            return;
        }
        if(messageType.equals("ConnectionInformation")) {
            System.out.println("send ConnectionInformation <name> <ip> <port> -> This will send your ConnectionInformation to anybody.");
            System.out.println("send ConnectionInformation <receiverId> <name> <ip> <port> -> This will send your ConnectionInformation to the specified receiver.");
            return;
        }
        if(messageType.equals("ConnectionInformationRequest")) {
            System.out.println("send ConnectionInformationRequest <name> -> This will send your ConnectionInformationRequest to anybody.");
            System.out.println("send ConnectionInformationRequest <receiverId> <name> -> This will send your ConnectionInformationRequest to the specified receiver.");
            return;
        }
        System.out.println("Sorry. this message type is not implemented in the integration test tool.");
    }
}
