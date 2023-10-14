/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app;

import fabric.serialization.Message;

/**
 * Exception for delegating error message handling
 */
public class MessageException extends Exception {
    /**
     * Error message causing exception
     */
    private final Message error;

    /**
     * Constructs message exception
     *
     * @param message exception message
     * @param cause exception cause
     * @param error Error message causing exception
     */
    public MessageException(String message, Throwable cause, Message error) {
        super(message, cause);
        this.error = error;
    }

    /**
     * Constructs message exception
     *
     * @param message exception message
     * @param error Error message causing exception
     */
    public MessageException(String message, Message error) {
        this(message, null, error);
    }

    /**
     * Constructs message exception
     *
     * @param message exception message
     * @param cause exception cause
     */
    public MessageException(String message, Throwable cause) {
        this(message, cause, null);
    }

    /**
     * Returns Error
     *
     * @return Error
     */
    public Message getError() {
        return error;
    }
}
