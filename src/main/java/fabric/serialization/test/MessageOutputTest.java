/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization.test;

import fabric.serialization.MessageOutput;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for MessageOutput class
 */
class MessageOutputTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String STRING = "testing";
    private static final byte[] STRINGENC = "testing".getBytes(ENC);
    private static final byte[] SPACEENC = " ".getBytes(ENC);
    private static final byte[] RETENC = "\r\n".getBytes(ENC);

    /**
     * Tests if constructor throws NullPointerException when out is null
     */
    @Test
    void constructorTestNull() {
        assertThrows(NullPointerException.class, () -> {new MessageOutput(null);});
    }
}