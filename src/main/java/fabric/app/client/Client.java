/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app.client;

import fabric.app.MessageException;
import fabric.app.MessageSender;
import fabric.serialization.*;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Fabric client app
 */
public class Client extends MessageSender {

    // Socket used for server connection
    private static Socket clientSocket;

    /**
     * Main method of Fabric client, allows user to send a single BOUT or
     * KNOWP message to the Fabric server pointed to by server and port in args
     *
     * @param args server, port, userid, password, and request
     */
    public static void main(String[] args) {
        // Validate initial user input
        if (args.length != 5 && args.length != 7) {
            System.err.println("Usage: java Client <server> <port> " +
                    "<userid> <password> <request...>");
            System.exit(1);
        } else if (!args[1].matches("[0-9]+")) {
            System.err.println("Validation Failed: invalid port number");
            System.exit(1);
        } else if ((!args[4].equals("KNOWP") && !args[4].equals("BOUT")) ||
                (args[4].equals("KNOWP") && args.length != 5) ||
                (args[4].equals("BOUT") && args.length != 7)) {
            System.err.println("Validation Failed: <request...> must be " +
                    "either 1) BOUT <category> <image...> or 2) KNOWP");
            System.exit(1);
        }

        // Declare socket connection variables
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        MessageInput in = null;
        MessageOutput out = null;

        // Initialize socket connection to server
        try {
            clientSocket = new Socket(server, port);
            in = new MessageInput(clientSocket.getInputStream());
            out = new MessageOutput(clientSocket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Unable to communicate: " + ex.getMessage());
            System.exit(1);
        }

        // general message holder
        Message m;

        // Read initial server message (FABRIC with version)
        System.out.println(receiveMessage(in, Fabric.class));

        // Send userID (ID)
        sendID(out, args[2]);

        // Read next server message (CLNG with nonce)
        m = receiveMessage(in, Challenge.class);
        System.out.println(m);

        // Send hash of password and nonce (CRED)
        sendCred(out, args[3], ((Challenge)m).getNonce());

        // Read next server message (ACK)
        System.out.println(receiveMessage(in, Ack.class));

        // Send request (BOUT or KNOWP)
        if (args.length == 5) {
            sendKnowp(out);
        } else {
            sendBout(out, args[5], args[6]);
        }

        // Read next server message (ACK)
        System.out.println(receiveMessage(in, Ack.class));

        // Close socket
        closeSocket(0);
    }

    /**
     * Closes client side of socket and exits if necessary. This method only
     * returns if status is 0 and socket is closed successfully.
     *
     * @param status exit status, or 0 if program should terminate naturally
     */
    private static void closeSocket (int status) {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            System.err.println("Unable to communicate: " + ex.getMessage());
            status = 1;
        }
        if (status != 0) {
            System.exit(status);
        }
    }

    /**
     * Receives a message from the server
     *
     * @param in MessageInput object from which to decode
     * @param type expected message type
     * @return the message received
     */
    private static Message receiveMessage(MessageInput in,
                                          Class<? extends Message> type) {
        Message m = null;
        // Try to read message until successful or program is terminated
        // by closeSocket
        while (m == null) {
            // Read next available message
            try {
                m = readMessage(in);
            } catch (MessageException ex) {
                if (ex.getError() == null) {
                    System.err.println(ex.getMessage());
                } else {
                    System.err.println("Error: " + ex.getMessage());
                }
                closeSocket(1);
            }

            // Check message type if message successfully read
            try {
                checkMessageType(m, type);
            } catch (MessageException ex) {
                System.err.println(ex.getMessage());
                m = null;
            }
        }
        return m;
    }

    /**
     * Writes a message to the server, closing the socket and terminating if
     * any errors occur.
     *
     * @param out MessageOutput to which Message is encoded
     * @param message Message to be sent to server
     */
    private static void sendMessage(MessageOutput out, Message message) {
        try {
            writeMessage(out, message);
        } catch (MessageException ex) {
            System.err.println(ex.getMessage());
            closeSocket(1);
        }
    }

    /**
     * Attempts to create an ID message and send it to the server. If an error
     * occurs, the socket is closed and the client terminated.
     *
     * @param out MessageOutput to which Message is encoded
     * @param ID User ID to be sent to the server
     */
    private static void sendID(MessageOutput out, String ID) {
        ID id = null;

        // Send ID to server
        try {
            id = new ID(ID);
        } catch (ValidationException ex) {
            System.err.println("Validation failed: " + ex.getMessage());
            closeSocket(1);
        }

        // closeSocket doesn't return, id is not null
        sendMessage(out, id);
    }

    /**
     * Attempts to create a Credentials message and send it to the server. If
     * an error occurs, the socket is closed and the client terminated.
     *
     * @param out MessageOutput to which Message is encoded
     * @param password User password to be hashed and sent to server
     * @param nonce nonce sent by server to be used for hash generation
     */
    private static void sendCred(MessageOutput out,
                                 String password, String nonce) {
        Credentials cred = null;
        MessageDigest md = null;

        // Get object for nonce-password MD5 hash generation
        try {
            md = MessageDigest.getInstance(HASH);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Error: hash failed: " + ex.getMessage());
            closeSocket(1);
        }

        // Generate hash (no null pointer since program terminates in catch)
        md.update((nonce + password).getBytes(CHARSET));

        // Send CRED to server
        try {
            cred = new Credentials(HexFormat.of().withUpperCase().
                    formatHex(md.digest()));
        } catch (ValidationException ex) {
            System.err.println("Validation failed: " + ex.getMessage());
            closeSocket(1);
        }

        // closeSocket doesn't return, cred is not null
        sendMessage(out, cred);
    }

    /**
     * Attempts to create a Knowp message and send it to the server.
     *
     * @param out MessageOutput to which Message is encoded
     */
    private static void sendKnowp(MessageOutput out) {
        Knowp knowp = new Knowp();

        // closeSocket doesn't return, id is not null
        sendMessage(out, knowp);
    }

    /**
     * Attempts to create a Bout message and send it to the server. If an error
     * occurs, the socket is closed and the client terminated.
     *
     * @param out MessageOutput to which Message is encoded
     * @param category Category to be included in Bout
     * @param filename Image to be encoded to Bout
     */
    private static void sendBout(MessageOutput out,
                                 String category, String filename) {
        Bout bout = null;
        Path path = Paths.get(filename);

        // create BOUT with image name provided on command line
        try {
            bout = new Bout(category,
                    Files.readAllBytes(path.toAbsolutePath()));
        } catch (ValidationException | IOException ex) {
            System.err.println("Validation failed: " + ex.getMessage());
            closeSocket(1);
        }

        // closeSocket doesn't return, bout is not null
        sendMessage(out, bout);
    }

    /**
     * Default constructor, unused (main class)
     */
    private Client() {}
}
