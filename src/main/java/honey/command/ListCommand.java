package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

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