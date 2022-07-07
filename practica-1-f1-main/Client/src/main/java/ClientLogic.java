import model.Game;
import model.GameMode;
import model.GameState;
import packets.ClientProtocol;
import utils.Dictionary;
//import utils.Dictionary;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jorge Vinagre, Chang Ye.
 */

/**
 * Client Logic class that handle all the working done by the client.
 */
public class ClientLogic {

    private int gameMode;
    private final ClientProtocol clientProtocol;
    private Dictionary dict;
    private final Game game;
    private final Socket socket;
    private boolean exitStatus;
    private int id, turns_cnt = 0;
    Scanner scanner = new Scanner(System.in);

    /**
     * Degault constructor of the ClientLogic class
     * @param socket Socket used to establish connection with server
     * @param gameMode Game mode to decide if playing with manual or automatic mode
     * @throws IOException
     */
    public ClientLogic(Socket socket, int gameMode) throws IOException {
        clientProtocol = new ClientProtocol(socket);
        this.gameMode = gameMode;
        this.socket = socket;
        this.game = new Game();
        this.exitStatus = false;

        if(gameMode == 1) dict = new Dictionary();
    }

    /**
     * Method to handle all the possible states during the client-server communication.
     * @throws IOException
     */
    public void handleMenu() throws IOException {
        if (gameMode == 0) {
            manual_mode();
        }else {
            automatic_mode();
        }

        Client.closeConnection();
        System.out.println("Connection closed.");
        System.exit(0);
    }

    /**
     * Method to handle showing stats to the player.
     * @throws IOException
     */
    private void handle_stats() throws IOException {
        String stats = "STATS  C <------ 7 ";
        stats += clientProtocol.read_message();
        System.out.println(stats);

        System.out.println("Do you want to play again? [1 == yes 0 == no]");
        int again = scanner.nextInt();
        if (again == 1) {
            game.setGameState(GameState.SENDING_PLAY);
        } else if (again == 0) {
            exitStatus = true;
        } else {
            int a = 0;
            while (a != 1) {
                System.out.println("ERROR: The answer is binary, must be 0 or 1");
                System.out.println("Do you want to play again? [1 == yes 0 == no]");
                again = scanner.nextInt();
                if (again == 1 || again == 0) a = 1;
            }
            if (again == 1) game.setGameState(GameState.SENDING_PLAY);
            else exitStatus = true;
        }
    }

    /**
     * Method to show the actual result and show different stats.
     * @throws IOException
     */
    private void handle_result() throws IOException {
        String result = clientProtocol.read_message();
        if (result.equals("^^^^^") || turns_cnt == 6) {
            if (turns_cnt == 6) clientProtocol.read_message(); // Read word
            game.setGameState(GameState.READING_STATS); // Won --> show stats
            turns_cnt = 0;
        }
        else {
            game.setGameState(GameState.SENDING_WORD);
        }
    }

    /**
     * Method to handle properly the word to send.
     * @throws IOException
     */
    private void handle_word() throws IOException {
        System.out.println("Send a word with 5 letters in catalan");
        String word = scanner.next();
        if (word.length() != 5) {
            System.out.println("ERROR: Must be a 5 letters word");
        } else {
            clientProtocol.send_word(word);
            game.setGameState(GameState.READING_RESULT);
            turns_cnt ++;
        }
    }

    private void handle_word_automatic(List<String> l) throws IOException {
        String s;
        // Try to choose a word that we haven't used before
        do {
            s = dict.getWord();
        }while(l.contains(s));

        clientProtocol.send_word(s);
        l.add(s);
        turns_cnt++;
        game.setGameState(GameState.READING_RESULT);
    }

    /**
     * Method to handle the admit command received from the server.
     * @throws IOException
     */
    private void handle_admit() throws IOException {
        int read_bool = Integer.parseInt(clientProtocol.read_message());
        if (read_bool == 0) {
            clientProtocol.send_error(4);
            game.setGameState(GameState.SENDING_PLAY);
        } else {
            System.out.println("Starting game...");
            game.setGameState(GameState.SENDING_WORD);
        }
    }

    /**
     * Method to handle sending play command to server.
     * @throws IOException
     */
    private void handle_play() throws IOException {
        clientProtocol.send_play(id);
        game.setGameState(GameState.READING_ADMIT);
    }

    /**
     * Method to handle the ready command received from server and go to the appropriate state.
     * @throws IOException
     */
    private void handle_ready() throws IOException {
        int read_id = Integer.parseInt(clientProtocol.read_message());
        // If player write 0 (new_game) we set the player a new random id.
        if (id == 0) {
            this.id = read_id;
            game.setGameState(GameState.SENDING_PLAY);
            // If player write his used sessionID we check if server send it correctly, if it does we
            // can move to sending_play state.
        } else {
            if (read_id != id) {
                String error = clientProtocol.read_message();
                System.out.println(error);
                game.setGameState(GameState.SENDING_HELLO);
            } else {
                game.setGameState(GameState.SENDING_PLAY);
            }
        }
    }

    /**
     * Method to send hello command to server and go to the next appropriate state.
     * @throws IOException
     */
    private void handle_hello() throws IOException {
        System.out.println("Introduce 0 for new game or enter the id of your last session");
        this.id = scanner.nextInt();
        System.out.println("Introduce your name (if you want to continue your last session it must " +
                "be the same)");
        String name = scanner.next();
        clientProtocol.send_hello(id, name);
        game.setGameState(GameState.READING_READY);
    }

    private void manual_mode() throws IOException {
        System.out.println("MANUAL MODE");
        while (!exitStatus) {
            switch (game.getGameState()) {
                case SENDING_HELLO: // To establish connection with server
                    handle_hello();
                    break;
                case READING_READY:  // To handle the server response
                    handle_ready();
                    break;
                case SENDING_PLAY: // To sending server the play petition
                    handle_play();
                    break;
                case READING_ADMIT:  // To receive the server admit
                    handle_admit();
                    break;
                case SENDING_WORD:  // To send a word
                    handle_word();
                    break;
                case READING_RESULT: // To read the result of our word
                    handle_result();
                    break;
                case READING_STATS: // To show the stats of the game
                    handle_stats();
                    break;
            }
        }
    }

    private void automatic_mode() throws IOException{
        System.out.println("AUTOMATIC MODE");
        List<String> usedwords = new ArrayList<>();

        while (!exitStatus) {
            switch (game.getGameState()) {
                case SENDING_HELLO: // To establish connection with server
                    handle_hello();
                    break;
                case READING_READY:  // To handle the server response
                    handle_ready();
                    break;
                case SENDING_PLAY: // To sending server the play petition
                    handle_play();
                    break;
                case READING_ADMIT:  // To receive the server admit
                    handle_admit();
                    break;
                case SENDING_WORD:  // To send a word
                    handle_word_automatic(usedwords);
                    break;
                case READING_RESULT: // To read the result of our word
                    handle_result();
                    break;
                case READING_STATS: // To show the stats of the game
                    usedwords.clear(); // clear used words in case the player want to play again
                    handle_stats();
                    break;
            }
        }
    }
}
