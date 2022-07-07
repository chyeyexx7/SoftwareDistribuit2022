package packets;

import utils.ComUtils;
import utils.ErrorsEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Jorge Vinagre, Chang Ye
 * ClientProtocol class contains all the necessary methods that write and read using the pipe with the server
 * and handle the correct format and outputs during the command send.
 */
public class ClientProtocol {

    private final ComUtils comUtils;

    /**
     * Constructor that initializes a ClientProtocol with a socket.
     * @param socket to establish connection with server.
     * @throws IOException exception.
     */
    public ClientProtocol(Socket socket) throws IOException {
        this.comUtils = new ComUtils(socket.getInputStream(), socket.getOutputStream());
    }

    /**
     * @param input inputStream to read
     * @param output outputStream to write
     * @throws IOException exception.
     */
    public ClientProtocol(InputStream input, OutputStream output) throws IOException{
        this.comUtils = new ComUtils(input, output);
    }

    // SEND METHODS
    /**
     * Method to send hello command to the server.
     * @param sessionId ID of the session
     * @param name name of the player
     * @throws IOException exception.
     */
    public void send_hello(int sessionId, String name) throws IOException {
        System.out.println("HELLO  C -------1 " + sessionId + " " + name + " 0 --------> S");
        this.comUtils.write_byte((byte) 1);
        this.comUtils.write_int32(sessionId);
        this.comUtils.write_string_variable(name);
        this.comUtils.write_byte((byte) 0);
    }

    /**
     * Method to send play command to the server.
     * @param sessionId ID of the session/player
     * @throws IOException exception
     */
    public void send_play(int sessionId) throws IOException {
        System.out.println("PLAY  C -------3 " + sessionId + " --------> S");
        this.comUtils.write_byte((byte) 3);
        this.comUtils.write_int32(sessionId);
    }


    /**
     * Method to send a word to the server
     * @param word word to send
     * @throws IOException exception
     */
    public void send_word(String word) throws IOException {
        System.out.println("WORD   C -------5 " + word.toUpperCase() + " -----------> S");
        this.comUtils.write_byte((byte) 5);
        this.comUtils.write_word(word);
    }

    /**
     * Method to send error to the server
     * @param code error code
     * @throws IOException exception
     */
    public void send_error(int code) throws  IOException{
        this.comUtils.write_byte((byte) 8);
        this.comUtils.write_byte((byte) code);
        StringBuilder err = new StringBuilder();
        switch(code) {
            case 1:
                err.append(ErrorsEnum.ERRCODE_1.getError());
                break;
            case 2:
                err.append(ErrorsEnum.ERRCODE_2.getError());
                break;
            case 3:
                err.append(ErrorsEnum.ERRCODE_3.getError());
                break;
            case 4:
                err.append(ErrorsEnum.ERRCODE_4.getError());
                break;
            case 5:
                err.append(ErrorsEnum.ERRCODE_5.getError());
                break;
            case 6:
                err.append(ErrorsEnum.ERRCODE_6.getError());
                break;
            default:
                err.append(ErrorsEnum.ERRCODE_99.getError());
                break;
        }
        this.comUtils.write_string_variable(err.toString());
        this.comUtils.write_byte((byte) 0);
    }

    // READ METHODS
    /**
     * @return the command read from server according to the code sent by the server.
     * @throws IOException exception.
     */
    public String read_message() throws IOException{
        String message;
        int code = (int) comUtils.read_byte();

        switch (code){
            case 2: //ready
                message = this.read_ready();
                break;
            case 4: //admit
                message = this.read_admit();
                break;
            case 5: // word
                message = this.read_word();
                break;
            case 6: //result
                message = this.read_result();
                break;
            case 7: //stat
                message = this.read_string();
                break;
            case 8: //error
                message = this.read_error();
            default:
                throw  new IOException("Invalid opcode");
        }

        return message;
    }

    /**
     * Method to read the int sent by the ready command server.
     * @return the read int from the server
     * @throws IOException exception
     */
    private String read_ready() throws IOException {
        int read_answer = this.comUtils.read_int32();
        System.out.println("READY  C <------2 " +  read_answer + " --------- S");
        return String.valueOf(read_answer);
    }

    /**
     * Method to read the byte sent by the admit server command.
     * @return the read byte from server.
     * @throws IOException exception
     */
    public String read_admit() throws IOException {
        byte answer = this.comUtils.read_byte();
        System.out.println("ADMIT  C <------4 " + answer + " ---------------- S");
        return String.valueOf((int) answer);
    }

    /**
     * Method to read the result word from server.
     * @return the result word
     * @throws IOException exception
     */
    private String read_result() throws IOException{
        String word = this.comUtils.read_word();
        System.out.println("RESULT C <------6 " + word + " ------------ S");
        return word;
    }

    /**
     * Method to read the string sent by the server
     * @return the string read from server
     * @throws IOException exception
     */
    private String read_string() throws IOException {
        return this.comUtils.read_string_variable();
    }

    /**
     * Method to read the word sent by server
     * @return the read word
     * @throws IOException exception
     */
    public String read_word() throws IOException {
        String word = this.comUtils.read_word();
        System.out.println("WORD C <------5 " + word + " ------------ S");
        return word;
    }

    /**
     * Method to read the error from server
     * @return error read
     * @throws IOException exception
     */
    private String read_error() throws IOException {
        String message = "";
        message += String.valueOf(comUtils.read_byte());
        message += " ";
        message += comUtils.read_string_variable();

        return message;
    }

}
