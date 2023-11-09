/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization.test;

import fabric.serialization.*;
import fabric.serialization.Error;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Error class, which extends Message class
 */
class ErrorTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String ERROP = "ERROR";
    private static final int ERRCODE = 200;
    private static final String ERRMSG = "error msg";
    private static final Error ERROR;
    private static final byte[] ERRORENC = "ERROR 200 error msg\r\n".getBytes(ENC);
    private static final byte[] ERRORENCVAL = "ERR 200 error msg\r\n".getBytes(ENC);
    private static final byte[] ERRORENCEOS = "ERROR 200 error ".getBytes(ENC);
    private static final int NEWCODE = 300;
    private static final String NEWMSG = "diff msg";
    private static final int BADCODE = -1;
    private static final String BADMSG = "*&()~";
    private static final String ERRSTR = "Error: code=200 message=error msg";

    static {
        try {
            ERROR = new Error(ERRCODE, ERRMSG);
        } catch (ValidationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Tests if Error message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        ERROR.encode(mOut);
        assertArrayEquals(ERRORENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Error messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Error message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation, code, or message are invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ERRORENC));
            Error e = (Error) Message.decode(mIn);
            assertEquals(ERRCODE, e.getCode());
            assertEquals(ERRMSG, e.getMessage());
            assertEquals(ERROP, e.getOperation());
        }

        /**
         * Tests decode NullPointerException throw
         */
        @Test
        void decodeTestNull() {
            assertThrows(NullPointerException.class, () -> {new MessageInput(null);});
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ERRORENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ERRORENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(ERRSTR, ERROR.toString());
    }

    /**
     * Tests getCode return
     */
    @Test
    void getCodeTest() {
        assertEquals(ERRCODE, ERROR.getCode());
    }

    /**
     * Tests setCode method
     */
    @Nested
    class setCodeTests {
        /**
         * Tests setCode with valid code parameter
         */
        @Test
        void setCodeTestValid() {
            try {
                Error e = new Error(ERRCODE, ERRMSG);
                e.setCode(NEWCODE);
                assertEquals(e.getCode(), NEWCODE);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad code parameter
         */
        @Test
        void setCodeTestBad() {
            try {
                Error e = new Error(ERRCODE, ERRMSG);
                assertThrows(ValidationException.class, () -> {e.setCode(BADCODE);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Tests getMessage return
     */
    @Test
    void getMessageTest() {
        assertEquals(ERROR.getMessage(), ERRMSG);
    }

    /**
     * Tests getOperation return for Error messages
     */
    @Test
    void getOperationTest() {
        assertEquals(ERROR.getOperation(), ERROP);
    }

    /**
     * Tests setMessage method
     */
    @Nested
    class setMessageTests {
        /**
         * Tests setMessage with valid message parameter
         */
        @Test
        void setMessageTestValid() {
            try {
                Error e = new Error(ERRCODE, ERRMSG);
                e.setMessage(NEWMSG);
                assertEquals(e.getMessage(), NEWMSG);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests NullPointerException throw
         */
        @Test
        void setMessageTestNull() {
            try {
                Error e = new Error(ERRCODE, ERRMSG);
                assertThrows(ValidationException.class, () -> {e.setMessage(null);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad message parameter
         */
        @Test
        void setMessageTestBad() {
            try {
                Error e = new Error(ERRCODE, ERRMSG);
                assertThrows(ValidationException.class, () -> {e.setMessage(BADMSG);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}