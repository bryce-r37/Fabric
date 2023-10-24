/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

/**
 * Represents a Stitch query and performs serialization/deserialization
 */
public class Query extends Message {

    /**
     * Current number of requested posts
     */
    private int requestedPosts;

    /**
     * Creates a new Query given individual attributes
     *
     * @param queryID ID for query
     * @param requestedPorts Number of requested ports
     * @throws IllegalArgumentException See setters for specific validation of
     * arguments
     */
    public Query(long queryID, int requestedPorts)
            throws IllegalArgumentException {

    }

    /**
     * Deserialize query
     *
     * @param buffer bytes from which to deserialize
     * @throws CodeException if validation fails. Validation problems include
     * insufficient/excess bytes (PACKETTOOSHORT/LONG), bad QR field value
     * (UNEXPECTEDPACKETTYPE), incorrect version (BADVERSION), bad reserve
     * (NETWORKERROR), non-zero error code (UNEXPECTEDERRORCODE), or other
     * validation problems (VALIDATIONERROR).
     */
    public Query(byte[] buffer) throws CodeException {

    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return null;
    }

    /**
     * Get the number of requested posts in the message
     *
     * @return current number of requested posts
     */
    public int getRequestedPosts() {
        return 0;
    }

    /**
     * Set the number of requested posts in the message
     *
     * @param requestedPosts new number of requested posts
     * @return Query with new requested posts
     * @throws IllegalArgumentException if number of requested posts is out of
     * range
     */
    public Query setRequestedPosts(int requestedPosts)
            throws IllegalArgumentException {
        return this;
    }
}
