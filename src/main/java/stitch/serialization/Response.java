/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Stitch response and performs serialization/deserialization
 */
public class Response extends Message {

    /**
     * Max unsigned 1 byte value (for encoding)
     */
    private static final int MAXPOSTS = 0x0FF;

    /**
     * Max post length for 2 byte encoding
     */
    private static final int MAXLEN = 0x0FFFF;

    /**
     * Min length in bytes of Response packets
     */
    private static final int RLEN = 7;

    /**
     * Permitted format for posts (all printable ISO-8859-1 characters)
     */
    private static final String POSTFORMAT = "^[ -~¡-¬®-ÿ]*$";

    /**
     * Response error code
     */
    private ErrorCode errorCode;

    /**
     * Response list of posts
     */
    private List<String> posts;

    /**
     * Creates a new Response given individual attributes
     *
     * @param queryID ID for response
     * @param errorCode error code for response
     * @param posts list of posts
     * @throws IllegalArgumentException See setters for specific validation of
     * arguments
     */
    public Response(long queryID, ErrorCode errorCode, List<String> posts)
        throws IllegalArgumentException {
        super(queryID);
        this.errorCode = validateErrorCode(errorCode);
        this.posts = validatePosts(posts);
    }

    /**
     * Deserialize response
     *
     * @param buffer bytes from which to deserialize
     * @throws CodeException if validation fails. Validation problems include
     * insufficient/excess bytes (PACKETTOOSHORT/LONG), bad QR field value
     * (UNEXPECTEDPACKETTYPE), incorrect version (BADVERSION), bad reserve
     * (NETWORKERROR), unexpected error code (UNEXPECTEDERRORCODE), or other
     * validation problems (VALIDATIONERROR)
     */
    public Response(byte[] buffer) throws CodeException {
        super (buffer, false);
        // set error code
        try {
            this.errorCode = ErrorCode.getErrorCode(buffer[1]);
        } catch (IllegalArgumentException ex) {
            throw new CodeException(ErrorCode.UNEXPECTEDERRORCODE, ex);
        }
        // check if packet too short
        if (buffer.length < RLEN) {
            throw new CodeException(ErrorCode.PACKETTOOSHORT);
        }
        int postCnt = buffer[RLEN - 1] & MAXPOSTS;
        if (buffer.length < RLEN + 2 * postCnt) {
            throw new CodeException(ErrorCode.PACKETTOOSHORT);
        }
        // set posts
        int next = RLEN;
        List<String> posts = new ArrayList<>();
        for (int i = 0; i < postCnt; ++i) {
            if (next + 2 > buffer.length) {
                throw new CodeException(ErrorCode.PACKETTOOSHORT);
            }
            int len = new BigInteger(buffer, next, 2).shortValue() & 0x0FFFF;
            next += 2;
            if (next + len > buffer.length) {
                throw new CodeException(ErrorCode.PACKETTOOSHORT);
            }
            String s = new String(buffer, next, len, Message.ENC);
            if (!s.matches(POSTFORMAT)) {
                throw new CodeException(ErrorCode.VALIDATIONERROR);
            }
            posts.add(s);
            next += len;
        }
        if (next != buffer.length) {
            throw new CodeException(ErrorCode.PACKETTOOLONG);
        }
        this.posts = posts;
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        StringBuilder response = new StringBuilder("Response: QueryID=" +
                getQueryID() + " Error=" + getErrorCode().getErrorCodeValue() +
                " Posts=" + posts.size() + ": ");
        for (String post : posts) {
            response.append(post);
            response.append(", ");
        }
        response.delete(response.length() - 2, response.length());
        return response.toString();
    }

    /**
     * Get the response list of posts
     *
     * @return current list of posts
     */
    public List<String> getPosts() {
        return this.posts;
    }

    /**
     * Set the response list of posts
     *
     * @param posts new list of posts
     * @return Response with new posts
     * @throws IllegalArgumentException if (list is null or outside length
     * range) OR (an individual post is null, outside length range, or post
     * contains illegal characters)
     */
    public Response setPosts(List<String> posts)
            throws IllegalArgumentException {
        this.posts = validatePosts(posts);
        return this;
    }

    /**
     * Set the response error code
     *
     * @param errorCode new error code
     * @return Response with new error code
     * @throws IllegalArgumentException if errorCode is null
     */
    public Response setErrorCode(ErrorCode errorCode)
            throws IllegalArgumentException {
        this.errorCode = validateErrorCode(errorCode);
        return this;
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    /**
     * Finishes encoding header and encodes data for a Message
     *
     * @param header the incomplete header encoding
     * @return a byte array of the encoded data
     */
    protected byte[] encodeData(byte[] header) {
        header[0] = (byte) (header[0] | Message.RESPONSE);
        header[1] = (byte) this.getErrorCode().getErrorCodeValue();

        int length = 0;
        for (String post : posts) {
            length += post.length();
        }

        ByteBuffer data = ByteBuffer.wrap(new byte[1 + length +
                2 * posts.size()]);

        data.put((byte) posts.size());

        for (String post : posts) {
            data.put((byte) (post.length() >> 8));
            data.put((byte) (post.length() & 0b01111_1111));
            data.put(post.getBytes(ENC));
        }

        return data.array();
    }

    /**
     * Returns whether a Response is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Response are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response response)) return false;
        if (!super.equals(o)) return false;
        return this.errorCode == response.errorCode &&
                Objects.equals(this.posts, response.posts);
    }

    /**
     * Returns an integer hash of a Response for use in collections
     *
     * @return an integer hash representing the Response
     */
    @Override
    public int hashCode() {
        return Objects.hash(getQueryID(), errorCode, posts);
    }

    /**
     * Validates the given ErrorCode
     *
     * @param errorCode the given ErrorCode
     * @return the given ErrorCode
     * @throws IllegalArgumentException if errorCode is null
     */
    private ErrorCode validateErrorCode(ErrorCode errorCode)
            throws IllegalArgumentException {
        if (errorCode == null) {
            throw new IllegalArgumentException("Null ErrorCode");
        }
        return errorCode;
    }

    /**
     * Validate the new list of posts
     *
     * @param posts new list of posts
     * @return new list of posts
     * @throws IllegalArgumentException if (list is null or outside length
     * range) OR (an individual post is null, outside length range, or post
     * contains illegal characters)
     */
    private List<String> validatePosts(List<String> posts)
            throws IllegalArgumentException {
        if (posts == null || posts.size() > MAXPOSTS) {
            throw new IllegalArgumentException("Bad post list");
        }
        for (String post : posts) {
            if (post == null || post.length() > MAXLEN ||
                    !post.matches(POSTFORMAT)) {
                throw new IllegalArgumentException("Bad post in list (" +
                        posts.indexOf(post) + "): " + post);
            }
        }
        return posts;
    }
}
