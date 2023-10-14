/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
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
 * Tests for Ack class, which extends Message class
 */
class AckTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String ACKOP = "ACK";
    private static final Ack ACK;
    private static final byte[] ACKENC = "ACK\r\n".getBytes(ENC);
    private static final byte[] ACKENCVAL = "ACK \r\n".getBytes(ENC);
    private static final byte[] ACKENCVAL2 = "ACC\r\n".getBytes(ENC);
    private static final byte[] ACKENCEOS = "ACK\r".getBytes(ENC);
    private static final String ACKSTR = "Ack";

    static {
        ACK = new Ack();
    }

    /**
     * Tests if Ack message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        ACK.encode(mOut);
        assertArrayEquals(ACKENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Ack messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Ack message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation is invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ACKENC));
            Ack a = (Ack) Message.decode(mIn);
            assertEquals(ACKOP, a.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid encoding
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ACKENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid2() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ACKENCVAL2));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(ACKENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(ACKSTR, ACK.toString());
    }

    /**
     * Tests getOperation return
     */
    @Test
    void getOperationTest() {
        assertEquals(ACK.getOperation(), ACKOP);
    }

}