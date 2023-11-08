/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

import java.util.Objects;

/**
 * Exception class used for signaling failure of message creation/management
 * with an error code
 */
public class CodeException extends Exception {

    /**
     * Protocol cause of exception
     */
    private final ErrorCode errorCode;

    /**
     * Construct with the given error code.
     *
     * @param errorCode the error code
     * @throws NullPointerException if errorCode is null
     */
    public CodeException(ErrorCode errorCode) throws NullPointerException {
        this(errorCode, null);
    }

    /**
     * Construct with the given error code and cause
     *
     * @param errorCode the error code
     * @param cause the cause of the error (null if no cause)
     * @throws NullPointerException if errorCode is null
     */
    public CodeException(ErrorCode errorCode, Throwable cause)
            throws NullPointerException {
        super(Objects.requireNonNull(errorCode).getErrorMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * Get the error code for the exception
     *
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns whether a CodeException is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and CodeException are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeException that)) return false;
        return errorCode == that.errorCode;
    }

    /**
     * Returns an integer hash of a CodeException for use in collections
     *
     * @return an integer hash representing the CodeException
     */
    @Override
    public int hashCode() {
        return Objects.hash(errorCode);
    }
}
