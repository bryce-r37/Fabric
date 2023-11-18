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
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Bout class, which extends Image class
 */
class BoutTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String BOUTOP = "BOUT";
    private static final Bout BOUT;
    private static final String CAT = "movie";
    private static final String NEWCAT = "food";
    private static final String BADCAT = "movie!";
    private static final byte[] IMG = "image".getBytes();
    private static final byte[] NEWIMG = "image2".getBytes();
    private static final byte[] BOUTENC = ("BOUT movie " + Base64.getEncoder().withoutPadding()
            .encodeToString(IMG) + "\r\n").getBytes(ENC);
    private static final byte[] BOUTENCVAL = ("BOUT movie! " + Base64.getEncoder().withoutPadding()
            .encodeToString(NEWIMG) + "\r\n").getBytes(ENC);
    private static final String BOUTSTR = "Bout: category=movie image=5 bytes";

    static {
        try {
            BOUT = new Bout(CAT, IMG);
        } catch (ValidationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Tests if Bout message encodes correctly to byte output
     *
     * @throws IOException if I/O problem
     */
    @Test
    void encodeTest() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MessageOutput mOut = new MessageOutput(bOut);
        BOUT.encode(mOut);
        assertArrayEquals(BOUTENC, bOut.toByteArray());
    }

    /**
     * Tests decode method for Bout messages
     */
    @Nested
    class decodeTests {
        /**
         * Tests happy-path decode for Bout message
         *
         * @throws NullPointerException if mIn or in is null
         * @throws ValidationException if operation is invalid
         * @throws IOException if I/O problem
         */
        @Test
        void decodeTestGood() throws NullPointerException, ValidationException, IOException {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(BOUTENC));
            Bout b = (Bout) Message.decode(mIn);
            assertEquals(BOUTOP, b.getOperation());
        }

        /**
         * Tests decode ValidationException throw for invalid operation
         */
        @Test
        void decodeTestInvalid() {
            MessageInput mIn = new MessageInput(new ByteArrayInputStream(BOUTENCVAL));
            assertThrows(ValidationException.class, () -> {Message.decode(mIn);});
        }
    }

    /**
     * Tests toString return
     */
    @Test
    void toStringTest() {
        assertEquals(BOUTSTR, BOUT.toString());
    }

    /**
     * Tests getCategory return
     */
    @Test
    void getCategoryTest() {
        assertEquals(CAT, BOUT.getCategory());
    }

    /**
     * Tests setCategory method
     */
    @Nested
    class setCategoryTests {
        /**
         * Tests setCategory with valid category parameter
         */
        @Test
        void setCategoryTestValid() {
            try {
                Bout b = new Bout(CAT, IMG);
                b.setCategory(NEWCAT);
                assertEquals(b.getCategory(), NEWCAT);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for bad category parameter
         */
        @Test
        void setCategoryTestBad() {
            try {
                Bout b = new Bout(CAT, IMG);
                assertThrows(ValidationException.class, () -> {b.setCategory(BADCAT);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Tests getImage return
     */
    @Test
    void getImageTest() {
        assertEquals(BOUT.getImage(), IMG);
    }

    /**
     * Tests getOperation return for Bout messages
     */
    @Test
    void getOperationTest() {
        assertEquals(BOUT.getOperation(), BOUTOP);
    }

    /**
     * Tests setImage method
     */
    @Nested
    class setImageTests {
        /**
         * Tests setImage with valid image parameter
         */
        @Test
        void setImageTestValid() {
            try {
                Bout b = new Bout(CAT, IMG);
                b.setImage(NEWIMG);
                assertEquals(b.getImage(), NEWIMG);
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }

        /**
         * Tests ValidationException throw for null image parameter
         */
        @Test
        void setImageTestBad() {
            try {
                Bout b = new Bout(CAT, IMG);
                assertThrows(ValidationException.class, () -> {b.setImage(null);});
            } catch (ValidationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
