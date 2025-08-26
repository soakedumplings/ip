package Command;

import Exceptions.HoneyException;
import Parser.Parser;
import Storage.Storage;
import TaskList.TaskList;
import Ui.Ui;

public class MarkCommand extends Command {
    
    public MarkCommand(String commandString) {
        super(commandString);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        int taskNumber = Parser.parseTaskNumber(commandString, "mark");
        tasks.markTask(taskNumber);
        storage.saveTasks(tasks.getTasks());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}