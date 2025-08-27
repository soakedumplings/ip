package honey.command;

import honey.exceptions.HoneyException;
import honey.parser.Parser;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

/**
 * Represents a command to mark a task as done.
 * Marks the specified task as completed in the task list.
 */
public class MarkCommand extends Command {
    
    /**
     * Constructs a mark command with the specified command string.
     *
     * @param commandString The command string containing task number to mark.
     */
    public MarkCommand(String commandString) {
        super(commandString);
    }

    /**
     * Executes the mark command by marking the specified task as done.
     *
     * @param tasks Task list containing the task to mark.
     * @param ui User interface for output.
     * @param storage Storage for saving the updated task list.
     * @throws HoneyException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        int taskNumber = Parser.parseTaskNumber(commandString, "mark");
        tasks.markTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}