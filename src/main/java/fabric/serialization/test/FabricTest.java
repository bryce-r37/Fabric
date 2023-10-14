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
 * Tests for Fabric class, which extends Message class
 */
class FabricTest {
    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String FABRICOP = "FABRIC";
    private static final Fabric FABRIC;
    private static final byte[] FABENC = "FABRIC 1.0\r\n".getBytes(ENC);
    private static final byte[] FABENCVAL = "FABRIC 1.0 \r\n".getBytes(ENC);
    private static final byte[] FABENCVAL2 = "FABRIC 2.0\r\n".getBytes(ENC);
    private static final byte[] FABENCEOS = "FABRIC 1.".getBytes(ENC);
    private static final String FABSTR = "Fabric 1.0";

    static {
        FABRIC = new Fabric();
    }

    /**
     * Tests if Fabric message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        FABRIC.encode(mOut);
        assertArrayEquals(FABENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Fabric messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Fabric message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation is invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(FABENC));
            Fabric f = (Fabric) Message.decode(mIn);
            assertEquals(FABRICOP, f.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid encoding
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(FABENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid2() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(FABENCVAL2));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(FABENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(FABSTR, FABRIC.toString());
    }

    /**
     * Tests getOperation return
     */
    @Test
    void getOperationTest() {
        assertEquals(FABRIC.getOperation(), FABRICOP);
    }
}
