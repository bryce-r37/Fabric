/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package stitch.serialization.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import stitch.serialization.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Query class, which extends Message class
 */
class QueryTest {

    private static final Query QUERY;
    private static final Query QUERY2;
    private static final Query QUERY3;
    private static final byte[] QENC;
    private static final byte[] SHORTQENC;
    private static final byte[] LONGQENC;
    private static final byte[] ERRQENC;
    private static final byte[] VERSQENC;
    private static final String QSTRING = "Query: QueryID=1 ReqPosts=16383";

    static {
        QUERY = new Query(1, 0b0011_1111_1111_1111);
        QUERY2 = new Query(1, 1);
        QUERY3 = new Query(1, 0b0011_1111_1111_1111);
        QENC = new byte[8];
        SHORTQENC = new byte[5];
        LONGQENC = new byte[9];
        ERRQENC = new byte[8];
        VERSQENC = new byte[8];
        QENC[0] = SHORTQENC[0] = LONGQENC[0] = ERRQENC[0] = 0b0010_0000;
        VERSQENC[0] = 0b0110_0000;
        ERRQENC[1] = 1;
        QENC[5] = 1;
        QENC[6] = (byte) 0b0011_1111;
        QENC[7] = (byte) 0b1111_1111;
    }

    /**
     * Tests Query parameterized constructor
     */
    @Nested
    class parameterizedConstructorTests {
        /**
         * Tests happy path parameterized constructor
         *
         * @param queryID ID for query
         * @param requestedPosts Number of requested posts
         */
        @ParameterizedTest
        @CsvSource({"1,1", "0,0", "1,0x0FFFF", "0x0FFFFFFFF,1", "123456,1234"})
        void goodParamConstructorTest(long queryID, int requestedPosts) {
            assertDoesNotThrow(() -> new Query(queryID, requestedPosts));
        }

        /**
         * Tests sad path parameterized constructor
         *
         * @param queryID ID for query
         * @param requestedPosts Number of requested posts
         */
        @ParameterizedTest
        @CsvSource({"-1,1", "1,-1", "1,0x01FFFF", "0x01FFFFFFFF,1"})
        void badParamConstructorTest(long queryID, int requestedPosts) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Query(queryID, requestedPosts));
        }
    }

    /**
     * Tests Query serialized constructor
     */
    @Nested
    class serializedConstructorTests {
        /**
         * Tests happy path serialized constructor
         */
        @Test
        void goodSerialConstructorTest() throws CodeException {
            assertEquals(QUERY, new Query(QENC));
        }

        /**
         * Tests short array serialized constructor
         */
        @Test
        void shortSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Query(SHORTQENC));
            assertEquals(ErrorCode.PACKETTOOSHORT, ex.getErrorCode());
        }

        /**
         * Tests long array serialized constructor
         */
        @Test
        void longSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Query(LONGQENC));
            assertEquals(ErrorCode.PACKETTOOLONG, ex.getErrorCode());
        }

        /**
         * Tests error serialized constructor
         */
        @Test
        void errorSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Query(ERRQENC));
            assertEquals(ErrorCode.UNEXPECTEDERRORCODE, ex.getErrorCode());
        }

        /**
         * Tests bad version serialized constructor
         */
        @Test
        void versionSerialConstructorTest() {
            CodeException ex = assertThrows(CodeException.class, () ->
                    new Query(VERSQENC));
            assertEquals(ErrorCode.BADVERSION, ex.getErrorCode());
        }
    }

    /**
     * Tests toString on valid query
     */
    @Test
    void toStringTest() {
        assertEquals(QSTRING, QUERY.toString());
    }

    /**
     * Tests getRequestedPosts on valid query
     */
    @Test
    void getRequestedPostsTest() {
        assertEquals(16383, QUERY.getRequestedPosts());
    }

    /**
     * Tests getQueryID on valid query
     */
    @Test
    void getQueryIDTest() {
        assertEquals(1L, QUERY.getQueryID());
    }

    /**
     * Tests setRequestedPosts
     */
    @Nested
    class setRequestedPostsTests {
        /**
         * Tests happy path setRequestedPosts
         *
         * @param requestedPosts Number of requested posts
         */
        @ParameterizedTest
        @ValueSource(ints = {0, 10, 255, 0x0FFFF})
        void goodRequestedPostsTest(int requestedPosts) {
            assertDoesNotThrow(() -> QUERY2.setRequestedPosts(requestedPosts));
        }

        /**
         * Tests sad path setRequestedPosts
         *
         * @param requestedPosts Number of requested posts
         */
        @ParameterizedTest
        @ValueSource(ints = {-1, 0x01FFFF, Integer.MAX_VALUE})
        void badRequestedPostsTest(int requestedPosts) {
            assertThrows(IllegalArgumentException.class, () ->
                    QUERY2.setRequestedPosts(requestedPosts));
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
         * @param queryID ID for query
         */
        @ParameterizedTest
        @ValueSource(longs = {0L, 10L, 0x0FFFFL, 0x0FFFFFFFFL})
        void goodQueryIDTest(long queryID) {
            assertDoesNotThrow(() -> QUERY2.setQueryID(queryID));
        }

        /**
         * Tests sad path setQueryID
         *
         * @param queryID ID for query
         */
        @ParameterizedTest
        @ValueSource(longs = {-1L, 0x01FFFFFFFFL, Long.MAX_VALUE})
        void badQueryIDTest(long queryID) {
            assertThrows(IllegalArgumentException.class, () ->
                    QUERY2.setQueryID(queryID));
        }
    }

    /**
     * Tests happy path encode
     */
    @Test
    void encodeTest() {
        assertEquals(0, Arrays.compare(QENC, QUERY.encode()));
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
            assertEquals(QUERY, QUERY3);
        }

        /**
         * Tests reflexive equals
         */
        @Test
        void reflexiveEqualsTest() {
            assertEquals(QUERY, QUERY);
        }

        /**
         * Tests sad path equals
         */
        @Test
        void badEqualsTest() {
            assertNotEquals(QUERY, QUERY2);
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
            assertEquals(QUERY.hashCode(), QUERY3.hashCode());
        }

        /**
         * Tests reflexive hash
         */
        @Test
        void reflexiveHashTest() {
            assertEquals(QUERY.hashCode(), QUERY.hashCode());
        }

        /**
         * Tests sad path hash
         */
        @Test
        void badEqualsTest() {
            assertNotEquals(QUERY.hashCode(), QUERY2.hashCode());
        }
    }
}