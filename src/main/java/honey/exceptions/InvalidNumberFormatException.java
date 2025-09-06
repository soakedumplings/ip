package honey.exceptions;

/**
 * Exception thrown when an invalid number format is provided for task operations.
 */
public class InvalidNumberFormatException extends HoneyException {
    /**
     * Constructs an InvalidNumberFormatException with the specified operation and provided input.
     *
     * @param operation The operation that failed due to invalid number format.
     * @param providedInput The input that was provided.
     */
    public InvalidNumberFormatException(String operation, String providedInput) {
        super("OOPS!!! I need a valid number to " + operation + " a task!\n"
              + "You provided: '" + providedInput + "'\n"
              + "Please use: " + operation + " [number]\n"
              + "Example: " + operation + " 1");
    }
}
