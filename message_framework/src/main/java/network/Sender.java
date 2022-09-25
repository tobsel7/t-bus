package network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Sender class is an abstraction for a client Socket.
 * It can be used to send messages to peers.
 *
 * @author Tobias Haider
 */
public class Sender {
    private final String ip;
    private final int port;

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    /**
     * Constructor for a Sender. Delivers an abstraction for a client socket.
     *
     * @param ip   IP address of another application
     * @param port Port number of another application
     */
    Sender(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Method used for sending a message package.
     *
     * @param message Message that should be sent
     */
    void sendMessage(String message) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(message);
            out.flush();
            logger.debug("Successfully sent a message to {}:{}.", ip, port);
        } catch (IOException e) {
            logger.debug("The message could not be sent. The receiver is probably not available.", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.trace("There occurred an error while closing the sender socket.", e);
                }
            }
        }
    }

    /**
     * Getter for the IP address
     *
     * @return IP address of outgoing connection
     */
    public String getIp() {
        return ip;
    }

    /**
     * Getter for port number
     *
     * @return Port number of outgoing connection
     */
    public int getPort() {
        return port;
    }
}
