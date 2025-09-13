package honey.command;

import honey.Honey;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    @Override
    public CommandResult execute() {
        return new CommandResult(Honey.GOODBYE_MESSAGE, true);
    }
}
