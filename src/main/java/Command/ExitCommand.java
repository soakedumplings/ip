package Command;

import Exceptions.HoneyException;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public class ExitCommand extends Command {
    
    public ExitCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        // No execution needed for exit
    }

    @Override
    public boolean isExit() {
        return true;
    }
}