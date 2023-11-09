/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 5
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app.server;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Y allows you to Yip and Yell about the inane.
 * Why Y?  Because X; that's Y.
 * 
 * Appends HTML updates to a specified file
 * 
 * @version 1.0
 */
public class Y {

  /**
   * File to update
   */
  private final OutputStream file;

  /**
   * File encoding
   */
  private static final Charset FILEENC = StandardCharsets.ISO_8859_1;

  /**
   * Create Updater
   * 
   * @param filename name of file to update
   *
   * @throws FileNotFoundException if named file not found
   */
  public Y(String filename) throws FileNotFoundException {
    this.file = new FileOutputStream(filename, true);
  }

  /**
   * Write single update with image
   * 
   * @param status update status
   * @param img    image bytes (jpeg only).
   * 
   * @throws IOException if I/O problem or cannot decode image
   */
  public void updateWithImage(String status, byte[] img) throws IOException {
    writeString("<img width=\"25%\" src=\"data:image/jpeg;base64,");
    writeString(new String(Base64.getEncoder().withoutPadding()
            .encode(img), FILEENC));
    writeString("\"><br/>");
    update(status);
  }

  /**
   * Write single update
   * 
   * @param status update status
   * 
   * @throws IOException if I/O problem or cannot decode image
   */
  public void update(String status) throws IOException {
    writeString(sanitizeString(status) + "<hr><p/>\n");
  }

  /**
   * Write encoded string to Updater file
   * 
   * @param s update string
   * 
   * @throws IOException if I/O problem
   */
  private void writeString(String s) throws IOException {
    file.write(s.getBytes(FILEENC));
  }

  /**
   * Cheesy string sanitizing for string
   * 
   * @param s string to sanitize
   * 
   * @return sanitized string
   */
  private String sanitizeString(String s) {
    return s.replace("<", "&lt;").replace(">", "&gt;").replace("/", "");
  }
}
