package honey;

import honey.command.Command;
import honey.command.CommandResult;
import honey.exceptions.HoneyException;
import honey.parser.Parser;
import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Represents the main Honey task management application.
 * Honey is a personal task manager that helps users track todos, deadlines, and events.
 */
public class Honey {
    public static final String WELCOME_MESSAGE = "Hello Bee Bee! I'm Honey <3\n"
            + "What can I do for you? I will do it SWEETLY ~";
    public static final String GOODBYE_MESSAGE = "Bye Bee Bee! Hope to see you again soon:)";

    /**
     * Storage component for saving and loading tasks
     */
    private final Storage storage;
    /**
     * Task list containing all user tasks
     */
    private TaskList tasks;
    private final Parser parser;

    /**
     * Constructs a new Honey application with the specified storage file path.
     * Initializes the storage, and attempts to load existing tasks.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath The file path where tasks will be stored.
     */
    public Honey(String filePath) {
        storage = new Storage(filePath);
        parser = new Parser();
        try {
            tasks = new TaskList(storage.load());
        } catch (HoneyException e) {
            tasks = new TaskList();
        }
    }

    public String getResponse(String input) {
        try {
            Command command = parser.parseCommand(input);
            command.setData(tasks, storage);
            CommandResult result = command.execute();
            return result.getFeedbackToUser();
        } catch (HoneyException e) {
            return e.getMessage();
        }
    }
}
