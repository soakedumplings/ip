package Command;

import Exceptions.HoneyException;
import Parser.Parser;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

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