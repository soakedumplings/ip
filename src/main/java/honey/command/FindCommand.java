package honey.command;

import honey.exceptions.HoneyException;

/**
 * Finds tasks that contain the specified keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public CommandResult execute() throws HoneyException {
        String result = tasks.findTasks(keyword);
        return new CommandResult(result);
    }
}
