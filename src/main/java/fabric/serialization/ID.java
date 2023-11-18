/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents an ID and provides serialization/deserialization
 */
public class ID extends Message {

    /**
     * Operation ID for ID messages
     */
    private static final String IDOP = "ID";

    /**
     * ID for ID messages
     */
    private String ID;

    /**
     * Constructs ID message
     *
     * @param ID ID id
     * @throws ValidationException if ID is null or invalid
     */
    public ID(String ID) throws ValidationException {
        this.ID = validateID(ID);
    }

    /**
     * Constructs Credentials from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected ID(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Validate id
        this.ID = validateID(msg);
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "ID: id=" + ID;
    }

    /**
     * Returns ID
     *
     * @return ID
     */
    public String getID() {
        return this.ID;
    }

    /**
     * Sets new ID
     *
     * @param ID new ID
     * @return this object with new ID
     * @throws ValidationException if null or invalid ID
     */
    public ID setID(String ID) throws ValidationException {
        this.ID = validateID(ID);
        return this;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return IDOP;
    }

    /**
     * Encodes ID to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(IDOP, this.ID);
    }

    /**
     * Returns whether an ID is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and ID are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(this.ID,
                ((ID) o).ID);
    }

    /**
     * Returns an integer hash of an ID for use in Collections
     *
     * @return an integer hash representing the ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(IDOP, this.ID);
    }

    /**
     * Validates that ID is 1+ alphanum
     *
     * @param ID credentials ID
     * @return ID
     */
    private static String validateID(String ID) throws ValidationException {
        if (ID == null || !ID.matches("[\\w]+")) {
            throw new ValidationException("Invalid ID", ID);
        }
        return ID;
    }
}
