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
        super("OOPS!!! I'm sorry, but I don't know what '" + command + "' means :(\n" +
              "Here are the commands I understand:\n" +
              "  • todo [description] - Add a todo task\n" +
              "  • deadline [description] /by [date] - Add a deadline task\n" +
              "  • event [description] /from [start] /to [end] - Add an event task\n" +
              "  • list - Show all tasks\n" +
              "  • mark [number] - Mark task as done\n" +
              "  • unmark [number] - Mark task as not done\n" +
              "  • delete [number] - Delete a task\n" +
              "  • bye - Exit the program");
    }
}