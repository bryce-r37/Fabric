/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents a Stitch message header
 */
public abstract class Message {

    /**
     * Length of packet header
     */
    private static final int HLEN = 6;

    /**
     * Bit mask for version bit
     */
    protected static final byte VERSION = (byte) 0b0010_0000;

    /**
     * Bit mask for query bit
     */
    protected static final byte QUERY = (byte) 0b0000_0000;

    /**
     * Bit mask for response bit
     */
    protected static final byte RESPONSE = (byte) 0b0000_1000;

    /**
     * Bit mask for reserved bits
     */
    protected static final byte RSRVD = (byte) 0b0000_0000;

    /**
     * Max unsigned 4 byte value (for encoding)
     */
    private static final long MAXQID = 0x0FFFFFFFFL;

    /**
     * Min unsigned 4 byte value (for encoding)
     */
    private static final long MINQID = 0L;

    /**
     * Character encoding used for serialization/deserialization
     */
    protected static final Charset ENC = StandardCharsets.ISO_8859_1;

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
     * @throws IllegalArgumentException if the given query ID is out of range
     */
    protected Message(long queryID) throws IllegalArgumentException {
        this.queryID = validateQueryID(queryID);
    }

    /**
     * Creates a new message from deserialization
     *
     * @param buffer byte buffer containing packet to deserialize
     * @param query true if Query, false if Response
     * @throws CodeException if validation fails. Validation problems include
     * insufficient bytes (PACKETTOOSHORT), bad QR field value
     * (UNEXPECTEDPACKETTYPE), incorrect version (BADVERSION), bad reserve
     * (NETWORKERROR), or other validation problems (VALIDATIONERROR).
     */
    protected Message(byte[] buffer, boolean query) throws CodeException {
        // check for full header
        if (buffer == null || buffer.length < HLEN) {
            throw new CodeException(ErrorCode.PACKETTOOSHORT);
        }
        // check version (first four bits)
        if (((buffer[0] & 0b1111_0000) ^ VERSION) != 0) {
            throw new CodeException(ErrorCode.BADVERSION);
        }
        // check type (fifth bit)
        if ((query && ((byte) (buffer[0] & 0b0_1000) ^ QUERY) != 0) ||
                (!query && (((byte) (buffer[0] & 0b0_1000) ^ RESPONSE) != 0))) {
            throw new CodeException(ErrorCode.UNEXPECTEDPACKETTYPE);
        }
        // check reserved (last three bits)
        if (((byte) (buffer[0] & 0b0111) ^ RSRVD) != 0) {
            throw new CodeException(ErrorCode.NETWORKERROR);
        }
        try {
            this.queryID = validateQueryID( new BigInteger(buffer, 2, 4)
                    .intValue() & MAXQID);
        } catch (IllegalArgumentException ex) {
            throw new CodeException(ErrorCode.VALIDATIONERROR, ex);
        }
    }

    /**
     * Set the query ID
     *
     * @param queryID the new query ID
     * @return Message with new query ID
     * @throws IllegalArgumentException if the given query ID is out of range
     */
    public Message setQueryID(long queryID) throws IllegalArgumentException {
        this.queryID = validateQueryID(queryID);
        return this;
    }

    /**
     * Get the query ID
     *
     * @return current query ID
     */
    public long getQueryID() {
        return this.queryID;
    }

    /**
     * Serialize the message
     *
     * @return serialized message
     */
    public byte[] encode() {
        byte[] header = new byte[6];
        header[0] = (byte) (VERSION | RSRVD);
        header[2] = (byte) (this.queryID >> 24 & 0x0FF);
        header[3] = (byte) (this.queryID >> 16 & 0x0FF);
        header[4] = (byte) (this.queryID >> 8 & 0x0FF);
        header[5] = (byte) (this.queryID & 0x0FF);

        byte[] data = this.encodeData(header);

        ByteBuffer encoding = ByteBuffer.wrap(new byte[
                header.length + data.length]);
        encoding.put(header);
        encoding.put(data);

        return encoding.array();
    }

    /**
     * Finishes encoding header and encodes data for a Message
     *
     * @param header the incomplete header encoding
     * @return a byte array of the encoded data
     */
    protected abstract byte[] encodeData(byte[] header);

    /**
     * Returns whether a Message is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Message are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return this.queryID == message.queryID;
    }

    /**
     * Validates query ID
     *
     * @param queryID the new query ID
     * @return the new query ID
     * @throws IllegalArgumentException if the given queryID is out of range
     */
    private long validateQueryID(long queryID) throws IllegalArgumentException {
        if (queryID < MINQID || queryID > MAXQID) {
            throw new IllegalArgumentException("Invalid queryID");
        }
        return queryID;
    }
}
