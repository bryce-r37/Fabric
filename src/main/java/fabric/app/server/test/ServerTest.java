/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app.server.test;

import fabric.serialization.*;
import fabric.serialization.Error;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for server tests
 */
class ServerTest {

    private static final String SERVER = "localhost";
    private static final int PORT = 12345;
    private static Socket clientSocket;
    private static MessageInput in;
    private static MessageOutput out;
    private static final String BADID = "robinson";
    private static final String ID = "test";
    private static final String BADPASS = "test";
    private static Error IDERROR;
    private static Error PASSERROR;
    private static MessageDigest md;

    static {
        try {
            IDERROR = new Error(500, "No such user robinson");
            PASSERROR = new Error(500, "Unable to authenticate");
            md = MessageDigest.getInstance("MD5");
        } catch (ValidationException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Opens new client socket and establishes MessageInput and MessageOutput
     * objects for communication
     */
    @BeforeEach
    void beforeEach() {
        String server = SERVER;
        int port = PORT;

        // Initialize socket connection to server
        try {
            clientSocket = new Socket(server, port);
            in = new MessageInput(clientSocket.getInputStream());
            out = new MessageOutput(clientSocket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Unable to communicate: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Close client socket after each test
     */
    @AfterEach
    void afterEach() {
        try {
            clientSocket.close();
        } catch (IOException ex) {}
    }

    /**
     * Test server timeout while waiting for client message
     *
     * @throws ValidationException if parse or validation problem
     * @throws IOException if I/O problem
     * @throws InterruptedException if sleep is interrupted
     */
    @Test
    void timeoutTest() throws ValidationException, IOException, InterruptedException {
        Message.decode(in);

        sleep(50000);
        assertThrows(SocketException.class, () -> clientSocket.getInputStream().read());
    }

    /**
     * Tests socket close after unexpected message
     *
     * @throws ValidationException if parse or validation problem
     * @throws IOException if I/O problem
     * @throws InterruptedException if sleep is interrupted
     */
    @Test
    void unexpectedMessageTest() throws ValidationException, IOException, InterruptedException {
        Message.decode(in);
        Message m = new Ack();
        m.encode(out);
        sleep(3000);
        assertThrows(SocketException.class, () -> clientSocket.getInputStream().read());
    }

    /**
     * Tests Error response to unknown ID
     *
     * @throws ValidationException if parse or validation problem
     * @throws IOException if I/O problem
     */
    @Test
    void unknownIDTest() throws ValidationException, IOException {
        Message.decode(in);
        Message m = new ID(BADID);
        m.encode(out);
        m = Message.decode(in);
        assertEquals(m, IDERROR);
    }

    /**
     * Tests Error response to incorrect password hash
     *
     * @throws ValidationException if parse or validation problem
     * @throws IOException if I/O problem
     */
    @Test
    void invalidPasswordTest() throws ValidationException, IOException {
        Message.decode(in);
        Message m = new ID(ID);
        m.encode(out);
        m = Message.decode(in);
        md.update((((Challenge) m).getNonce() + BADPASS)
                .getBytes(StandardCharsets.ISO_8859_1));
        String hash = HexFormat.of().withUpperCase().formatHex(md.digest());
        m = new Credentials(hash);
        m.encode(out);
        m = Message.decode(in);
        assertEquals(PASSERROR, m);
    }

}
