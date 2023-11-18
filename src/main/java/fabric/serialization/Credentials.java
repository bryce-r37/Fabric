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
 * Represents a credentials and provides serialization/deserialization
 */
public class Credentials extends Message {

    /**
     * Operation ID for Credentials messages
     */
    private static final String CREDOP = "CRED";

    /**
     * Credentials hash
     */
    private String hash;

    /**
     * Constructs Credentials message
     *
     * @param hash credentials hash
     * @throws ValidationException if hash is null or invalid
     */
    public Credentials(String hash) throws ValidationException {
        this.hash = validateHash(hash);
    }

    /**
     * Constructs Credentials from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Credentials(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Validate Credentials and hash
        this.hash = validateHash(msg);
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Credentials: hash=" + this.hash;
    }

    /**
     * Returns hash
     *
     * @return hash
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * Sets hash
     *
     * @param hash new hash
     * @return this object with new hash
     * @throws ValidationException if null or invalid hash
     */
    public Credentials setHash(String hash) throws ValidationException {
        this.hash = validateHash(hash);
        return this;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return CREDOP;
    }

    /**
     * Encodes Credentials message to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(CREDOP, this.hash);
    }

    /**
     * Returns whether a Credentials is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Credentials are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(this.hash,
                ((Credentials) o).hash);
    }

    /**
     * Returns an integer hash of a Credentials message for use in Collections
     *
     * @return an integer hash representing the Credentials message
     */
    @Override
    public int hashCode() {
        return Objects.hash(CREDOP, this.hash);
    }

    /**
     * Validates that hash is 16 hex values
     *
     * @param hash credentials hash
     * @return hash
     */
    private static String validateHash(String hash) throws ValidationException {
        if (hash == null || !hash.matches("[0-9A-F]{32}")) {
            throw new ValidationException("Invalid hash", hash);
        }
        return hash;
    }
}
