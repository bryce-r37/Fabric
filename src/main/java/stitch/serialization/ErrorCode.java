/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

/**
 * Allowable error codes with associated numeric values and error messages.
 */
public enum ErrorCode {

    /**
     * Indicates no error
     */
    NOERROR ("No error", 0),

    /**
     * Indicates a message with a bad version was received
     */
    BADVERSION ("Bad version", 1),

    /**
     * Indicates a message with an unexpected error code was received
     */
    UNEXPECTEDERRORCODE ("Unexpected error code", 2),

    /**
     * Indicates a message with an unexpected packet type was received
     */
    UNEXPECTEDPACKETTYPE ("Unexpected packet type", 3),

    /**
     * Indicates a message with extraneous trailing bytes was received
     */
    PACKETTOOLONG ("Packet too long", 4),

    /**
     * Indicates a message with insufficient bytes was received
     */
    PACKETTOOSHORT ("Packet too short", 5),

    /**
     * Indicates some network error occurred
     */
    NETWORKERROR ("Network error", 7),

    /**
     * Indicates a validation error occurred
     */
    VALIDATIONERROR ("Validation error", 8);


    /**
     * Message associated with enumerated ErrorCode
     */
    private final String message;

    /**
     * Value associated with enumerated ErrorCode
     */
    private final int code;

    /**
     * Constructs ErrorCode constants
     *
     * @param message error message
     * @param code error code
     */
    private ErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    /**
     * Get the error value
     *
     * @return the value associated with the error code
     */
    public int getErrorCodeValue() {
        return this.code;
    }

    /**
     * Get the error message
     *
     * @return the message associated with the error code
     */
    public String getErrorMessage() {
        return this.message;
    }

    /**
     * Get the error code associated with the given error value
     *
     * @param errorCodeValue error value
     * @return error code associated with given value
     * @throws IllegalArgumentException if error value is out of range
     */
    public static ErrorCode getErrorCode(int errorCodeValue)
            throws IllegalArgumentException {
        ErrorCode val;
        switch (errorCodeValue) {
            case 0 -> val = NOERROR;
            case 1 -> val = BADVERSION;
            case 2 -> val = UNEXPECTEDERRORCODE;
            case 3 -> val = UNEXPECTEDPACKETTYPE;
            case 4 -> val = PACKETTOOLONG;
            case 5 -> val = PACKETTOOSHORT;
            case 7 -> val = NETWORKERROR;
            case 8 -> val = VALIDATIONERROR;
            default -> throw new IllegalArgumentException("Error value out of range");
        }
        return val;
    }
}
