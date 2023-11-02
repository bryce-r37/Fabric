package stitch.serialization.test;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import stitch.serialization.ErrorCode;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ErrorCode enum class
 */
class ErrorCodeTest {
    private static final ErrorCode[] codes = {ErrorCode.NOERROR,
            ErrorCode.BADVERSION, ErrorCode.UNEXPECTEDERRORCODE,
            ErrorCode.UNEXPECTEDPACKETTYPE, ErrorCode.PACKETTOOLONG,
            ErrorCode.PACKETTOOSHORT, ErrorCode.NETWORKERROR,
            ErrorCode.VALIDATIONERROR};

    /**
     * Tests the values method for ErrorCode
     */
    @Test
    void valuesTest() {
        assertEquals(0, Arrays.compare(codes, ErrorCode.values()));
    }

    /**
     * Tests for valueOf
     */
    @Nested
    class valueOfTests {
        /**
         * Tests valueOf for NOERROR
         */
        @Test
        void noErrorValueOfTest() {
            assertEquals(ErrorCode.NOERROR, ErrorCode.valueOf("NOERROR"));
        }

        /**
         * Tests valueOf for PACKETTOOLONG
         */
        @Test
        void tooLongValueOfTest() {
            assertEquals(ErrorCode.PACKETTOOLONG, ErrorCode.valueOf("PACKETTOOLONG"));
        }

        /**
         * Tests valueOf with invalid name
         */
        @Test
        void nonexistentValueOfTest() {
            assertThrows(IllegalArgumentException.class, () ->
                    ErrorCode.valueOf("FAKEERROR"));
        }

        /**
         * Tests valueOf for null
         */
        @Test
        void nullValueOfTest() {
            assertThrows(NullPointerException.class, () ->
                    ErrorCode.valueOf(null));
        }
    }

    /**
     * Tests for getErrorCodeValue
     */
    @Nested
    class getErrorCodeValueTests {
        /**
         * Test getErrorCodeValue for BADVERSION
         */
        @Test
        void badVersionCodeValueTest() {
            assertEquals(1, ErrorCode.BADVERSION.getErrorCodeValue());
        }

        /**
         * Test getErrorCodeValue for UNEXPECTEDERRORCODE
         */
        @Test
        void unexpectedErrorCodeValueTest() {
            assertEquals(2, ErrorCode.UNEXPECTEDERRORCODE.getErrorCodeValue());
        }

        /**
         * Test getErrorCodeValue for NETWORKERROR
         */
        @Test
        void networkCodeValueTest() {
            assertEquals(7, ErrorCode.NETWORKERROR.getErrorCodeValue());
        }
    }

    /**
     * Tests for getErrorMessage
     */
    @Nested
    class getErrorMessageTests {
        /**
         * Test getErrorMessage for PACKETTOOLONG
         */
        @Test
        void tooLongMessageTest() {
            assertEquals("Packet too long",
                    ErrorCode.PACKETTOOLONG.getErrorMessage());
        }

        /**
         * Test getErrorMessage for VALIDATIONERROR
         */
        @Test
        void validationErrorMessageTest() {
            assertEquals("Validation error",
                    ErrorCode.VALIDATIONERROR.getErrorMessage());
        }

        /**
         * Test getErrorCodeValue for NOERROR
         */
        @Test
        void noErrorMessageTest() {
            assertEquals("No error",
                    ErrorCode.NOERROR.getErrorMessage());
        }
    }

    /**
     * Tests for getErrorCode
     */
    @Nested
    class getErrorCodeTests {
        /**
         * Test getErrorCode for UNEXPECTEDPACKETTYPE
         */
        @Test
        void unexpectedTypeGetCodeTest() {
            assertEquals(ErrorCode.UNEXPECTEDPACKETTYPE,
                    ErrorCode.getErrorCode(3));
        }

        /**
         * Test getErrorCode for PACKETTOOSHORT
         */
        @Test
        void tooShortGetCodeTest() {
            assertEquals(ErrorCode.PACKETTOOSHORT,
                    ErrorCode.getErrorCode(5));
        }

        /**
         * Test getErrorCode for BADVERSION
         */
        @Test
        void badVersionGetCodeTest() {
            assertEquals(ErrorCode.BADVERSION,
                    ErrorCode.getErrorCode(1));
        }
    }
}
