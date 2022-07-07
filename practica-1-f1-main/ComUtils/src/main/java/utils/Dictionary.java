package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Dictionary class which is used by client and server
 */
public class Dictionary {

    private volatile List<String> words = new ArrayList<>();
    BufferedReader br;
    private final Random r = new Random();

    /**
     * default constructor which reads the dictionary.txt we will use for the game WORDLE
     */
    public Dictionary() {
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    this.getClass().getClassLoader().getResourceAsStream("DISC2-LP-WORDLE.txt"))
                    ));

            String line;
            char[] aux = new char[5];
            while ((line = br.readLine()) != null) {
                for(int i = 0; i < 5; i++) {
                    aux[i] = (char) ((line.charAt(i) + 256) % 256);
                }
                words.add(String.valueOf(aux));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that checks if a word exists in the dictionary
     * @param word we want to check
     * @return true in case the words exists, otherwise, return false
     */
    public boolean contains(String word) {
        return words.contains(word);
    }

    /**
     * Method that returns a random word from dictionary
     * @return a word from the dictionary
     */
    public String getWord() {
        String word;
        int idx;

        idx = r.nextInt(words.size());
        word = words.get(idx);

        return word;
    }
}