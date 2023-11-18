/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.serialization;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * Represents a Bout message and provides serialization/deserialization
 */
public class Bout extends Message {

    /**
     * Operation ID for Bout messages
     */
    private static final String BOUTOP = "BOUT";

    /**
     * Activity category
     */
    private String category;

    /**
     * Activity image
     */
    private byte[] image;

    /**
     * Encoding to use image byte[] String conversions
     */
    private static final Charset ENC = StandardCharsets.ISO_8859_1;

    /**
     * Constructs bout message using given values
     *
     * @param category activity category
     * @param image activity image
     * @throws ValidationException if validation fails
     */
    public Bout(String category, byte[] image) throws ValidationException {
        this.category = validateCategory(category);
        this.image = validateImage(image);
    }

    /**
     * Constructs Bout from MessageInput
     *
     * @param in message input object
     * @throws ValidationException if data from input is invalid
     * @throws IOException if I/O problem
     */
    protected Bout(MessageInput in) throws ValidationException, IOException {
        String msg = Message.validateFabricMessage(in.readLine());

        // Check that category can be parsed as an alphanumeric value
        String category = msg.substring(0, msg.indexOf(' '));
        if (!category.matches("^[\\w]+$" )) {
            throw new ValidationException("Bad category in Bout", category);
        }
        this.category = validateCategory(category);

        // Check that image is Base64 encoding with no padding
        msg = msg.substring(msg.indexOf(' ') + 1);
        this.image = validateImage(Base64.getDecoder().decode(msg));
    }

    /**
     * Returns a String representation
     *
     * @return a String representation
     */
    public String toString() {
        return "Bout: category=" + this.category + " image=" +
                this.image.length + " bytes";
    }

    /**
     * Returns category
     *
     * @return category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Sets category
     *
     * @param category new category
     * @return this object with new category
     * @throws ValidationException if null or invalid category
     */
    public Bout setCategory(String category) throws ValidationException {
        this.category = validateCategory(category);
        return this;
    }

    /**
     * Returns image
     *
     * @return image
     */
    public byte[] getImage() {
        return this.image;
    }

    /**
     * Sets image
     *
     * @param image new image encoding
     * @return this object with new image
     * @throws ValidationException if null image
     */
    public Bout setImage(byte[] image) throws ValidationException {
        this.image = validateImage(image);
        return this;
    }

    /**
     * Returns message operation
     *
     * @return message operation
     */
    @Override
    public String getOperation() {
        return BOUTOP;
    }

    /**
     * Encodes a Bout to output
     *
     * @param out serialization output sink
     * @throws NullPointerException if out is null
     * @throws IOException if I/O problem
     */
    @Override
    public void encode(MessageOutput out) throws NullPointerException, IOException {
        out.writeMessage(BOUTOP, category, Base64.getEncoder().withoutPadding()
                .encodeToString(image));
    }

    /**
     * Returns whether a Bout is equal to Object o
     *
     * @param o second object to be compared
     * @return boolean representation of whether object and Bout are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(this.category,
                ((Bout) o).category) && Arrays.equals(this.image,
                ((Bout) o).image);
    }

    /**
     * Returns an integer hash of a Bout for use in collections
     *
     * @return an integer hash representing the Bout
     */
    @Override
    public int hashCode() {
        return Objects.hash(BOUTOP, this.category,
                Arrays.hashCode(this.image));
    }

    /**
     * Validates that category is 1+ of alphanum
     *
     * @param category activity category
     * @return category
     * @throws ValidationException if validation fails
     */
    private static String validateCategory(String category) throws ValidationException {
        // Validate that category is 1+ of alphanumeric
        if (category == null || !category.matches("^[\\w]+$")) {
            throw new ValidationException("Invalid category", category);
        }
        return category;
    }

    /**
     * Validates that image is not null
     *
     * @param image activity image
     * @return image
     * @throws ValidationException if validation fails
     */
    private static byte[] validateImage(byte[] image) throws ValidationException {
        // Validate that image is not null
        if (image == null) {
            throw new ValidationException("Null image", null);
        }
        return image;
    }
}
