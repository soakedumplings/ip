package Command;

import Exceptions.HoneyException;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public class AddCommand extends Command {
    
    public AddCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        tasks.addTask(commandString);
        storage.saveTasks(tasks.getTasks());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}