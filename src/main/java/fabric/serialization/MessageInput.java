/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 4
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Deserialization input source
 */
public class MessageInput {

    /**
     * Encoding to use for deserialization
     */
    private static final Charset ENC = StandardCharsets.ISO_8859_1;

    /**
     * Reader Object to make use of charset through stream
     */
    private final InputStreamReader reader;

    /**
     * Constructs a new input source from an InputStream
     *
     * @param in byte input source
     * @throws NullPointerException if in is null
     */
    public MessageInput(InputStream in) throws NullPointerException {
        reader = new InputStreamReader(
                Objects.requireNonNull(in, "Null input stream"), ENC);
    }

    /**
     * Reads the next delimited value from in and returns it to caller
     *
     * @return value read from in
     * @throws IOException if I/O problem
     */
    String readOp() throws IOException {
        int c;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            c = reader.read();
            if (c == -1) {
                throw new IOException("Premature EOS");
            }
            stringBuilder.append((char) c);
        } while (c != Message.TERM1 && c != Message.DELIM);
        return stringBuilder.toString();
    }

    /**
     * Reads from in to the end of a Fabric Message
     *
     * @return String containing all bytes read
     * @throws IOException if I/O problem
     */
    String readLine() throws IOException {
        int c;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            c = reader.read();
            if (c == -1) {
                throw new IOException("Premature EOS");
            }
            stringBuilder.append((char) c);
        } while (c != Message.TERM2);
        return stringBuilder.toString();
    }
}
