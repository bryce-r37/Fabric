/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 3
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
 * Tests for Knowp class, which extends Message class
 */
class KnowpTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String KNOWPOP = "KNOWP";
    private static final Knowp KNOWP;
    private static final byte[] KNOWPENC = "KNOWP\r\n".getBytes(ENC);
    private static final byte[] KNOWPENCVAL = "KNOWP \r\n".getBytes(ENC);
    private static final byte[] KNOWPENCVAL2 = "KNOWp\r\n".getBytes(ENC);
    private static final byte[] KNOWPENCEOS = "KNOWP\r".getBytes(ENC);
    private static final String KNOWPSTR = "Knowp";

    static {
        KNOWP = new Knowp();
    }

    /**
     * Tests if Knowp message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        KNOWP.encode(mOut);
        assertArrayEquals(KNOWPENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Knowp messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Knowp message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation is invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(KNOWPENC));
            Knowp k = (Knowp) Message.decode(mIn);
            assertEquals(KNOWPOP, k.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid encoding
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(KNOWPENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid2() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(KNOWPENCVAL2));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(KNOWPENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(KNOWPSTR, KNOWP.toString());
    }

    /**
     * Tests getOperation return
     */
    @Test
    void getOperationTest() {
        assertEquals(KNOWP.getOperation(), KNOWPOP);
    }

}