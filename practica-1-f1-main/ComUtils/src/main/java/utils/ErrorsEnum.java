package utils;

/**
 * Enum which contains the possible errors
 */
public enum ErrorsEnum {
    ERRCODE_1("CARÀCTER NO RECONEGUT"),
    ERRCODE_2("MISSATGE DESCONEGUT"),
    ERRCODE_3("MISSATGE FORA DE PROTOCOL"),
    ERRCODE_4("INICI DE SESSIÓ INCORRECTE"),
    ERRCODE_5("PARAULA DESCONEGUDA"),
    ERRCODE_6("MISSATGE MAL FORMAT"),
    ERRCODE_99("ERROR DESCONEGUT");

    private final String error;

    /**
     * Default constructor for enum
     * @param error setter
     */
    ErrorsEnum(String error){
        this.error = error;
    }

    /**
     * Getter of error
     * @return error missatge
     */
    public String getError(){
        return error;
    }
}
