package Command;

import Exceptions.HoneyException;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public abstract class Command {
    protected String commandString;

    public Command(String commandString) {
        this.commandString = commandString;
    }

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException;

    public abstract boolean isExit();
}