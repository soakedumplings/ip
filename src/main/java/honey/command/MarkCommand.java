package honey.command;

import honey.exceptions.HoneyException;
import honey.parser.Parser;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

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