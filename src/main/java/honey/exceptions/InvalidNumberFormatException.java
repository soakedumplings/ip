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
        super("Oh honey! 🔢 I need a little number to " + operation + " a task for us!\n"
              + "You gave me: '" + providedInput + "' but I was expecting a sweet number! 💕\n"
              + "Could you try: " + operation + " [number]\n"
              + "Like this: " + operation + " 1 ✨");
    }
}
