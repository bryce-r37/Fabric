/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 3
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization.test;

import fabric.serialization.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ValidationException class
 */
class ValidationExceptionTest {

    private static final ValidationException EX = new ValidationException("message", "token");
    private static final String TOKEN = "token";

    /**
     * Tests getBadToken return
     */
    @Test
    void getBadTokenTest() {
        assertEquals(TOKEN, EX.getBadToken());
    }
}