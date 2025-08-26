package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

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