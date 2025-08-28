package honey.exceptions;

/**
 * Base exception class for the Honey application.
 * All custom exceptions in the Honey application extend this class.
 */
public class HoneyException extends Exception {
    /**
     * Constructs a HoneyException with the specified error message.
     *
     * @param message Error message describing the exception.
     */
    public HoneyException(String message) {
        super(message);
    }
}