package honey.command;

import honey.exceptions.HoneyException;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {

    private final int taskNumber;

    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.unmarkTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return new CommandResult(result);
    }
}
