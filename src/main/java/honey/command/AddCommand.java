package honey.command;

import honey.exceptions.HoneyException;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {

    private final String taskDescription;

    public AddCommand(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.addTask(taskDescription);
        storage.saveTasks(tasks.getTasks());
        return new CommandResult(result);
    }
}
