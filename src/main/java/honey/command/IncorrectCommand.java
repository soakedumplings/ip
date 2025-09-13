package honey.command;

/**
 * Represents an incorrect command that cannot be executed.
 * Used when the user input cannot be parsed into a valid command.
 */
public class IncorrectCommand extends Command {

    private final String errorMessage;

    public IncorrectCommand(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public CommandResult execute() {
        return new CommandResult(errorMessage);
    }
}
