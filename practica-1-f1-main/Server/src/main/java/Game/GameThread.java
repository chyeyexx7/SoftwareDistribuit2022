package Game;

import com.google.gson.JsonObject;
import utils.Dictionary;
import utils.ServerLog;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GameThread class which extends from Thread and starts a new thread with a new GameServer
 */
public class GameThread extends Thread {

    private GameServer game;
    private final Socket socket;
    ConcurrentHashMap<Integer, String> sessions;
    ConcurrentHashMap<Integer, JsonObject> stats;
    Dictionary dict;

    /**
     * Default constructor
     * @param socket socket used to communicate between client and server
     * @param dict dictionary that stores the words used for WORDLE
     * @param sessions HashMap that stores the sessions of different clients
     * @param stats HashMap that stores the stats of different clients
     */
    public GameThread(Socket socket, Dictionary dict, ConcurrentHashMap<Integer, String> sessions, ConcurrentHashMap<Integer, JsonObject> stats){
        this.socket = socket;
        this.dict = dict;
        this.sessions = sessions;
        this.stats = stats;
    }

    /**
     * Overrides the run() method from Thread and starts a new game
     */
    @Override
    public void run() {
        ServerLog log = null;
        try {
            log = new ServerLog();
            game = new GameServer(socket, dict, sessions, stats, log);
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            if (log != null) log.close();
        }
    }
}
