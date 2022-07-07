package exceptions;

/**
 * Exception that shows message when PortParameters are missing.
 */
public class MissingPortParametersException extends Exception{
    public MissingPortParametersException(String message) {
        super(message);
    }
}
