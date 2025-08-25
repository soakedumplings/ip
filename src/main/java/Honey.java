import java.util.Scanner;

public class Honey {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Task.loadTasks();
        greeting();
        runChatLoop(scanner);
        scanner.close();
    }

    public static void greeting() {
        printBreak();
        System.out.println(" Hello! I'm Honey <3");
        System.out.println(" What can I do for you? I will do it SWEETLY ~");
        printBreak();
    }

    public static void runChatLoop(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            printBreak();

            try {
                if (input.equals("bye")) {
                    System.out.println(" Bye! I hope I made your day SWEETER and hope to see you again soon:)");
                    printBreak();
                    break;
                } else if (input.equals("list")) {
                    Task.listTasks();
                } else if (input.startsWith("mark")) {
                    Task.markTask(input);
                } else if (input.startsWith("unmark")) {
                    Task.unmarkTask(input);
                } else if (input.startsWith("todo") || input.startsWith("deadline") || input.startsWith("event")) {
                    Task.addTask(input);
                } else if (input.startsWith("delete")) {
                    Task.deleteTask(input);
                } else {
                    throw new InvalidCommandException(input);
                }
            } catch (HoneyException e) {
                System.out.println(" " + e.getMessage());
            }
            printBreak();
        }
    }
    public static void printBreak() {
        System.out.println("____________________________________________________________");
    }
}