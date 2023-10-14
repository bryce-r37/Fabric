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
 * Represents ACK and provides serialization/deserialization
 */
public class Ack extends Message {

    /**
     * Operation ID for Ack messages
     */
    private static final String ACKOP = "ACK";

    /**
     * Constructs ACK message
     */
    public Ack() {}

    /**
     * Constructs ACK message from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Ack(MessageInput in) throws ValidationException, IOException {
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
        return "Ack";
    }

    /**
     * Encodes Ack to output
     *
     * @param out message output object
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(this.getOperation());
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public String getOperation() {
        return ACKOP;
    }

    /**
     * Returns whether an Ack is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether message and Ack are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns an integer hash of an Ack for use in Collections
     *
     * @return an integer hash representing the Ack
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getOperation());
    }
}
