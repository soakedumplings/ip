package honey.command;

import honey.exceptions.HoneyException;
import honey.parser.Parser;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

public class DueCommand extends Command {
    
    public DueCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        String dueDate = Parser.parseDueDate(commandString);
        tasks.findTasksDue(dueDate);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}