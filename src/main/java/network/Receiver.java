package network;

import mf.MessageBusController;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Receiver class is an abstraction for a server socket.
 * It is used for receiving messages from peers.
 * Although multiple Receivers can be created it recommended to only use one Receiver inside the messaging framework.
 *
 * @author Tobias Haider
 */
public class Receiver {
    private final int serverPort;
    private final MessageBusController messageBusController;
    private final ThreadPoolExecutor threads;
    private boolean running;

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    /**
     * Constructor for Receiver. Delivers an abstraction for a server socket.
     *
     * @param serverPort           Server port to which the receiver should listen
     * @param messageBusController Message bus object to which received message should be passed on
     */
    public Receiver(int serverPort, MessageBusController messageBusController) {
        this.serverPort = serverPort;
        this.messageBusController = messageBusController;
        this.threads = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        running = false;
    }

    /**
     * Method used to start the receiver and therefore start listening to incoming messages .
     *
     * @throws IOException Throws an exception, if the server socket could not be instantiated
     */
    public void start() throws IOException {
        running = true;
        ServerSocket serverSocket = new ServerSocket(serverPort);
        logger.debug("Receiver is now listening to incoming messages at port " + serverPort + ".");
        new Thread(() -> {
            Socket socket = null;
            while (running) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                threads.submit(new SocketHandler(socket, messageBusController));
            }
            try {
                serverSocket.close();
                logger.debug("Receiver stopped listening to incoming messages.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Method used to stop the receiver and therefore stop listening to incoming messages .
     */
    public void stopReceiving() {
        running = false;
    }

    /**
     * Internal runnable implementation used for processing multiple incoming messages simultaneously.
     */
    private static class SocketHandler implements Runnable {
        private final Socket socket;
        private final MessageBusController messageBusController;

        /**
         * Constructor for MessageDeliverer.
         *
         * @param socket               Socket which shall be dealt with
         * @param messageBusController Controller where the message can be forwarded
         */
        SocketHandler(Socket socket, MessageBusController messageBusController) {
            this.socket = socket;
            this.messageBusController = messageBusController;
        }

        /**
         * Task of the runnable. Read information from socket and pass it to the message bus com.controller.
         */
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                byte[] bytes = in.readAllBytes();
                String message = new String(bytes, StandardCharsets.UTF_8);
                logger.debug("Received message from " + socket.getInetAddress() + ". Forwarding to messaging bus.");
                messageBusController.processMessage(message);

                // Close Stream and sockets
                socket.close();
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
