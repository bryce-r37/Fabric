/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package fabric.app.server;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;

/**
 * Structure to store userIDs, passwords, and sequence numbers for a server
 */
public class Users {
    /**
     * Mapping of userIDs to passwords
     */
    private final HashMap<String, String> psswd = new HashMap<>();

    /**
     * Mapping of userIDs to sequence numbers
     */
    private final HashMap<String, Integer> seq = new HashMap<>();

    /**
     * Error message for constructor exceptions
     */
    private static final String FORMATERR = "Improper password line format";

    /**
     * Charset used to encode password hashes
     */
    private static final Charset ENC = StandardCharsets.ISO_8859_1;

    /**
     * Hashing method for password hashes
     */
    private static final String HASH = "MD5";

    /**
     * Constructor for Users object
     *
     * @param file file containing userIDs and passwords in the format
     *             "userID:password\r\n"
     * @throws IOException if improper line format
     */
    public Users(String file) throws IOException {
        Path path = Paths.get(file);
        InputStreamReader in = new InputStreamReader(
                new FileInputStream(path.toFile()), ENC);
        String uid, pass;
        StringBuilder stringBuilder;
        int c;

        // Read all uids and passwords from file
        while (in.ready()) {
            // Read uid from file
            stringBuilder = new StringBuilder();
            while ((c = in.read()) != ':') {
                if (c == -1 || c == '\r') {
                    throw new IOException(FORMATERR);
                }
                stringBuilder.append((char) c);
            }
            uid = stringBuilder.toString();

            // Read password from file
            stringBuilder = new StringBuilder();
            while ((c = in.read()) != '\r') {
                if (c == -1 || c == '\n') {
                    throw new IOException(FORMATERR);
                }
                stringBuilder.append((char) c);
            }
            pass = stringBuilder.toString();

            // Check line terminator
            if (in.read() != '\n') {
                throw new IOException(FORMATERR);
            }

            // Store uid and password in map
            psswd.put(uid, pass);
            // Store uid and seq number in map
            seq.put(uid, 0);
        }
    }

    /**
     * Checks if a userID is included in the Users
     *
     * @param uid userID to be searched for
     * @return true if uid is found; false otherwise
     */
    public boolean contains(String uid) {
        return psswd.containsKey(uid);
    }

    /**
     * Generates a hash of the given nonce and the password corresponding
     * with the given userID
     *
     * @param nonce nonce to be used in hash generation
     * @param uid userID corresponding with password to be used in
     *            hash generation
     * @return generated hash, or null if hash generation fails
     */
    public String hash(String nonce, String uid) {
        MessageDigest md;

        // Get object for nonce-password MD5 hash generation
        try {
            md = MessageDigest.getInstance(HASH);
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }

        // Generate hash (no null pointer since program terminates in catch)
        md.update((nonce + psswd.get(uid))
                .getBytes(ENC));

        // return hash
        return HexFormat.of().withUpperCase().formatHex(md.digest());
    }

    /**
     * Increments the sequence number for the given userID
     *
     * @param uid userID corresponding with sequence number to be updated
     */
    public void update(String uid) {
        seq.replace(uid, seq.get(uid) + 1);
    }

    /**
     * Returns the sequence number for the given userID
     *
     * @param uid userID corresponding with sequence number to be returned
     * @return sequence number corresponding with the given userID
     */
    public Integer getSequence(String uid) {
        return seq.get(uid);
    }
}
