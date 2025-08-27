package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

/**
 * Represents a command to exit the application.
 * This command terminates the application loop.
 */
public class ExitCommand extends Command {
    
    /**
     * Constructs an exit command.
     *
     * @param commandString The command string (typically "bye").
     */
    public ExitCommand(String commandString) {
        super(commandString);
    }

    /**
     * Executes the exit command.
     * No action is needed as the exit is handled by the isExit() method.
     *
     * @param tasks Task list (unused).
     * @param ui User interface (unused).
     * @param storage Storage (unused).
     * @throws HoneyException Never thrown for exit command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        // No execution needed for exit
    }

    /**
     * Indicates this is an exit command.
     *
     * @return True to signal the application should exit.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}