import exceptions.MissingHostnameParametersException;
import exceptions.MissingPortParametersException;
import exceptions.WrongModeParametersException;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
/**
 * @author Jorge Vinagre, Chang Ye.
 */

/**
 * Class that allow the connection between server and client and also handle with all the logic of the game(by client).
 */
public class Client {

    private static final HashMap<String, String> options = new HashMap<>();
    private static String[] arguments;
    private static String hostname;
    private static int port, mode = 0;
    private static Socket socket;

    /**
     * Main class that run all the functionality, it checks the correct way to enter the client command to
     * establish connection, it opens the connection to server and also handle the Client logic.
     * @param args
     */
    public static void main(String[] args) {
        arguments = args;
        mode = 0;
        boolean b;
        try {
            b = checkArgs();
            if (b) {
                openConnection();
                handleGame();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method that stores the params in a hash map in order to handle the correct introducing format.
     * @param args
     */
    // Guardem parametres en un hash map
    private static void setOptions(String[] args) {
        for (int i = 0; i < args.length; i = i+2) {
            options.put(args[i], args[i+1]);
        }
    }

    /**
     * @return true if parameters are well introduced or false if there's an error.
     * @throws MissingHostnameParametersException
     * @throws MissingPortParametersException
     * @throws WrongModeParametersException
     */
    private static boolean checkArgs() throws MissingHostnameParametersException, MissingPortParametersException, WrongModeParametersException {
        if (arguments.length == 1 && arguments[0].equals("-h")) {
            helpMessage();
            return false;
        } else if (arguments.length == 0 || arguments.length % 2 == 1) {
            helpMessage();
            return false;
        }
        setOptions(arguments);

        if (!options.containsKey("-s")) throw new MissingHostnameParametersException("Hostname of the server  was not specified!");
        if (!options.containsKey("-p")) throw new MissingPortParametersException("Port of the server was not specified!");

        // Get hostname and port
        hostname = options.get("-s");
        port = Integer.parseInt(options.get("-p"));

        // Get game mode
        if (options.containsKey("-i")) {
            mode = Integer.parseInt(options.get("-i"));
            if (mode != 0 && mode != 1) throw new WrongModeParametersException("The specified mode doesn't exists!");
        }

        if (options.get("-s") == null || options.get("-p") == null) {
            helpMessage();
            return false;
        }
        return true;
    }

    /**
     * Method to open the connection socket and establish connection with server.
     * @throws IOException
     */
    public static void openConnection() throws IOException {
        try {
            socket = new Socket(hostname, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Port " + port + " closed");
            System.exit(-1);
        }
        socket.setSoTimeout(500 * 1000);
        System.out.println("Connection with server established");
    }

    /**
     * Method to close connection socket.
     * @throws IOException
     */
    public static void closeConnection() throws IOException {
        socket.close();
    }

    /**
     * Method to handle the Client logic.
     * @throws IOException
     */
    private static void handleGame() throws IOException {
        ClientLogic game = new ClientLogic(socket, mode);
        game.handleMenu();
    }

    /**
     * Help message to throw when parameters aren't well introduced.
     */
    private static void helpMessage() {
        System.out.println("Us: java -jar client -s <maquina_servidora> -p <port>  [-i 0|1]");
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public static int getMode() {
        return mode;
    }
}
