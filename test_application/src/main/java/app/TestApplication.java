package app;

import mf.MessageBus;

import java.util.Arrays;
import java.util.Scanner;

public class TestApplication {

    private final MessageBus messageBus;
    private final String name;

    public TestApplication(String name, MessageBus messageBus) {
        this.name = name;
        this.messageBus = messageBus;
    }

    public void run() {
        InfoPrinter.printWelcome();
        Scanner scanner = new Scanner(System.in);
        String[] command;
        while(true) {
            try {
                command = scanner.nextLine().split(" ");
                if (command.length > 0) {
                    handleCommand(command[0], Arrays.copyOfRange(command,1, command.length));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCommand(String command, String[] parameters) {
        switch(command) {
            case "help": {
                if (parameters.length > 0) {
                    InfoPrinter.printHelp(parameters[1]);
                } else {
                    InfoPrinter.printHelp();
                }
                return;
            }
            case "add": {
                if(parameters.length > 1) {
                    if (parameters.length > 2) {
                        addConnection(parameters[0], parameters[1], parameters[2]);
                    } else {
                        addConnection(parameters[0], parameters[1]);
                    }
                    return;
                }
            }
            case "send": {
                if (parameters.length > 0) {
                    send(parameters);
                    return;
                }
            }
            default: System.out.println("The command could not be interpreted.");
        }
    }

    private void addConnection(String name, String ip, String port) {
        try {
            messageBus.addConnection(name, ip, Integer.valueOf(port));
            System.out.println("Added connection " + name + "(" + ip + ":" + port + ") to outgoing connections.");
        } catch(Exception e) {
            System.out.println("The port must be an integer");
            e.printStackTrace();
        }
    }

    private void addConnection(String name, String port) {
        addConnection(name, "127.0.0.1", port);
    }

    private void send(String[] parameters) {
        IntegrationTestMessage message;
        if (messageBus.hasConnection(parameters[0])) {
            String messageString = String.join(" ", Arrays.copyOfRange(parameters,1, parameters.length));
            message = new IntegrationTestMessage(messageString);
            messageBus.publishMessageTo(parameters[0], IntegrationTestMessage.class, message);
        } else {
            String messageString = String.join(" ", parameters);
            message = new IntegrationTestMessage(messageString);
            messageBus.publishMessageToAny(IntegrationTestMessage.class, message);
        }
    }
}
