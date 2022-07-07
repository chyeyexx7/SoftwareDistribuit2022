package packets;

import utils.ComUtils;
import utils.ErrorsEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class protocol for server
 */
public class ServerProtocol {

    private final ComUtils comUtils;

    /**
     * Constructor with socket as parameter
     * @param socket
     * @throws IOException
     */
    public ServerProtocol(Socket socket) throws IOException {
        this.comUtils = new ComUtils(socket.getInputStream(), socket.getOutputStream());
    }

    /**
     * Constructor which accepts IO stream
     * @param input
     * @param output
     * @throws IOException
     */
    public ServerProtocol(InputStream input, OutputStream output) throws IOException{
        this.comUtils = new ComUtils(input, output);
    }

    /**
     * send packet ready through socket
     * @param sessionId int of 32 bits
     * @throws IOException
     */
    public void send_ready(int sessionId) throws IOException {
        this.comUtils.write_byte((byte) 2);
        this.comUtils.write_int32(sessionId);
    }

    /**
     * send packet admit through socket
     * @param b boolean which will be true or false
     * @throws IOException
     */
    public void send_admit(boolean b) throws IOException {
        this.comUtils.write_byte((byte) 4);
        this.comUtils.write_byte((byte) (b ? 1 : 0));
    }

    /**
     * send packet word through socket
     * @param word word to be sent
     * @throws IOException
     */
    public void send_word(String word) throws IOException{
        this.comUtils.write_byte((byte) 5);
        this.comUtils.write_word(word);
    }

    /**
     * send packet result through socket
     * @param result string to be sent
     * @throws IOException
     */
    public void send_result(String result) throws IOException {
        this.comUtils.write_byte((byte) 6);
        this.comUtils.write_string_variable(result);
    }

    /**
     * send packet stats through socket
     * @param stat String in JSON format
     * @throws IOException
     */
    public void send_stats(String stat) throws IOException {
        this.comUtils.write_byte((byte) 7);
        this.comUtils.write_string_variable(stat);
        this.comUtils.write_byte((byte) 0);
    }

    /**
     * send packet error through socket
     * @param code ERRCODE of the error
     * @throws IOException
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


    /**
     * read the message from the socket
     * @return the string which contains the command
     * @throws IOException
     */
    public String read_message() throws IOException{
        String message;
        int code = (int) comUtils.read_byte();

        switch (code){
            case 1: //hello
                message = this.read_hello();
                break;
            case 3: //play
                message = this.read_play();
                break;
            case 5: //word
                message = this.read_word();
                break;
            case 8:
                message = this.read_error();
                break;
            default:
                throw new IOException("INVALID OPCODE");
        }

        return message;
    }

    /**
     * read the hello packet from the socket
     * @return a string which contains the sessionId and name
     * @throws IOException
     */
    private String read_hello() throws IOException{
        String message = "";
        message += String.valueOf(this.comUtils.read_int32());
        message += " ";
        message += this.comUtils.read_string_variable();

        return message;
    }

    /**
     * read the play packet from the socket
     * @return sessionId
     * @throws IOException
     */
    private String read_play() throws IOException{
        return String.valueOf(this.comUtils.read_int32());
    }

    /**
     * read the word from the socket
     * @return string of 5 bytes
     * @throws IOException
     */
    private String read_word() throws IOException{
        return this.comUtils.read_word();
    }

    /**
     * read the error from the socket
     * @return the error message
     * @throws IOException
     */
    private String read_error() throws IOException {
        String message = "";
        message += String.valueOf(comUtils.read_byte());
        message += " ";
        message += comUtils.read_string_variable();

        return message;
    }

}
