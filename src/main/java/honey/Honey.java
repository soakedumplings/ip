package honey;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.parser.Parser;
import honey.tasklist.TaskList;
import honey.ui.Ui;

/**
 * Represents the main Honey task management application.
 * Honey is a personal task manager that helps users track todos, deadlines, and events.
 */
public class Honey {
    /** Storage component for saving and loading tasks */
    private Storage storage;
    /** Task list containing all user tasks */
    private TaskList tasks;
    /** User interface component for input/output operations */
    private Ui ui;

    /**
     * Constructs a new Honey application with the specified storage file path.
     * Initializes the UI, storage, and attempts to load existing tasks.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath The file path where tasks will be stored.
     */
    public Honey(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (HoneyException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main application loop.
     * Displays welcome message and continuously processes user commands
     * until the user chooses to exit.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                isExit = Parser.executeCommand(fullCommand, tasks, ui, storage);
            } catch (HoneyException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        
        ui.close();
    }

    /**
     * Starts the Honey application.
     * Creates a new Honey instance with default storage location and runs it.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Honey("Data/honey.txt").run();
    }
}