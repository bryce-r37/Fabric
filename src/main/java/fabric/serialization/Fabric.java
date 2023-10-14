/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a fabric message and provides serialization/deserialization
 */
public class Fabric extends Message {

    /**
     * Operation ID for Fabric messages
     */
    private static final String FABRICOP = "FABRIC";

    private static final String VERSION = "1.0";

    /**
     * Constructs Fabric message
     */
    public Fabric() {}

    /**
     * Constructs Fabric from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Fabric(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Check that message has correct version
        if (!msg.equals(VERSION)) {
            throw new ValidationException("Invalid version", msg);
        }
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Fabric " + VERSION;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return FABRICOP;
    }

    /**
     * Encodes Fabric message to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(FABRICOP, VERSION);
    }

    /**
     * Returns whether a Fabric is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Fabric are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * Returns an integer hash of a Fabric message for use in Collections
     *
     * @return an integer hash representing the Fabric message
     */
    @Override
    public int hashCode() {
        return Objects.hash(FABRICOP, VERSION);
    }
}
