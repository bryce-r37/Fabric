/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization;

import java.util.List;

/**
 * Represents a Stitch response and performs serialization/deserialization
 */
public class Response extends Message {

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
     * Get the response list of posts
     *
     * @return current list of posts
     */
    public List<String> getPosts() {
        return null;
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
        return this;
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public ErrorCode getErrorCode() {
        return null;
    }
}
