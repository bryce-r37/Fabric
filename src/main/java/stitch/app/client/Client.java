/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package stitch.app.client;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.Random;

import stitch.serialization.*;

/**
 * Stitch Client
 */
public class Client {

    /**
     * Maximum port available
     */
    private static final int MAXPORT = 65535;

    /**
     * Socket receive timeout (in milliseconds)
     */
    private static final int TIMEOUT = 4000;

    /**
     * Max 4 byte unsigned value (max queryID)
     */
    private static final long UINT_MAX = 0x0FFFFFFFFL;

    /**
     * Max payload size for UDP packet
     */
    public static final int UDPMAX = 65507;

    /**
     * Random number generator for queryIDs
     */
    private static final Random random = new Random(new Date().getTime());

    /**
     * Main method of Stitch Client. Makes a single request to Stitch server.
     *
     * @param args server IP/name, server port, requested maximum posts
     */
    public static void main(String[] args) {
        int port = 0;
        int reqPosts = 0;
        InetAddress address = null;

        // input validation
        if (args.length != 3) {
            System.err.println("Usage: java Client <server> <port> " +
                    "<requested max posts>");
            System.exit(1);
        }
        // validate port
        else if (!args[1].matches("[0-9]{1,5}") ||
                (port = Integer.parseInt(args[1])) > MAXPORT) {
            System.err.println("Validation Failed: invalid port number");
            System.exit(1);
        }
        // validate max posts
        else if (!args[2].matches("[0-9]{1,5}") ||
                (reqPosts = Integer.parseInt(args[2])) > 0x0FFFF) {
            System.err.println("Validation Failed: requested max posts must " +
                    "be between 0 and " + 0x0FFFF);
            System.exit(1);
        }
        // validate server
        try {
             address = Inet6Address.getByName(args[0]);
        } catch (UnknownHostException ex) {
            System.err.println("Validation Failed: unknown server address");
            System.exit(1);
        }

        // open udp socket
        try (DatagramSocket socket = new DatagramSocket()) {

            // set transmission variables
            socket.setSoTimeout(TIMEOUT);
            DatagramPacket packet;
            DatagramPacket response = new DatagramPacket(new byte[UDPMAX], UDPMAX);
            Response r;
            long rID;

            // send first query
            long id = random.nextLong(UINT_MAX + 1);
            packet = getQueryPacket(id, reqPosts, address, port);
            socket.send(packet);

            // receive response
            do {
                try {
                    socket.receive(response);
                } catch (SocketTimeoutException ex) {
                    // resend query if timeout
                    socket.send(packet);
                    socket.receive(response);
                }

                // decode response
                byte[] data = new byte[response.getLength()];
                System.arraycopy(response.getData(), 0, data, 0, data.length);
                r = new Response(data);
                rID = r.getQueryID();

                // ignore responses with incorrect queryID
            } while (rID != id);

            // print error if non-zero
            if (r.getErrorCode() != ErrorCode.NOERROR) {
                System.err.println(r.getErrorCode().getErrorMessage());
                System.exit(1);
            }

            // print posts
            List<String> posts = r.getPosts();
            for (String s : posts) {
                System.out.println(s);
            }

        } catch (IllegalArgumentException ex) {
            System.err.println("Validation Failed: " + ex.getMessage());
            System.exit(1);
        } catch (CodeException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Communication Failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Generate a DatagramPacket containing a Query with the correct values and
     * the correct destination address/port
     *
     * @param id queryID for Query
     * @param reqPosts number of posts requested for Query
     * @param address destination address
     * @param port destination port
     * @return fully formed DatagramPacket with the given values
     */
    private static DatagramPacket getQueryPacket(
            long id, int reqPosts, InetAddress address, int port)
            throws IllegalArgumentException {
        Message m = new Query(id, reqPosts);
        byte[] buffer = m.encode();
        return new DatagramPacket(buffer, buffer.length, address, port);
    }

    /**
     * Default constructor (unused)
     */
    protected Client() {}
}
