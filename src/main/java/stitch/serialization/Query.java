/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a Stitch query and performs serialization/deserialization
 */
public class Query extends Message {

    /**
     * Min unsigned 2 byte value (for encoding)
     */
    private static final int MINPOSTS = 0;

    /**
     * Max unsigned 2 byte value (for encoding)
     */
    private static final int MAXPOSTS = 0x0FFFF;

    /**
     * Length in bytes of Query packets
     */
    static final int QLEN = 8;

    /**
     * Current number of requested posts
     */
    private int requestedPosts;

    /**
     * Creates a new Query given individual attributes
     *
     * @param queryID ID for query
     * @param requestedPosts Number of requested posts
     * @throws IllegalArgumentException See setters for specific validation of
     * arguments
     */
    public Query(long queryID, int requestedPosts)
            throws IllegalArgumentException {
        super(queryID);
        this.requestedPosts = validateRequestedPosts(requestedPosts);
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
        super(buffer, true);
        // check error code (second byte)
        if (buffer[1] != 0) {
            throw new CodeException(ErrorCode.UNEXPECTEDERRORCODE);
        }
        // check if data too short
        if (buffer.length < QLEN) {
            throw new CodeException(ErrorCode.PACKETTOOSHORT);
        }
        // check if data too long
        if (buffer.length > QLEN) {
            throw new CodeException(ErrorCode.PACKETTOOLONG);
        }
        // check requested post number
        try {
            this.requestedPosts = validateRequestedPosts(
                    new BigInteger(buffer, 6, 2).shortValue() & MAXPOSTS);
        } catch (IllegalArgumentException ex) {
            throw new CodeException(ErrorCode.VALIDATIONERROR, ex);
        }
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Query: QueryID=" + getQueryID() + " ReqPosts=" +
                this.requestedPosts;
    }

    /**
     * Get the number of requested posts in the message
     *
     * @return current number of requested posts
     */
    public int getRequestedPosts() {
        return this.requestedPosts;
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
        this.requestedPosts = validateRequestedPosts(requestedPosts);
        return this;
    }

    /**
     * Finishes encoding header and encodes data for a Message
     *
     * @param header the incomplete header encoding
     * @return a byte array of the encoded data
     */
    protected byte[] encodeData(byte[] header) {
        header[0] = (byte) (header[0] | Message.QUERY);
        header[1] = 0;

        byte[] data = new byte[2];
        data[0] = (byte) (requestedPosts >> 8);
        data[1] = (byte) (requestedPosts & 0b01111_1111);

        return data;
    }

    /**
     * Returns whether a Query is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Query are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Query query)) return false;
        if (!super.equals(o)) return false;
        return this.requestedPosts == query.requestedPosts;
    }

    /**
     * Returns an integer hash of a Query for use in collections
     *
     * @return an integer hash representing the Query
     */
    @Override
    public int hashCode() {
        return Objects.hash(getQueryID(), this.requestedPosts);
    }

    /**
     * Validates the number of requested posts
     *
     * @param requestedPosts the number of requested posts
     * @return the number of requested posts
     * @throws IllegalArgumentException if the given number is out of range
     */
    private int validateRequestedPosts(int requestedPosts)
            throws IllegalArgumentException {
        if (requestedPosts < MINPOSTS || requestedPosts > MAXPOSTS) {
            throw new IllegalArgumentException("Invalid post number request");
        }
        return requestedPosts;
    }
}
