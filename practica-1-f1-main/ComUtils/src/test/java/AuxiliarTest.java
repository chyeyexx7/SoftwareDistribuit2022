import org.junit.Test;
import static org.junit.Assert.*;

import packets.ClientProtocol;
import packets.ServerProtocol;

import java.io.*;

public class AuxiliarTest {
    ClientProtocol client;
    ServerProtocol server;

    public AuxiliarTest() {
        File file = new File("test");
        try {
            file.createNewFile();
            client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void resultTest() {
        try {
            String msg = "^^^^^";
            server.send_result(msg);
            assertEquals(msg, client.read_message());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
