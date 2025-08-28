package honey;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.parser.Parser;
import honey.tasklist.TaskList;
import honey.ui.Ui;

public class Honey {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

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

    public static void main(String[] args) {
        new Honey("Data/honey.txt").run();
    }
}