/************************************************
 *
 * Author: Bryce Robinson
 * Assignment: Program 6
 * Class: CSI 4321
 *
 ************************************************/

package stitch.app.server;

import stitch.serialization.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static stitch.app.client.Client.UDPMAX;
import static stitch.serialization.Response.RLEN;

/**
 * Stitch server implementation (runs as a thread in Fabric Server)
 */
public class Server implements Runnable {

    /**
     * Port number on which the server is running
     */
    private final int port;

    /**
     * List to store all posts since server last restarted
     */
    private final List<String> posts;

    /**
     * Static Stitch Server logger
     */
    private static final Logger logger = Logger.getLogger("stitchServer");

    /**
     * Name of file where Stitch logs are written
     */
    private static final String STITCHLOG = "stitch.log";

    // static logger initialization
    static {
        logger.setLevel(Level.FINE);
        try {
            FileHandler file = new FileHandler(STITCHLOG);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.setUseParentHandlers(false);
        } catch (IOException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Default constructor for Stitch server instance
     *
     * @param port port on which to open the UDP port
     */
    public Server(int port) {
        this.port = port;
        this.posts = new ArrayList<>();
    }

    /**
     * Adds a new post to the Stitch Server list
     *
     * @param post String representation of new post
     */
    public void addPost(String post) {
        posts.add(post);
    }

    /**
     * Default run method for Stitch server
     */
    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            DatagramPacket packet;

            while (true) {
                Query q = null;
                Response r = null;
                packet = new DatagramPacket(new byte[UDPMAX], UDPMAX);

                // receive client query
                try {
                    q = receiveQuery(socket, packet);
                    Query finalQ = q;
                    logger.info(() -> "Received message: " + finalQ);
                } catch (CodeException ex) {
                    logger.warning(ex::getMessage);
                    r = new Response(0, ex.getErrorCode(), new ArrayList<>());
                }

                // construct response
                if (q != null) {
                    r = generateResponse(q.getRequestedPosts(), q.getQueryID());
                }

                // send response
                if (r != null) {
                    packet.setData(r.encode());
                    packet.setLength(packet.getData().length);
                    try {
                        Response finalR = r;
                        logger.info(() -> "Sending message: " + finalR);
                        socket.send(packet);
                    } catch (IOException ex) {
                        logger.warning(() -> "Unable to communicate: " + ex.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            logger.severe(() -> "Unable to start: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * Receives a query from the given socket into the given packet and returns
     * a deserialized representation
     *
     * @param socket the socket to receive from
     * @param packet the packet to receive into
     * @return the received Query
     * @throws CodeException if invalid Query
     */
    private Query receiveQuery(DatagramSocket socket, DatagramPacket packet)
            throws CodeException {
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            logger.warning(() -> "Unable to communicate: " + ex.getMessage());
            return null;
        }
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, data, 0, data.length);
        return new Query(data);
    }

    /**
     * Generates a Response with the appropriate size post list to be
     * serialized for transmission to client
     *
     * @param requestedPosts number of posts client has requested
     * @param queryID queryID for Client Query
     * @return the generated Response
     */
    private Response generateResponse(int requestedPosts, long queryID) {
        int count = Math.min(requestedPosts, posts.size());
        List<String> subset = posts.subList(0, count);
        int size = 0;
        for (int i = 0; i < subset.size(); ++i) {
            size += subset.get(i).length();
            if (size > UDPMAX - RLEN || i >= Response.MAXPOSTS) {
                subset = subset.subList(0, i);
                break;
            }
        }
        return new Response(queryID, ErrorCode.NOERROR, subset);
    }
}
