package Game;

import com.google.gson.*;
import packets.ServerProtocol;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import utils.Dictionary;
import utils.ErrorsEnum;
import utils.ServerLog;

/**
 * GameServer class which is the gameLogic for server
 */
public class GameServer{

    private final ServerLog log;
    private final ServerProtocol protocol;
    private final Dictionary dict;
    private final GameStats stats;
    private int state = 1;

    private final ConcurrentHashMap<Integer, String> sessions;
    private final List<String> words = new ArrayList<>();
    private int currentId, currentIntent = 0;
    private String currentWord;
    private final Socket socket;

    /**
     * Default constructor of GameServer.
     * @param socket socket used to communicate between client and server
     * @param dict dictionary class that contains the dictionary used for WORDLE
     * @param sessions HashMap that stores the sessions of every client
     * @param stats HashMap that will be used the GameStats class which contains the stats of every client
     * @param logger ServerLogger class that has methods that show us the logs of the server
     * @throws IOException serverProtocol fins an error
     */
    public GameServer(Socket socket, Dictionary dict, ConcurrentHashMap<Integer, String> sessions, ConcurrentHashMap<Integer, JsonObject> stats, ServerLog logger) throws IOException {
        this.socket = socket;
        this.dict = dict;
        this.sessions = sessions;
        this.log = logger;

        this.stats = new GameStats(stats);
        this.protocol = new ServerProtocol(socket);

        play();
    }

    /**
     * Method that checks the current state of the game and the main loop for the logic.
     * @throws IOException when the protocol has encountered an error
     */
    public void play() throws IOException{
        while (state != 0 && !socket.isClosed()) {
            switch(state) {
                case 1: // handle hello
                    handle_hello();
                    break;
                case 2: // handle play
                    handle_play();
                    break;
                case 3: // handle word
                    handle_word();
                    break;
                case 4: // handle error
                    handle_error();
                    break;
                default:
                    state = 0;
                    break;
            }
        }
        socket.close();
    }

    /**
     * Method that handles hello packet. It first checks if the client send the correct message
     * and in case the client sends the correct packet, proceed to generate id (if client wants to
     * start a new session) or load the previous session with its correspondent stat.
     * @throws IOException throws when an IO error occurs
     */
    public void handle_hello() throws IOException {
        String[] msg = protocol.read_message().split("\\s+");
        String name = "";
        int id;

        id = Integer.parseInt(msg[0]);
        for(int i = 1; i < msg.length; i++) {
            if (i < msg.length)
                name += msg[i] + " ";
            else
                name += msg[i];
        }
        log.registerLog(false, 1, id+ " " +name);

        // Generate a new id in case the player wants to start a new session
        if (id == 0) {
            Random r = new Random();
            id = r.nextInt((19999 - 10000) + 1) + 10000; // Generate a new id

            currentId = id;
            sessions.put(id, name); // Store session

            stats.generateJson(id); // Generate a new Json object for this id

            // Tell client that the server is ready and write the logs
            protocol.send_ready(id);
            log.registerLog(true,2, String.valueOf(id));

            // Go to state 2 where the server will be waiting for the client to send PLAY packet
            state = 2;
        }
        // If the introduced name and id already exists, save the id locally and tell the client the server is ready
        else if(sessions.containsKey(id) && sessions.get(id).equals(name)) {
            currentId = id;
            protocol.send_ready(id);
            // Write log and go to state 2 where the server will be waiting for client to send PLAY packet
            log.registerLog(true,2, String.valueOf(id));
            state = 2;
        } else {
            protocol.send_error(4);
            log.registerLog(true, 8, ErrorsEnum.ERRCODE_4.getError());
        }
    }

    /**
     * Method that gets a random word from dictionary which will be used for the game.
     * The word will be unique per session, so the same client won't get repeated word
     * during this session.
     */
    public void handle_play() throws IOException {
        int id = Integer.parseInt(protocol.read_message());
        log.registerLog(false, 3, String.valueOf(id));

        // check if the id from protocol play is the same as the previous one
        if (id == currentId) {
            do {
                // Do not repeat words in the same session
                currentWord = dict.getWord();
            }while(words.contains(currentWord));

            words.add(currentWord); // add to list, so we don't repeat word for this client

            // Used to check the result from the server side
            System.out.println("Result: " + currentWord);

            // send admit and register log
            protocol.send_admit(true);
            log.registerLog(true, 4, "1");
            state = 3;
        } else {
            // If the id is different, do not admit and register log
            protocol.send_admit(false);
            log.registerLog(true, 4, "0");
            state = 1;
        }
    }

    /**
     * Method that handles the word sent by the client.
     * If the word doesn't exist in the dictionary, proceed to send an error instead of increasing the count value (intent)
     * @throws IOException when the protocol reads something wrong in the socket
     */
    private void handle_word() throws IOException{
        String result = "";
        // Convert message to uppercase
        String w = protocol.read_message().toUpperCase();
        log.registerLog(false, 5, w);

        // Check if the message is the answer
        if (!dict.contains(w)) { // If the word is no existent
            // Send error with ERRCODE_5 and write log
            protocol.send_error(5);
            log.registerLog(true, 8, ErrorsEnum.ERRCODE_5.getError());
            return;
        } else {
            currentIntent++;
            result = match(w);
        }

        String json;
        if (result.equals("^^^^^")) {
            json = stats.generateJson(currentId, currentIntent, true);
            protocol.send_result(result);
            protocol.send_stats(json);
            currentIntent = 0;
            // Send log
            log.registerLog(true, 5, result);
            log.registerLog(true, 7, json);

            // Go PLAY state
            state = 2;
        } else if (currentIntent >= 6){
            json = stats.generateJson(currentId, currentIntent, false);
            protocol.send_result(result);
            protocol.send_word(currentWord);
            protocol.send_stats(json);

            currentIntent = 0;
            // Send log
            log.registerLog(true, 5, result);
            log.registerLog(true, 5, currentWord);
            log.registerLog(true, 7, json);
            // Go PLAY state
            state = 2;
        } else {
            protocol.send_result(result);

            log.registerLog(true, 6, result);
        }
    }

    /**
     * Method that check the matches of the word with the answer.
     * If the answer contains the character, it will add "?" if it's in the wrong index.
     * If the answer contains the character and the index matches, it will add "^".
     * If the answer doesn't contain the character, it will add "*".
     * @param s String where we will check if it matches
     * @return result
     */
    private String match(String s) {
        char[] result = new char[5];
        char[] aux = currentWord.toCharArray();
        char c;

        for (int i = 0; i < 5; i++) {
            c = s.charAt(i);
            if (c == aux[i]) {
                result[i] = '^';
                aux[i] = '0';
            }
        }

        int idx;
        for (int i = 0; i < 5; i++) {
            c = s.charAt(i);
            idx = new String(aux).indexOf(c);
            if (idx == -1 && aux[i] != '0') {
                result[i] = '*';
            } else if (aux[i] != '0'){
                result[i] = '?';
                aux[i] = '0';
            }
        }

        return String.valueOf(result);
    }
    private void handle_error() {
    }
}
