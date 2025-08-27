package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

/**
 * Represents a command to add a new task.
 * Handles adding todos, deadlines, and events to the task list.
 */
public class AddCommand extends Command {
    
    /**
     * Constructs an add command with the specified command string.
     *
     * @param commandString The full command string for creating a task.
     */
    public AddCommand(String commandString) {
        super(commandString);
    }

    /**
     * Executes the add command by creating and adding a new task.
     *
     * @param tasks Task list to add the new task to.
     * @param ui User interface for output.
     * @param storage Storage for saving the updated task list.
     * @throws HoneyException If task creation fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        tasks.addTask(commandString);
        storage.saveTasks(tasks.getTasks());
    }

    /**
     * Indicates this is not an exit command.
     *
     * @return False as add commands don't exit the application.
     */
    @Override
    public boolean isExit() {
        return false;
    }
}