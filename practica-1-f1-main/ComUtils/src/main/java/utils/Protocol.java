package utils;

/**
 * Enum used to store COMMANDS and its OPCODE
 */
public enum Protocol {
    HELLO((byte) 1, "HELLO"), READY((byte) 2, "READY"),
    PLAY((byte) 3, "PLAY"), ADMIT((byte) 4, "ADMIT"),
    WORD((byte) 5, "WORD"), RESULT((byte) 6, "RESULT"),
    STATS((byte) 7, "STATS"), ERROR((byte) 8, "ERROR");

    private final byte code;
    private final String name;

    /**
     * Default constructor of Protocol
     * @param code opcode of the command
     * @param name name of the command
     */
    Protocol(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * getter of opcode
     * @return opcode
     */
    public byte getCode() {
        return code;
    }

    /**
     * getter of name
     * @return command name
     */
    public String getName() {
        return name;
    }

    /**
     * getter of command given opcode
     * @param b OPCODE
     * @return name of the command
     */
    public static Protocol getComByCode(byte b) {
        switch (b) {
            case 1: return HELLO;
            case 2: return READY;
            case 3: return PLAY;
            case 4: return ADMIT;
            case 5: return WORD;
            case 6: return RESULT;
            case 7: return STATS;
            case 8: return ERROR;
        }
        return null;
    }


}
