/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
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
 * Tests for Credentials class, which extends Message class
 */
class CredentialsTest {
    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String CREDOP = "CRED";
    private static final String HASH = "25A4C9468BE3EEA2F5070AC28A49D3ED";
    private static final Credentials CREDENTIALS;
    private static final byte[] CREDENC = "CRED 25A4C9468BE3EEA2F5070AC28A49D3ED\r\n".getBytes(ENC);
    private static final byte[] CREDENCVAL = "CRED 25a4c9468be3eea2f5070ac28a49d3ed\r\n".getBytes(ENC);
    private static final byte[] CREDENCEOS = "CRED 25A4C9468BE3EEA2F5070AC28A4".getBytes(ENC);
    private static final String NEWHASH = "053C0BF3CCFDA915D77AE33E55D8F273";
    private static final String BADHASH = "053c0bf3ccfda915d77ae33e55d8f273";
    private static final String CREDSTR = "Credentials: hash=25A4C9468BE3EEA2F5070AC28A49D3ED";

    static {
        try {
            CREDENTIALS = new Credentials(HASH);
        } catch (ValidationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Tests if Credentials message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        CREDENTIALS.encode(mOut);
        assertArrayEquals(CREDENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Credentials messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Credentials message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation or hash are invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CREDENC));
            Credentials c = (Credentials) Message.decode(mIn);
            assertEquals(HASH, c.getHash());
            assertEquals(CREDOP, c.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CREDENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(CREDENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(CREDSTR, CREDENTIALS.toString());
    }

    /**
     * Tests getID return
     */
    @Test
    void getHashTest() {
        assertEquals(CREDENTIALS.getHash(), HASH);
    }

    /**
     * Tests getOperation return for Credentials messages
     */
    @Test
    void getOperationTest() {
        assertEquals(CREDENTIALS.getOperation(), CREDOP);
    }

    /**
     * Tests setHash method
     */
    @Nested
    class setHashTests {
        /**
         * Tests setHash with valid hash parameter
         */
        @Test
        void setHashTestValid() {
            try {
                Credentials c = new Credentials(HASH);
                c.setHash(NEWHASH);
                assertEquals(c.getHash(), NEWHASH);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests NullPointerException throw
         */
        @Test
        void setHashTestNull() {
            try {
                Credentials c = new Credentials(HASH);
                assertThrows(ValidationException.class, () -> {c.setHash(null);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad hash parameter
         */
        @Test
        void setHashTestBad() {
            try {
                Credentials c = new Credentials(HASH);
                assertThrows(ValidationException.class, () -> {c.setHash(BADHASH);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
