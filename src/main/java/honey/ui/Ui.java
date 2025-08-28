package honey.ui;

import java.util.Scanner;

/**
 * Handles user interface operations for the Honey application.
 * Manages input reading and output display to the user.
 */
public class Ui {
    /** Scanner for reading user input */
    private Scanner scanner;

    /**
     * Constructs a new UI handler.
     * Initializes the scanner for reading user input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message to the user.
     * Shows the application greeting and instructions.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Honey <3");
        System.out.println(" What can I do for you? I will do it SWEETLY ~");
        showLine();
    }

    /**
     * Displays the goodbye message to the user.
     * Shows farewell message when the application exits.
     */
    public void showGoodbye() {
        System.out.println(" Bye! I hope I made your day SWEETER and hope to see you again soon:)");
        showLine();
    }

    /**
     * Displays a separator line.
     * Used to visually separate different sections of output.
     */
    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Reads a command from user input.
     *
     * @return User input as a string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Displays a loading error message.
     * Used when the application fails to load tasks from storage.
     */
    public void showLoadingError() {
        System.out.println(" Warning: Could not load tasks from file. Starting with empty list.");
    }

    /**
     * Closes the scanner and releases resources.
     * Should be called when the application exits.
     */
    public void close() {
        scanner.close();
    }
}