package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

/**
 * Represents a command in the Honey application.
 * This is an abstract base class for all command types.
 */
public abstract class Command {
    /** The original command string from user input */
    protected String commandString;

    /**
     * Constructs a command with the specified command string.
     *
     * @param commandString The original command string from user input.
     */
    public Command(String commandString) {
        this.commandString = commandString;
    }

    /**
     * Executes this command.
     *
     * @param tasks Task list to operate on.
     * @param ui User interface for input/output.
     * @param storage Storage for saving changes.
     * @throws HoneyException If command execution fails.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException;

    /**
     * Checks if this command should exit the application.
     *
     * @return True if this is an exit command, false otherwise.
     */
    public abstract boolean isExit();
}