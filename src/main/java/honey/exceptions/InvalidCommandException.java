package honey.exceptions;

/**
 * Exception thrown when user enters an invalid or unknown command.
 * Provides a helpful list of valid commands to the user.
 */
public class InvalidCommandException extends HoneyException {
    /**
     * Constructs an InvalidCommandException for the specified invalid command.
     *
     * @param command The invalid command that was entered.
     */
    public InvalidCommandException(String command) {
        super(command);
    }
}
