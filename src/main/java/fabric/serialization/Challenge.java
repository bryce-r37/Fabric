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
 * Represents a Challenge and provides serialization/deserialization
 */
public class Challenge extends Message {

    /**
     * Operation ID for Challenge messages
     */
    private static final String CLNGOP = "CLNG";

    /**
     * Challenge nonce
     */
    private String nonce;

    /**
     * Constructs Challenge message
     *
     * @param nonce challenge nonce
     * @throws ValidationException if nonce is null or invalid
     */
    public Challenge(String nonce) throws ValidationException {
        this.nonce = validateNonce(nonce);
    }

    /**
     * Constructs Challenge from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Challenge(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Validate Challenge and nonce
        this.nonce = validateNonce(msg);
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Challenge: nonce=" + this.nonce;
    }

    /**
     * Returns nonce
     *
     * @return nonce
     */
    public String getNonce() {
        return this.nonce;
    }

    /**
     * Sets nonce
     *
     * @param nonce new nonce
     * @return this object with new nonce
     * @throws ValidationException if null or invalid nonce
     */
    public Challenge setNonce(String nonce) throws ValidationException {
        this.nonce = validateNonce(nonce);
        return this;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return CLNGOP;
    }

    /**
     * Encodes a Challenge to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(CLNGOP, nonce);
    }

    /**
     * Returns whether a Challenge is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Challenge are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(this.nonce,
                ((Challenge) o).nonce);
    }

    /**
     * Returns an integer hash of a Challenge message for use in Collections
     *
     * @return an integer hash representing the Challenge message
     */
    @Override
    public int hashCode() {
        return Objects.hash(CLNGOP, this.nonce);
    }

    /**
     * Validates that nonce is 1+ numeric
     *
     * @param nonce challenge nonce
     * @return nonce
     * @throws ValidationException if validation fails
     */
    private static String validateNonce(String nonce) throws ValidationException {
        if (nonce == null || !nonce.matches("[0-9]+")) {
            throw new ValidationException("Invalid nonce", nonce);
        }
        return nonce;
    }
}
