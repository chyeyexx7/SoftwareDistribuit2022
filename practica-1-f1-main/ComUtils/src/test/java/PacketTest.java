/**
 * Chang Ye
 * 07/03/2022
 */
import org.junit.Test;
import static org.junit.Assert.*;

import packets.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PacketTest {

    @Test
    public void hello_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));
            client.send_hello(0, "chang");
            String message = server.read_message();

            assertEquals("0 chang", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ready_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));

            server.send_ready(0);
            String message = client.read_message();

            assertEquals("0", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void play_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));

            client.send_play(0);
            String message = server.read_message();

            assertEquals("0", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void admit_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));

            server.send_admit(true);
            String message = client.read_message();

            assertEquals("1", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void word_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));

            client.send_word("TEXTO");
            String message = server.read_message();

            assertEquals("TEXTO", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void result_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ClientProtocol client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            ServerProtocol server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));

            server.send_result("^^^^^");
            String message = client.read_message();

            assertEquals("^^^^^", message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
