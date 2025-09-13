package honey.command;

import honey.exceptions.HoneyException;

/**
 * Lists all tasks in the task list.
 */
public class ListCommand extends Command {

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.listTasks();
        return new CommandResult(result);
    }
}
