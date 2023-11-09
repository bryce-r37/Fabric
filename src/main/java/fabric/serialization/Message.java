/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents generic portion of message and provides
 * serialization/deserialization
 */
public abstract class Message {

    /**
     * Primary delimiter in messages
     */
    // package for static use by encode/decode methods
    static final char DELIM = ' ';

    /**
     * First terminating character for messages
     */
    // package for static use by encode/decode methods
    static final char TERM1 = '\r';

    /**
     * Second terminating character for messages
     */
    // package for static use by encode/decode methods
    static final char TERM2 = '\n';

    /**
     * Default constructor
     */
    // protected for subclass access
    protected Message() {}

    /**
     * Deserializes message from input source
     *
     * @param in deserialization input source
     * @return a specific message resulting from deserialization
     * @throws NullPointerException if in is null
     * @throws ValidationException if parse or validation problem
     * @throws IOException if I/O problem
     */
    public static Message decode(MessageInput in) throws NullPointerException,
            ValidationException, IOException {
        Message m;
        String op = in.readOp();

        switch (op) {
            case "ACK" + Message.TERM1 -> m = new Ack(in);
            case "BOUT" + Message.DELIM -> m = new Bout(in);
            case "CLNG" + Message.DELIM -> m = new Challenge(in);
            case "CRED" + Message.DELIM -> m = new Credentials(in);
            case "ERROR" + Message.DELIM -> m = new Error(in);
            case "FABRIC" + Message.DELIM -> m = new Fabric(in);
            case "ID" + Message.DELIM -> m = new ID(in);
            case "KNOWP" + Message.TERM1 -> m = new Knowp(in);
            default -> throw new ValidationException("Bad operation", op);
        }

        return m;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public abstract String getOperation();

    /**
     * Serializes message to given output sink
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    public abstract void encode(MessageOutput out) throws NullPointerException,
            IOException;

    /**
     *
     * @param msg input String to be validated
     * @return String with terminating sequence removed
     * @throws IOException if premature EOS
     */
    // package for static use by Message constructors
    static String validateFabricMessage(String msg) throws IOException {
        if (!msg.endsWith("" + Message.TERM1 + Message.TERM2)) {
            throw new IOException("Premature EOS");
        }
        return msg.replace("" + Message.TERM1 +
                Message.TERM2, "");
    }

    /**
     * Returns whether Object o equals Message this
     *
     * @param o second object to be compared
     * @return boolean representation of whether messages are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message message)) {
            return false;
        }
        return Objects.equals(this.getOperation(), message.getOperation());
    }

    /**
     * Returns an integer hash of a message object for use in Collections
     *
     * @return an integer hash representing the object
     */
    @Override
    public abstract int hashCode();

}
