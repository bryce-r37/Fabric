/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization.test;

import fabric.serialization.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Challenge class, which extends Message class
 */
class ChallengeTest {
    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String CLNGOP = "CLNG";
    private static final String SERVNONCE = "123456";
    private static final Challenge CHALLENGE;
    private static final byte[] CLNGENC = "CLNG 123456\r\n".getBytes(ENC);
    private static final byte[] CLNGENCVAL = "CLNG 12E456\r\n".getBytes(ENC);
    private static final byte[] CLNGENCEOS = "CLNG 1234".getBytes(ENC);
    private static final String NEWSERVNONCE = "654321";
    private static final String BADSERVNONCE = "abcdef";
    private static final String CLNGSTR = "Challenge: nonce=123456";

    static {
        try {
            CHALLENGE = new Challenge(SERVNONCE);
        } catch (ValidationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Tests if Challenge message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        CHALLENGE.encode(mOut);
        assertArrayEquals(CLNGENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Challenge messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Challenge message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation or nonce are invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CLNGENC));
            Challenge c = (Challenge) Message.decode(mIn);
            assertEquals(SERVNONCE, c.getNonce());
            assertEquals(CLNGOP, c.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CLNGENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CLNGENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(CLNGSTR, CHALLENGE.toString());
    }

    /**
     * Tests getNonce return
     */
    @Test
    void getNonceTest() {
        assertEquals(CHALLENGE.getNonce(), SERVNONCE);
    }

    /**
     * Tests getOperation return for Challenge messages
     */
    @Test
    void getOperationTest() {
        assertEquals(CHALLENGE.getOperation(), CLNGOP);
    }

    /**
     * Tests setNonce method
     */
    @Nested
    class setNonceTests {
        /**
         * Tests setNonce with valid nonce parameter
         */
        @Test
        void setNonceTestValid() {
            try {
                Challenge c = new Challenge(SERVNONCE);
                c.setNonce(SERVNONCE);
                assertEquals(c.getNonce(), SERVNONCE);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests NullPointerException throw
         */
        @Test
        void setNonceTestNull() {
            try {
                Challenge c = new Challenge(SERVNONCE);
                assertThrows(ValidationException.class, () -> {c.setNonce(null);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad nonce parameter
         */
        @Test
        void setNonceTestBad() {
            try {
                Challenge c = new Challenge(SERVNONCE);
                assertThrows(ValidationException.class, () -> {c.setNonce(BADSERVNONCE);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
