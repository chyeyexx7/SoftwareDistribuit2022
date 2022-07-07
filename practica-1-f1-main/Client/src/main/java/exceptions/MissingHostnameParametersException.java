package exceptions;

/**
 * Exception that shows message when HostnameParameters are missing.
 */
public class MissingHostnameParametersException extends Exception{
    public MissingHostnameParametersException(String message) {
        super(message);
    }
}
