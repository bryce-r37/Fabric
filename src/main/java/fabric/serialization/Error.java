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
 * Represents an error and provides serialization/deserialization
 */
public class Error extends Message {

    /**
     * Operation ID for Error messages
     */
    private static final String ERROP = "ERROR";

    /**
     * 3-digit code from 100 to 999 representing the error cause
     */
    private int code;

    /**
     * String holding the message for the Error
     */
    private String message;

    /**
     * Minimum value for an Error code
     */
    private static final int MINCODE = 100;

    /**
     * Maximum value for an Error code
     */
    private static final int MAXCODE = 999;

    /**
     * Constructs error message using set values
     *
     * @param code error code
     * @param message error message
     * @throws ValidationException if null or not 1+ of either alphanum or sp
     */
    public Error(int code, String message) throws ValidationException {
        this.code = validateCode(code);
        this.message = validateMessage(message);
    }

    /**
     * Constructs error message from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Error(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Check that error code can be parsed as a sp terminated int
        String code = msg.substring(0, msg.indexOf(Message.DELIM));
        if (!code.matches("^[0-9]+$" )) {
            throw new ValidationException("Bad code in Error", code);
        }
        this.code = validateCode(Integer.parseInt(code));

        // Check that message has correct terminating sequence
        msg = msg.substring(msg.indexOf(Message.DELIM) + 1);
        this.message = validateMessage(msg);
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Error: code=" + this.code + " message=" + this.message;
    }

    /**
     * Returns code
     *
     * @return code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Sets code
     *
     * @param code new code
     * @return the Error with the new code
     * @throws ValidationException if invalid code
     */
    public Error setCode(int code) throws ValidationException {
        this.code = validateCode(code);
        return this;
    }

    /**
     * Returns message
     *
     * @return message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets message
     *
     * @param message new message
     * @return the Error with the new message
     * @throws ValidationException if invalid message, including null
     */
    public Error setMessage(String message) throws ValidationException {
        this.message = validateMessage(message);
        return this;
    }

    /**
     * Encodes Error to output
     *
     * @param out message output object
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(this.getOperation(),
                Integer.toString(this.code), this.message);
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    public String getOperation() {
        return ERROP;
    }

    /**
     * Returns whether an Error is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Error are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.code == ((Error) o).code &&
                Objects.equals(this.message, ((Error) o).message);
    }

    /**
     * Returns an integer hash of an Ack for use in Collections
     *
     * @return an integer hash representing the Ack
     */
    @Override
    public int hashCode() {
        return Objects.hash(ERROP, this.code, this.message);
    }

    /**
     * Validates that code is between 100 and 999
     *
     * @param code int to be validated
     * @throws ValidationException if code is invalid
     */
    private static int validateCode(int code) throws ValidationException {
        if (code < MINCODE || code > MAXCODE) {
            throw new ValidationException("Invalid error code", Integer.toString(code));
        }
        return code;
    }

    /**
     * Validates that message is 1+ of alphanumeric characters or spaces
     *
     * @param message String to be validated
     * @return message
     * @throws ValidationException if message is invalid
     */
    private static String validateMessage(String message) throws ValidationException {
        // Validate that message is 1+ of alphanum or sp
        if (message == null || !message.matches("^[\\w ]+$")) {
            throw new ValidationException("Invalid message", message);
        }
        return message;
    }
}
