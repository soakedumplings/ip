package honey.command;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
// Solution below inspired by https://github.com/se-edu/addressbook-level2/blob/master/test/java/seedu/addressbook/storage/StorageFileTest.java

/**
 * Represents an abstract command that can be executed.
 * All concrete commands should extend this class and implement the execute method.
 */
public abstract class Command {
    protected TaskList tasks;
    protected Storage storage;

    /**
     * Sets the data context for this command.
     *
     * @param tasks The task list to operate on
     * @param storage The storage for persisting changes
     */
    public void setData(TaskList tasks, Storage storage) {
        assert tasks != null : "TaskList cannot be null";
        assert storage != null : "Storage cannot be null";
        this.tasks = tasks;
        this.storage = storage;
    }

    /**
     * Executes this command.
     *
     * @return The result of executing this command
     * @throws HoneyException If there is an error during execution
     */
    public abstract CommandResult execute() throws HoneyException;

}
