package model;

/**
 * Game class will provide and establish the necessary game information
 */
public class Game {
    private GameState gameState;

    /**
     * Default constructor of the class game, just initializing in HELLO command.
     */
    public Game() {
        gameState = GameState.SENDING_HELLO;
    }

    /**
     * GameState setter
     * @param state game state
     */
    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * GameState getter
     * @return game state
     */
    public GameState getGameState() {
        return gameState;
    }
}
