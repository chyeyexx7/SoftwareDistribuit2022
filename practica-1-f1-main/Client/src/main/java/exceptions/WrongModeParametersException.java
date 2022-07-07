package exceptions;

/**
 * Exception that shows message when Mode parameters are wrong introduced.
 */
public class WrongModeParametersException extends Exception{
    public WrongModeParametersException(String message) {
        super(message);
    }
}
