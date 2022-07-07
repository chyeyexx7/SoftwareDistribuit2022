import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Test;

import packets.ClientProtocol;
import packets.ServerProtocol;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuxiliarTest {
    ClientProtocol client;
    ServerProtocol server;

    public AuxiliarTest() {
        File file = new File("test99");
        try {
            file.createNewFile();
            client = new ClientProtocol(new FileInputStream(file), new FileOutputStream(file));
            server = new ServerProtocol(new FileInputStream(file), new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void JsonTest() {
        JsonObject json = new JsonObject();
        JsonObject stats = new JsonObject();
        JsonObject victories = new JsonObject();

        victories.addProperty("1", 0);
        victories.addProperty("2", 0);
        victories.addProperty("3", 0);
        victories.addProperty("4", 0);
        victories.addProperty("5", 0);
        victories.addProperty("6", 0);

        stats.addProperty("Jugades", 0);
        stats.addProperty("Exits", 0);
        stats.addProperty("Ratxa Actual", 0);
        stats.addProperty("Ratxa Maxima", 0);
        stats.add("Victories", victories);
        json.add("Stats", stats);

        JsonObject j = stats.get("Victories").getAsJsonObject();
        System.out.println(j);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(json));
    }
}
