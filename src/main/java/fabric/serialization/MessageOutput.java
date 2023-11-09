/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Serialization output sink
 */
public class MessageOutput {

    /**
     * Encoding to use for serialization
     */
    private static final Charset ENC = StandardCharsets.ISO_8859_1;

    /**
     * Output stream to which serialized bytes will be written
     */
    private final OutputStream out;

    /**
     * Constructs a new output sink from an OutputStream
     *
     * @param out byte output sink
     * @throws NullPointerException if out is null
     */
    public MessageOutput(OutputStream out) throws NullPointerException {
        this.out = Objects.requireNonNull(out, "Null output stream");
    }

    /**
     * Writes the provided strings to out delimited by message delimiter and
     * terminated by the two char message terminator sequence
     *
     * @param a the series of strings to be written to out
     * @throws IOException if I/O problem
     */
    void writeMessage(String... a) throws IOException {
        for (int i = 0; i < a.length; ++i) {
            out.write(a[i].getBytes(ENC));
            out.write(i == a.length - 1 ?
                    ("" + Message.TERM1 + Message.TERM2).getBytes(ENC) :
                    ("" + Message.DELIM).getBytes(ENC));
        }
    }
}
