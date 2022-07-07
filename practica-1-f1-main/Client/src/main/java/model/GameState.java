package model;

/**
 * Enum to be able to keep moving through the possible game states.
 */
public enum GameState {
    SENDING_HELLO, READING_HELLO,
    READING_READY, SENDING_READY,
    READING_PLAY, SENDING_PLAY,
    READING_ADMIT, SENDING_ADMIT,
    READING_WORD, SENDING_WORD,
    READING_RESULT, SENDING_RESULT,
    READING_STATS, SENDING_STATS,
    READING_ERROR, SENDING_ERROR,
    ENDING
}
