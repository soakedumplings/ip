package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

public abstract class Command {
    protected String commandString;

    public Command(String commandString) {
        this.commandString = commandString;
    }

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException;

    public abstract boolean isExit();
}