package honey.command;

import honey.exceptions.HoneyException;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {

    private final int taskNumber;

    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.markTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
        return new CommandResult(result);
    }
}
