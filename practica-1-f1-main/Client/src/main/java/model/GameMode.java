package model;

public enum GameMode {
    MANUAL, AUTOMATIC;

    public static GameMode getMode(int mode) {
        switch (mode) {
            default:
                // TODO : Create exception for different code from de 0 o 1.
            case 0:
                return GameMode.MANUAL;
            case 1:
                return GameMode.AUTOMATIC;
        }
    }
}