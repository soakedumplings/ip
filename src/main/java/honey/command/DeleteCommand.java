package honey.command;

import honey.exceptions.HoneyException;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {

    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.deleteTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return new CommandResult(result);
    }
}
