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
 * Tests for ID class, which extends Message class
 */
class IDTest {
    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String IDOP = "ID";
    private static final String IDMSG = "14hg6kd32";
    private static final ID ID;
    private static final byte[] IDENC = "ID 14hg6kd32\r\n".getBytes(ENC);
    private static final byte[] IDENCVAL = "ID 14hg&6kd$32\r\n".getBytes(ENC);
    private static final byte[] IDENCEOS = "ID 14hg6k".getBytes(ENC);
    private static final String NEWMSG = "67fa32";
    private static final String BADMSG = "*&()~";
    private static final String IDSTR = "ID: id=14hg6kd32";

    static {
        try {
            ID = new ID(IDMSG);
        } catch (ValidationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Tests if ID message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        ID.encode(mOut);
        assertArrayEquals(IDENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for ID messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for ID message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation or ID are invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(IDENC));
            ID i = (ID) Message.decode(mIn);
            assertEquals(IDMSG, i.getID());
            assertEquals(IDOP, i.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(IDENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }

        /**
         * Tests decode IOException throw for early EOS
         */
        @Test
        void decodeTestEOS() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(IDENCEOS));
            assertThrows(IOException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(IDSTR, ID.toString());
    }

    /**
     * Tests getID return
     */
    @Test
    void getIDTest() {
        assertEquals(ID.getID(), IDMSG);
    }

    /**
     * Tests getOperation return for ID messages
     */
    @Test
    void getOperationTest() {
        assertEquals(ID.getOperation(), IDOP);
    }

    /**
     * Tests setID method
     */
    @Nested
    class setIDTests {
        /**
         * Tests setID with valid message parameter
         */
        @Test
        void setIDTestValid() {
            try {
                ID i = new ID(IDMSG);
                i.setID(NEWMSG);
                assertEquals(i.getID(), NEWMSG);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests NullPointerException throw
         */
        @Test
        void setIDTestNull() {
            try {
                ID i = new ID(IDMSG);
                assertThrows(ValidationException.class, () -> {i.setID(null);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad id parameter
         */
        @Test
        void setIDTestBad() {
            try {
                ID i = new ID(IDMSG);
                assertThrows(ValidationException.class, () -> {i.setID(BADMSG);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
