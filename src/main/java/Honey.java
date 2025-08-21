import java.util.Scanner;

public class Honey {
    private static String[] tasks = new String[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

            if (input.equals("bye")) {
                System.out.println(" Bye! I hope I made your day SWEETER and hope to see you again soon:)");
                printBreak();
                break;
            } else if (input.equals("list")) {
                listTasks();
            } else {
                addTask(input);
            }
            printBreak();
        }
    }

    public static void listTasks() {
        if (taskCount == 0) {
            System.out.println(" No tasks in your list!");
        } else {
            for (int i = 0; i < taskCount; i++) {
                System.out.println(" " + (i + 1) + ". " + tasks[i]);
            }
        }
    }

    public static void addTask(String task) {
        tasks[taskCount] = task;
        taskCount++;
        System.out.println(" added: " + task);
    }

    public static void printBreak() {
        System.out.println("____________________________________________________________");
    }
}