/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app;

import fabric.serialization.Message;
import fabric.serialization.MessageInput;
import fabric.serialization.MessageOutput;
import fabric.serialization.ValidationException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents generic portion of server/client and provides methods
 * for generic message sending, receiving, and validation
 */
public abstract class MessageSender {

    /**
     * Hashing method for Credentials messages
     */
    public static final String HASH = "MD5";

    /**
     * Charset used for hash encoding in Credentials messages
     */
    public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    /**
     * Default constructor
     */
    protected MessageSender() {}

    /**
     * Checks a Message to confirm that it has the expected Message type.
     *
     * @param m Message to be checked
     * @param a array of expected message types
     * @throws MessageException if unexpected message type
     */
    protected static void checkMessageType(Message m,
                                            Class<? extends Message>... a)
            throws MessageException {
        for (Class<? extends Message> c : a) {
            if (m.getClass() == c) {
                return;
            }
        }
        throw new MessageException("Unexpected message: " + m, m);
    }

    /**
     * Attempts to encode the given Message to the output. If an error occurs,
     * a MessageException is thrown with the appropriate message and cause.
     *
     * @param out MessageOutput to which m is encoded
     * @param m Message to be encoded
     * @throws MessageException if I/O problem
     */
    protected static void writeMessage(MessageOutput out, Message m) throws MessageException {
        try {
            m.encode(out);
        } catch (IOException ex) {
            throw new MessageException("Unable to communicate: " +
                    m.getOperation() + " encoding error: " +
                    ex.getMessage(), ex);
        }
    }

    /**
     * Attempts to read the next message from input. If an error occurs, the
     * socket is closed and the client terminated.
     *
     * @param in MessageInput object from which to decode
     * @return the message read from in
     * @throws MessageException if message decoding error or error
     * received from server
     */
    protected static Message readMessage(MessageInput in)
            throws MessageException {
        Message m;

        // Decode message from input
        try {
            m = Message.decode(in);
        } catch (ValidationException ex) {
            throw new MessageException("Invalid message: " +
                    ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new MessageException("Unable to communicate: " +
                    ex.getMessage(), ex);
        }

        // Check if message is ERROR and throw exception if so
        if (m instanceof fabric.serialization.Error) {
            throw new MessageException(m.toString(), m);
        }

        return m;
    }


}