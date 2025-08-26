package Command;

import Exceptions.HoneyException;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public class ListCommand extends Command {
    
    public ListCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        tasks.listTasks();
    }

    @Override
    public boolean isExit() {
        return false;
    }
}