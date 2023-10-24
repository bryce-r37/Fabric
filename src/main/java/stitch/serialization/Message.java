/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

/**
 * Represents a Stitch message header
 */
public abstract class Message {

    /**
     * ID of the query
     */
    private long queryID;

    /**
     * Default constructor
     */
    protected Message() {

    }

    /**
     * Creates a new Message given individual attributes
     *
     * @param queryID ID for query
     */
    protected Message(long queryID) {

    }

    /**
     * Set the query ID
     *
     * @param queryID the new query ID
     * @return Message with new query ID
     * @throws IllegalArgumentException if the given query ID is out of range
     */
    public Message setQueryID(long queryID) throws IllegalArgumentException {
        return this;
    }

    /**
     * Get the query ID
     *
     * @return current query ID
     */
    public long getQueryID() {
        return 0;
    }

    /**
     * Serialize the message
     *
     * @return serialized message
     */
    public byte[] encode() {
        return null;
    }

}
