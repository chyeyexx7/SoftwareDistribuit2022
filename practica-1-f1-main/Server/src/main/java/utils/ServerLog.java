package utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * ServerLog class which is used to write server logs
 */
public class ServerLog {

    Logger log;

    private final Map<Integer, String> command = new HashMap<Integer, String>() {{
        put(1, "HELLO");
        put(2, "READY");
        put(3, "PLAY");
        put(4, "ADMIT");
        put(5, "WORD");
        put(6, "RESULT");
        put(7, "STATS");
        put(8, "ERROR");
    }};

    /**
     * Default constructor for server logger
     * @throws IOException in case something goes wrong with IO
     */
    public ServerLog() throws IOException {
        log = Logger.getLogger("ServerLogger");
        log.addHandler(new FileHandler("Server " + Thread.currentThread().getName() + ".log"));
        log.info("C- [TCP Connect]");
        log.info("S- [TCP Accept]");
    }

    /**
     * Closes the log when the connection is ended.
     */
    public void close() {
        log.info("C- [Connection closed]");
        log.info("S- [Connection closed]");
        log.getHandlers()[0].close();
    }

    /**
     *
     * @param server variable used to know if server send the message
     * @param code opcode of the packet
     * @param message message to send
     */
    public void registerLog(boolean server, int code, String message) {
        if (server) {
            log.info(command.get(code) +
                    "\tC <-------" +
                    code + " " +
                    message + "------- S"
            );
        } else {
            log.info(command.get(code) +
                    "\tC -------" +
                    code + " " +
                    message + " -------> S"
            );
        }
    }
}
