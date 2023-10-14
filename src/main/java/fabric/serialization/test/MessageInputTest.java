/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization.test;

import fabric.serialization.MessageInput;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for MessageInput class
 */
class MessageInputTest {

    private static final Charset ENC = StandardCharsets.ISO_8859_1;
    private static final String ELEMENTS = "testing and more\r\n";
    private static final String FIRST = "testing";
    private static final String STRING = "testing\r\n";
    private static final String END = "and more";
    private static final byte[] STRINGENC = "testing\r\n".getBytes(ENC);
    private static final int INDX = 0;
    private static final int ENDX = 1;

    /**
     * Tests if constructor throws NullPointerException when in is null
     */
    @Test
    void constructorTestNull() {
        assertThrows(NullPointerException.class, () -> {new MessageInput(null);});
    }
}