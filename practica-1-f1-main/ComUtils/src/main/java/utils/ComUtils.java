package utils;

import java.io.*;
import java.net.Socket;

/**
 * ComUtils class used to write or read from IOStream
 */
public class ComUtils {
    private final int STRSIZE = 5;

    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }

    /**
     * Constructor which acceps IO Stream
     * @param inputStream inputStream where comUtils will read
     * @param outputStream outputStream where comUtils will write
     * @throws IOException in case something goes wrong with IO
     */
    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    /**
     * Constructor which acceps a socket and sets the IO variables
     * @param socket socket to communicate with server
     * @throws IOException in case something goes wrong with IO
     */
    public ComUtils(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Method that reads an int32 which consists of 4 bytes
     * @return integer of 4 bytes
     * @throws IOException in case something goes wrong with IO
     */
    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes,Endianness.BIG_ENNDIAN);
    }

    /**
     * Method that writes an int of 32 bits
     * @param number int that we wll write to outputStream
     * @throws IOException in case something goes wrong with IO
     */
    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    /**
     * Read a word of 5 bytes
     * @return String containing the word of 5 bytes
     * @throws IOException in case something goes wrong with IO
     */
    public String read_word() throws IOException {
        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) ((bStr[i] + 256) % 256);

        result = String.valueOf(cStr);

        return result.trim();
    }

    /**
     * Method that writes a word of fixed size (5 bytes)
     * @param str string to write into outputStream
     * @throws IOException in case something goes wrong with IO
     */
    public void write_word(String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0,STRSIZE);
    }

    /**
     * Convert int of 32 bits to correspondent Endian
     * @param number int we want to convert
     * @param endianness the endianness we want
     * @return array of bytes containing the 4 bytes
     */
    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /**
     * Method that converts bytes into int of 32 bits
     * @param bytes array of bytes we want to convert
     * @param endianness the correct endianness of the int
     * @return int of 4 bytes
     */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    /**
     * Method that reads numBytes from inputStream
     * @param numBytes number of bytes to read
     * @return array of bytes
     * @throws IOException in case something goes wrong with IO
     */
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /**
     * Method to write a char
     * @param a char we want to write
     * @throws IOException in case something goes wrong with IO
     */
    public void write_char(char a) throws IOException {
        byte bChar = (byte) a;
        dataOutputStream.write(bChar);
    }

    /**
     * Method to read a char
     * @return char we read
     * @throws IOException in case something goes wrong with IO
     */
    public char read_char() throws IOException {
        byte bChar[] = read_bytes(1);
        return (char) bChar[0];
    }

    /**
     * Method that writes a single byte in outputStream
     * @param b byte we want to write
     * @throws IOException in case something goes wrong with IO
     */
    public void write_byte(byte b) throws IOException {
        dataOutputStream.write(b);
    }

    /**
     * Method that reads a single byte from inputStream
     * @return a single byte
     * @throws IOException in case something goes wrong with IO
     */
    public byte read_byte() throws IOException {
        byte b = dataInputStream.readByte();
        if(b == -1) throw new IOException("Broken Pipe");
        return b;
    }

    /**
     * Method that reads a string of variable size and will stop once it fins a byte == 0
     * @return a String
     * @throws IOException in case something goes wrong with IO
     */
    public String read_string_variable() throws IOException {
        ByteArrayOutputStream bArray = new ByteArrayOutputStream();
        byte b = read_byte();
        while (b != (byte) 0){
            bArray.write(b);
            b = read_byte();
        }

        return bArray.toString();
    }

    /**
     * Writes a string of variable size into outputStream
     * @param string string we want to write
     * @throws IOException in case something goes wrong with IO
     */
    public void write_string_variable(String string) throws IOException {
        for(char c : string.toCharArray()){
            write_char(c);
        }
    }
}


