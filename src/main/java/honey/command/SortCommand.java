package honey.command;

import honey.exceptions.HoneyException;

/**
 * Sorts deadline tasks by their deadline dates in ascending order.
 * Shows overdue tasks with an indication.
 */
public class SortCommand extends Command {

    private final String sortType;

    public SortCommand(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        assert sortType != null : "Sort type cannot be null";

        if ("deadline".equals(sortType)) {
            String result = tasks.sortDeadlines();
            return new CommandResult(result);
        } else {
            return new CommandResult("Sorry, I can only sort 'deadline' tasks for now.\nUsage: sort deadline");
        }
    }
}
