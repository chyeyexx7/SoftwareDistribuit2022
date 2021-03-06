package Game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * GameStats class that stores stats of different threads
 */
public class GameStats {

    private final ConcurrentHashMap<Integer, JsonObject> stats;

    /**
     * default constructor for GameStats
     * @param stats HashMap that contains stats of every session
     */
    public GameStats(ConcurrentHashMap<Integer, JsonObject> stats) {
        this.stats = stats;
    }

    /**
     * Method that generates an JsonObject for a new user
     * @param id unique id
     */
    public void generateJson(int id) {
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
        stats.addProperty("Exits %", 0);
        stats.addProperty("Ratxa Actual", 0);
        stats.addProperty("Ratxa Maxima", 0);
        stats.add("Victories", victories);
        json.add("Stats", stats);

        this.stats.put(id, json);
    }

    /**
     * Method that updates the JsonObject (stats) of an user
     * @param currentId the id of the user we will update
     * @param intent the amount of tries that the player played
     * @param win boolean that checks if the player won
     * @return String which is generated by Gson
     */
    public String generateJson(int currentId, int intent, boolean win) {
        JsonObject json = this.stats.get(currentId);
        JsonObject stats = json.get("Stats").getAsJsonObject();
        JsonObject victories = stats.get("Victories").getAsJsonObject();

        stats.addProperty("Jugades", stats.get("Jugades").getAsInt()+1);
        String temp = String.valueOf(intent);
        System.out.println("current intent: " + temp);
        if (win) {
            int highest, totalWins = 0;
            int percent;
            // Update victories
            System.out.println("intent " + temp);
            victories.addProperty(temp, victories.get(temp).getAsInt()+1);

            for(int i=1; i<7; i++){
                totalWins += victories.get(String.valueOf(i)).getAsInt();
            }

            // Update stats
            percent = (int) (((float)totalWins/stats.get("Jugades").getAsInt()) * 100);
            stats.addProperty("Exits %", percent);
            stats.addProperty("Ratxa Actual", stats.get("Ratxa Actual").getAsInt()+1);
            highest = stats.get("Ratxa Maxima").getAsInt();
            if (stats.get("Ratxa Actual").getAsInt() > highest)
                stats.addProperty("Ratxa Maxima", stats.get("Ratxa Actual").getAsInt());
        } else {
            int totalWins = 0;
            int percent;
            // Update victories
            for(int i=1; i<7; i++){
                totalWins += victories.get(String.valueOf(i)).getAsInt();
            }
            // Update stats
            percent = (int) (((float)totalWins/stats.get("Jugades").getAsInt()) * 100);
            stats.addProperty("Exits %", percent);
            stats.addProperty("Ratxa Actual", 0);
        }
        stats.add("Victories", victories);
        json.add("Stats", stats);

        this.stats.put(currentId, json);

        // Builder to convert json to more readable format
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }
}
