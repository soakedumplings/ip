package honey.command;

import honey.exceptions.HoneyException;

/**
 * Finds tasks that are due on a specific date.
 */
public class DueCommand extends Command {

    private final String dateStr;

    public DueCommand(String dateStr) {
        this.dateStr = dateStr;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String tasksDue = tasks.findTasksDue(dateStr);
        return new CommandResult(tasksDue);
    }
}
