import Game.*;
import com.google.gson.JsonObject;
import utils.Dictionary;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class of server
 */
public class Server {

    private static final HashMap<String, String> options = new HashMap<>();
    private static final Dictionary dictionary = new Dictionary();
    private static ServerSocket serverSocket;
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;
        boolean b = checkArgs();

        // Start server if input are correct
        if (b) {
            startServer();
        }
    }

    /**
     * Store the arguments locally which will be accessed later
     * @param args which contains the arguments passed
     */
    private static void setOptions(String[] args) {
        for (int i = 0; i < args.length; i = i+2) {
            options.put(args[i], args[i+1]);
        }
    }

    /**
     * Checks the input arguments.
     * @return a boolean indicating if the passed parameters are legal or not.
     */
    private static boolean checkArgs() {
        if (arguments.length == 1 && arguments[0].equals("-h")) {
            helpMessage();
            return false;
        } else if (arguments.length != 2) {
            helpMessage();
            return false;
        }
        setOptions(arguments);
        if (options.get("-p") == null) {
            helpMessage();
            return false;
        }
        return true;
    }

    /**
     * Method that shows a message that tells the user how to use this class
     */
    private static void helpMessage() {
        System.out.println("Us: java -jar server.jar -p <port> ");
    }

    /**
     * Creates the Server Socket and starts accepting connections.
     */
    private static void startServer() {
        try {
            createServerSocket();
            startAcceptingConnections();
        } catch (IOException | NumberFormatException e) {
            if (e instanceof IOException) {
                System.out.println("IOException: " + e.getMessage());
            } else {
                System.out.println("NumberFormatException: " + e.getMessage());
            }
            helpMessage();
        }
    }

    /**
     * Method that looks if there are clients who want to connect to our server
     * @throws IOException in case something goes wrong with IO
     */
    private static void startAcceptingConnections() throws IOException {
        System.out.println("SERVER IP: " + InetAddress.getLocalHost().getHostAddress());
        System.out.println("SERVER LISTENING PORT: " + options.get("-p"));
        Socket socket;

        ConcurrentHashMap<Integer, String> sessions = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, JsonObject> stats = new ConcurrentHashMap<>();
        do {
            socket = serverSocket.accept();
            socket.setSoTimeout(1000000);
            Thread server = new Thread(new GameThread(socket, dictionary, sessions, stats));
            server.start();
        } while (!serverSocket.isClosed());
        serverSocket.close();
    }

    /**
     * Method that initializes a serverSocket
     * @throws IOException in case something goes wrong with IO
     */
    private static void createServerSocket() throws IOException {
        int port = Integer.parseInt(options.get("-p"));
        serverSocket = new ServerSocket(port);
    }
}