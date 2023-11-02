/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization.test;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import stitch.serialization.CodeException;
import stitch.serialization.ErrorCode;
import stitch.serialization.Response;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Response class, which extends Message class
 */
class ResponseTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final Response RESPONSE;
    private static final Response RESPONSE2;
    private static final Response RESPONSE3;
    private static final Response RESPONSE4;
    private static final byte[] RENC;
    private static final byte[] SHORTRENC;
    private static final byte[] LONGRENC;
    private static final byte[] ERRRENC;
    private static final byte[] VERSRENC;
    private static final List<String> GOODPOSTS = new ArrayList<>(
            Arrays.asList("test1", "test2", "test3"));
    private static final List<String> BADPOST = new ArrayList<>(
            Arrays.asList("test1", "test2\r", "test3"));
    private static final List<String> NULLPOST = new ArrayList<>(
            Arrays.asList("test1", null, "test3"));
    private static final List<String> LARGELIST = new ArrayList<>();
    private static final String RSTRING = "Response: QueryID=1 Error=" +
            "NO ERROR Posts=3: test1, test2, test3";


    static {
        RESPONSE = new Response(1, ErrorCode.NOERROR, GOODPOSTS);
        RESPONSE2 = new Response(100, ErrorCode.NETWORKERROR, new ArrayList<>());
        RESPONSE3 = new Response(1, ErrorCode.NOERROR, GOODPOSTS);
        RESPONSE4 = new Response(100, ErrorCode.NETWORKERROR, new ArrayList<>());
        ByteBuffer buffer = ByteBuffer.wrap(new byte[28]);
        buffer.put(new byte[6]);
        buffer.put((byte) 3);
        buffer.putShort((short) GOODPOSTS.get(0).length());
        buffer.put(GOODPOSTS.get(0).getBytes(ENC));
        buffer.putShort((short) GOODPOSTS.get(1).length());
        buffer.put(GOODPOSTS.get(1).getBytes(ENC));
        buffer.putShort((short) GOODPOSTS.get(2).length());
        buffer.put(GOODPOSTS.get(2).getBytes(ENC));
        RENC = buffer.array();
        SHORTRENC = new byte[5];
        LONGRENC = new byte[9];
        ERRRENC = new byte[7];
        VERSRENC = new byte[7];
        RENC[0] = SHORTRENC[0] = LONGRENC[0] = ERRRENC[0] = 0b0010_1000;
        VERSRENC[0] = 0b0110_0000;
        ERRRENC[1] = 6;
        RENC[5] = 1;
        for (int i = 0; i < 0x0101; ++i) {
            LARGELIST.add("string");
        }
    }

    /**
     * Tests Response parameterized constructor
     */
    @Nested
    class parameterizedConstructorTests {
        /**
         * Tests happy path parameterized constructor
         */
        @Test
        void goodParamConstructorTest() {
            assertDoesNotThrow(() -> new
                    Response(1, ErrorCode.NOERROR, GOODPOSTS));
        }

        /**
         * Tests happy path parameterized constructor
         */
        @Test
        void goodParamConstructorTest2() {
            assertDoesNotThrow(() -> new
                    Response(0, ErrorCode.BADVERSION, GOODPOSTS));
        }

        /**
         * Tests bad queryID parameterized constructor
         */
        @Test
        void badIDConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(Long.MAX_VALUE, ErrorCode.NOERROR, GOODPOSTS));
        }

        /**
         * Tests bad queryID parameterized constructor
         */
        @Test
        void badIDConstructorTest2() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(-1, ErrorCode.NOERROR, GOODPOSTS));
        }

        /**
         * Tests bad post parameterized constructor
         */
        @Test
        void badPostConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(1, ErrorCode.NOERROR, BADPOST));
        }

        /**
         * Tests null post parameterized constructor
         */
        @Test
        void nullPostConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(1, ErrorCode.NOERROR, NULLPOST));
        }

        /**
         * Tests null list parameterized constructor
         */
        @Test
        void nullListConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(1, ErrorCode.NOERROR, null));
        }

        /**
         * Tests large list parameterized constructor
         */
        @Test
        void largeListConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(1, ErrorCode.NOERROR, LARGELIST));
        }

        /**
         * Tests null error parameterized constructor
         */
        @Test
        void nullErrorConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new
                    Response(1, null, GOODPOSTS));
        }
    }

    /**
     * Tests Response serialized constructor
     */
    @Nested
    class serializedConstructorTests {
        /**
         * Tests happy path serialized constructor
         */
        @Test
        void goodSerialConstructorTest() throws CodeException {
            assertEquals(RESPONSE, new Response(RENC));
        }

        /**
         * Tests short array serialized constructor
         */
        @Test
        void shortSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Response(SHORTRENC));
            assertEquals(ErrorCode.PACKETTOOSHORT, ex.getErrorCode());
        }

        /**
         * Tests long array serialized constructor
         */
        @Test
        void longSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Response(LONGRENC));
            assertEquals(ErrorCode.PACKETTOOLONG, ex.getErrorCode());
        }

        /**
         * Tests error serialized constructor
         */
        @Test
        void errorSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Response(ERRRENC));
            assertEquals(ErrorCode.UNEXPECTEDERRORCODE, ex.getErrorCode());
        }

        /**
         * Tests bad version serialized constructor
         */
        @Test
        void versionSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Response(VERSRENC));
            assertEquals(ErrorCode.BADVERSION, ex.getErrorCode());
        }
    }

    /**
     * Tests toString on valid response
     */
    @Test
    void toStringTest() {
        assertEquals(RSTRING, RESPONSE.toString());
    }

    /**
     * Tests getPosts on valid response
     */
    @Test
    void getPostsTest() {
        assertEquals(GOODPOSTS, RESPONSE.getPosts());
    }

    /**
     * Tests getErrorCode on valid response
     */
    @Test
    void getErrorCodeTest() {
        assertEquals(ErrorCode.NOERROR, RESPONSE.getErrorCode());
    }

    /**
     * Tests getQueryID on valid response
     */
    @Test
    void getQueryIDTest() {
        assertEquals(1L, RESPONSE.getQueryID());
    }

    /**
     * Tests setPosts
     */
    @Nested
    class setPostsTests {
        /**
         * Tests happy path setPosts
         */
        @Test
        void goodSetPostsTest() {
            assertDoesNotThrow(() -> RESPONSE2.setPosts(GOODPOSTS));
        }

        /**
         * Tests setPosts with bad post
         */
        @Test
        void badPostSetPostsTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE2.setPosts(BADPOST));
        }

        /**
         * Tests setPosts with null post
         */
        @Test
        void nullPostSetPostsTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE2.setPosts(NULLPOST));
        }

        /**
         * Tests setPosts with null List
         */
        @Test
        void nullListSetPostsTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE2.setPosts(null));
        }

        /**
         * Tests setPosts with large List
         */
        @Test
        void largeListSetPostsTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE2.setPosts(LARGELIST));
        }
    }

    /**
     * Tests setErrorCode
     */
    @Nested
    class setErrorCodeTests {
        /**
         * Tests happy path setErrorCode
         */
        @Test
        void goodSetErrorCodeTest() {
            assertDoesNotThrow(() ->
                    assertEquals(RESPONSE4.setErrorCode(ErrorCode.NOERROR),
                            new Response(100, ErrorCode.NOERROR,
                                    new ArrayList<>()))
            );
        }

        /**
         * Tests setErrorCode with null error code
         */
        @Test
        void nullSetErrorCodeTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE4.setErrorCode(null));
        }
    }

    /**
     * Tests setQueryID
     */
    @Nested
    class setQueryIDTests {
        /**
         * Tests happy path setQueryID
         *
         * @param queryID ID for response
         */
        @ParameterizedTest
        @ValueSource(longs = {0L, 10L, 0x0FFFFL, 0x0FFFFFFFFL})
        void goodQueryIDTest(long queryID) {
            assertDoesNotThrow(() -> RESPONSE2.setQueryID(queryID));
        }

        /**
         * Tests sad path setQueryID
         *
         * @param queryID ID for response
         */
        @ParameterizedTest
        @ValueSource(longs = {-1L, 0x01FFFFFFFFL, Long.MAX_VALUE})
        void badQueryIDTest(long queryID) {
            assertThrows(IllegalArgumentException.class, () ->
                    RESPONSE2.setQueryID(queryID));
        }
    }

    /**
     * Tests happy path encode
     */
    @Test
    void encodeTest() {
        assertEquals(0, Arrays.compare(RENC, RESPONSE.encode()));
    }

    /**
     * Tests equals method
     */
    @Nested
    class testEquals {
        /**
         * Tests happy path equals
         */
        @Test
        void goodEqualsTest() {
            assertEquals(RESPONSE, RESPONSE3);
        }

        /**
         * Tests reflexive equals
         */
        @Test
        void reflexiveEqualsTest() {
            assertEquals(RESPONSE, RESPONSE);
        }

        /**
         * Tests sad path equals
         */
        @Test
        void badEqualsTest() {
            assertNotEquals(RESPONSE, RESPONSE2);
        }
    }

    /**
     * Tests hashCode method
     */
    @Nested
    class testHashCode {
        /**
         * Tests equivalent hash
         */
        @Test
        void equivalentHashTest() {
            assertEquals(RESPONSE.hashCode(), RESPONSE3.hashCode());
        }

        /**
         * Tests reflexive hash
         */
        @Test
        void reflexiveHashTest() {
            assertEquals(RESPONSE.hashCode(), RESPONSE.hashCode());
        }

        /**
         * Tests sad path hash
         */
        @Test
        void badEqualsTest() {
            assertNotEquals(RESPONSE.hashCode(), RESPONSE2.hashCode());
        }
    }
}