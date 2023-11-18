/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

/**
 * Exception for handling validation problems
 */
public class ValidationException extends Exception {

    /**
     * Bad string token causing exception (null if no such string)
     */
    private final String badToken;

    /**
     * Constructs validation exception
     *
     * @param message exception message
     * @param cause exception cause
     * @param badToken bad string token causing exception (null if no such
     *                  string)
     */
    public ValidationException(String message, Throwable cause,
                               String badToken) {
        super(message, cause);
        this.badToken = badToken;
    }

    /**
     * Constructs validation exception
     *
     * @param message exception message
     * @param badToken bad string token causing exception (null if no such
     *                  string)
     */
    public ValidationException(String message, String badToken) {
        this(message, null, badToken);
    }

    /**
     * Returns bad token
     *
     * @return bad token
     */
    public String getBadToken() {
        return badToken;
    }
}
