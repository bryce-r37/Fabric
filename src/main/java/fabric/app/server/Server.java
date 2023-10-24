/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app.server;

import fabric.app.MessageException;
import fabric.app.MessageSender;
import fabric.serialization.*;
import fabric.serialization.Error;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
 * Fabric server app
 */
public class Server extends MessageSender {

    /**
     * Logger for Fabric server events
     */
    private static final Logger logger = Logger.getLogger("fabricServer");

    /**
     * Storage structure for uIDs, passwords, and seq numbers
     */
    private static Users users;

    /**
     * Writer structure for writing client messages to html
     */
    private static Y y;

    /**
     * Random number generator for nonce generation
     */
    private static final Random nonceGen = new Random(new Date().getTime());

    /**
     * Duration (in seconds) to block on socket reads
     */
    private static final int TIMEOUT = 40;

    /**
     * Error code for authentication errors
     */
    private static final int AUTHERR = 500;

    /**
     * Error code for posting errors
     */
    private static final int POSTERR = 600;

    /**
     * Name of file where Fabric messages are written
     */
    private static final String OUTPUT = "y.html";

    /**
     * Name of file where Fabric logs are written
     */
    private static final String SERVERLOGS = "server.log";

    // Logger initialization before main method begins execution
    static {
        logger.setLevel(Level.FINE);
        try {
            FileHandler file = new FileHandler(SERVERLOGS, true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.setUseParentHandlers(false);
        } catch (IOException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Main method of Fabric server
     *
     * @param args server port, thread pool size, and password file
     */
    public static void main(String[] args) {
        // Argument checks
        if (args.length != 3 || !args[0].matches("^[0-9]+$") ||
                !args[1].matches("^[0-9]+$")) {
            logger.severe(() -> "Unable to start: Usage: java Server <port> " +
                    "<thread pool size> <password filename>");
            System.exit(1);
        }
        if (Integer.parseInt(args[1]) < 1) {
            logger.severe(() -> "Unable to start: Invalid number of threads " +
                    Integer.parseInt(args[1]));
            System.exit(1);
        }

        // Password file read
        try {
            users = new Users(args[2]);
        } catch (IOException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }

        // Writer opening
        try {
            y = new Y(OUTPUT);
        } catch (FileNotFoundException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }

        // Open server socket and wait for client connections
        try ( ServerSocket serverSocket = new ServerSocket(
                    Integer.parseInt(args[0]));
                ExecutorService threads = Executors
                    .newFixedThreadPool(Integer.parseInt(args[1])) ) {
            do {
                try {
                    // Accept new client
                    Socket client = serverSocket.accept();
                    logger.info(() -> "New client: " +
                            client.getInetAddress().getHostAddress() + ":" +
                            client.getPort());
                    // Delegate client to thread
                    threads.submit(() -> run(client));
                } catch (IOException ex) {
                    logger.warning(() -> "Unable to communicate: " +
                            ex.getMessage());
                }
            } while (true);
        } catch (IOException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Main method for server threads to accept and send messages
     *
     * @param client Socket hosting client connection
     */
    private static void run(Socket client) {
        MessageInput in;
        MessageOutput out;
        try {
            in = new MessageInput(client.getInputStream());
            out = new MessageOutput(client.getOutputStream());
            client.setSoTimeout(TIMEOUT * 1000);
        } catch (IOException ex) {
            logger.warning(() -> "Unable to communicate: " + ex.getMessage());
            closeClient(client, false);
            return;
        }

        // declare general message holder and client socket address
        Message m;
        String addr = client.getInetAddress().getHostAddress() + ":" +
                client.getPort();

        // Send initial Fabric message with version to client
        logger.fine(() -> "Sending Ack to client at " + addr);
        if (!sendFabric(out, client)) {
            return;
        }

        // Read next client message (ID with userID expected)
        if ((m = receiveMessage(in, client, ID.class)) == null) {
            return;
        }
        logger.fine(() -> "Received Id from client at " + addr);

        // Check if userID is valid, send error message if not
        String userID = ((ID) m).getID();
        if (!users.contains(userID)) {
            sendServerError(out, AUTHERR, "No such user " + userID, client);
            return;
        }

        // Send Challenge message with random nonce to client
        String nonce = String.valueOf(nonceGen.nextInt(Integer.MAX_VALUE));
        logger.fine(() -> "Sending Challenge to client at " + addr);
        if (!sendChallenge(out, nonce, client)) {
            return;
        }

        // Read next client message (Credentials with hash expected)
        if ((m = receiveMessage(in, client, Credentials.class)) == null) {
            return;
        }
        logger.fine(() -> "Received Credentials from client at " + addr);

        // Validate hash
        String hash = ((Credentials) m).getHash();
        if (hash == null || !hash.equals(users.hash(nonce, userID))) {
            sendServerError(out, AUTHERR, "Unable to authenticate", client);
            return;
        }
        logger.fine(() -> "Authenticated client at " + addr);

        // Send Ack message to client
        logger.fine(() -> "Sending Ack to client at " + addr);
        if (!sendAck(out, client)) {
            return;
        }

        // Read next client message (Bout with category and image
        // or Knowp expected)
        if ((m = receiveMessage(in, client, Bout.class,
                Knowp.class)) == null) {
            return;
        }
        String op = m.getClass().getSimpleName();
        logger.fine(() -> "Recevied " + op + " from client at " + addr);

        // Process received Bout or Knowp
        try {
            postMessage(m, userID);
            logger.fine(() -> "Posted message from client at " + addr);
        } catch (IOException ex) {
            sendServerError(out, POSTERR, "Could not post message", client);
            return;
        }

        // Send Ack message to client
        logger.fine(() -> "Sending Ack to client at " + addr);
        if (!sendAck(out, client)) {
            return;
        }

        // Close client connection gracefully
        closeClient(client, true);
    }

    /**
     * Closes client connection
     *
     * @param client socket to be closed
     * @param graceful whether to close the socket gracefully (true) or
     *                 forcefully (false)
     */
    private static void closeClient(Socket client, boolean graceful) {
        logger.info(() -> "Closing connection to client at " +
                client.getInetAddress().getHostAddress() + ":" +
                client.getPort());
        try {
            // set linger time to force closure on error
            client.setSoLinger(true, graceful ? TIMEOUT : 0);
        } catch (SocketException ex) {
            logger.fine(() -> "Socket error: could not set linger time");
        }
        try {
            // close gracefully if linger set to TIMEOUT or
            // forcefully if set to 0
            client.close();
        } catch (IOException ex) {
            logger.warning(() -> "Unable to communicate: " + ex.getMessage());
        }
    }

    /**
     * Receives a message from the client
     *
     * @param in MessageInput object from which to decode
     * @param client client Socket to be closed if error occurs
     * @param a array of expected message types
     * @return the message received
     */
    private static Message receiveMessage(MessageInput in,
                                          Socket client,
                                          Class<? extends Message>... a) {
        Message m = null;
        // Try to read message until successful or connection is terminated
        // by closeClient
        while (m == null) {
            // Read next available message
            try {
                m = readMessage(in);
            } catch (MessageException ex) {
                if (ex.getError() == null) {
                    logger.warning(ex::getMessage);
                } else {
                    logger.warning(() -> "Received error: " + ex.getMessage());
                }
                closeClient(client, false);
                break;
            }

            // Check message type if message successfully read
            try {
                checkMessageType(m, a);
            } catch (MessageException ex) {
                logger.warning(ex::getMessage);
                m = null;
            }
        }
        return m;
    }

    /**
     * Uses y to post a user Bout or Knwop to the html output file
     *
     * @param m Bout or Knowp received to be posted
     * @param userID id of user who sent m
     * @throws IOException if I/O problem in post
     */
    private static synchronized void postMessage(Message m, String userID) throws IOException {
        if (m instanceof fabric.serialization.Knowp) {
            // update user sequence number and write Knowp to output
            users.update(userID);
            y.update(userID + ": KNOWP " +
                    users.getSequence(userID));
        } else {
            // write Bout to output
            y.updateWithImage(userID + ": BOUT #" +
                    ((Bout) m).getCategory(), ((Bout) m).getImage());
        }
    }

    /**
     * Writes a message to the client, closing the socket and terminating if
     * any errors occur.
     *
     * @param out MessageOutput to which Message is encoded
     * @param message Message to be sent to client
     * @param client client Socket to be closed if error occurs
     */
    private static boolean sendMessage(MessageOutput out, Message message,
                                       Socket client) {
        try {
            writeMessage(out, message);
        } catch (MessageException ex) {
            logger.warning(ex::getMessage);
            closeClient(client, false);
            return false;
        }
        return true;
    }

    /**
     * Sends Fabric message to client
     *
     * @param out MessageOutput to which Message is encoded
     * @param client client Socket to be closed if error occurs
     */
    private static boolean sendFabric(MessageOutput out, Socket client) {
        Fabric fabric = new Fabric();

        return sendMessage(out, fabric, client);
    }

    /**
     * Sends Challenge message to client
     *
     * @param out MessageOutput to which Message is encoded
     * @param nonce random nonce for client to use in credentials hash
     * @param client client Socket to be closed if error occurs
     */
    private static boolean sendChallenge(MessageOutput out, String nonce,
                                         Socket client) {
        Challenge challenge;

        // Create Challenge to be sent to client
        try {
            challenge = new Challenge(nonce);
        } catch (ValidationException ex) {
            logger.warning(() -> "Validation failed: " + ex.getMessage());
            return false;
        }

        // attempt to send Challenge to client
        return sendMessage(out, challenge, client);
    }

    /**
     * Sends Ack message to client
     *
     * @param out MessageOutput to which Message is encoded
     * @param client client Socket to be closed if error occurs
     */
    private static boolean sendAck(MessageOutput out, Socket client) {
        Ack ack = new Ack();

        return sendMessage(out, ack, client);
    }

    /**
     * Sends Error message to client
     *
     * @param out MessageOutput to which Message is encoded
     * @param code error code
     * @param message error message
     * @param client client Socket to be closed if error occurs
     */
    private static void sendServerError(MessageOutput out, int code,
                                           String message, Socket client) {
        Error error = null;
        // Create Error to be sent to client
        try {
            error = new Error(code, message);
        } catch (ValidationException ex) {
            logger.warning(() -> "Validation failed: " + ex.getMessage());
        }

        // log Error if successfully created
        if (error != null) {
            logger.severe(error::toString);
        }

        // attempt to send Error to client
        sendMessage(out, error, client);
        closeClient(client, false);
    }

    /**
     * Default constructor, unused (main class)
     */
    private Server() {}
}
