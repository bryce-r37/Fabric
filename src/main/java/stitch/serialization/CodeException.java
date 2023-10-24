/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

/**
 * Exception class used for signaling failure of message creation/management
 * with an error code
 */
public class CodeException extends Exception {

    /**
     * Construct with the given error code.
     *
     * @param errorCode the error code
     */
    public CodeException(ErrorCode errorCode) {

    }

    /**
     * Construct with the given error code and cause
     *
     * @param errorCode the error code
     * @param cause the cause of the error (null if no cause)
     */
    public CodeException(ErrorCode errorCode, Throwable cause) {

    }

    /**
     * Get the error code for the exception
     *
     * @return the error code
     */
    public ErrorCode getErrorCode() {

    }
}
