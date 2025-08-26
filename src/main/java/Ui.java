package Ui;
import java.util.Scanner;

public class Ui {
    private Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Honey <3");
        System.out.println(" What can I do for you? I will do it SWEETLY ~");
        showLine();
    }

    public void showGoodbye() {
        System.out.println(" Bye! I hope I made your day SWEETER and hope to see you again soon:)");
        showLine();
    }

    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showError(String message) {
        System.out.println(" " + message);
    }

    public void showLoadingError() {
        System.out.println(" Warning: Could not load tasks from file. Starting with empty list.");
    }

    public void close() {
        scanner.close();
    }
}