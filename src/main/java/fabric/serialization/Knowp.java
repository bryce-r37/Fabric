/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 3
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a Knowp message and provides serialization/deserialization
 */
public class Knowp extends Message {

    /**
     * Operation ID for Knowp messages
     */
    private static final String KNOWPOP = "KNOWP";

    /**
     * Constructs Knowp message
     */
    public Knowp() {}

    /**
     * Constructs Knowp message from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Knowp(MessageInput in) throws ValidationException, IOException {
        String msg = in.readLine();
        if (!Objects.equals(msg, "" + Message.TERM2)) {
            throw new ValidationException("Bad message", msg);
        }
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Knowp";
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return KNOWPOP;
    }

    /**
     * Encodes Knowp to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(KNOWPOP);
    }

    /**
     * Returns whether a Knowp is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Knowp are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns an integer hash of a Knowp for use in Collections
     *
     * @return an integer hash representing the Knowp
     */
    @Override
    public int hashCode() {
        return Objects.hash(KNOWPOP);
    }
}
