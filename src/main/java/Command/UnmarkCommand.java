package Command;

import Exceptions.HoneyException;
import Parser.Parser;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public class UnmarkCommand extends Command {
    
    public UnmarkCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        int taskNumber = Parser.parseTaskNumber(commandString, "unmark");
        tasks.unmarkTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}