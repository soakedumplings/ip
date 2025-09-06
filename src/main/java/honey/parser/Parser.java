package honey.parser;

import honey.Honey;
import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidDateFormatException;
import honey.exceptions.InvalidNumberFormatException;
import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Handles parsing and executing user commands directly.
 * Simplified implementation without command pattern abstraction.
 */
public class Parser {
    /**
     * Parses and executes a user command directly.
     *
     * @param input The user input string.
     * @param tasks The task list to operate on.
     * @param storage The storage for saving changes.
     * @return "true" if the command is an exit command, or the String response otherwise
     * @throws HoneyException If the command is invalid or execution fails.
     */
    public String executeCommand(String input, TaskList tasks, Storage storage) throws HoneyException {
        String trimmed = input.trim();

        if (trimmed.equals("bye")) {
           return "true";
        } else if (trimmed.equals("list")) {
            return tasks.listTasks();
        } else if (trimmed.startsWith("mark ")) {
            int taskNumber = parseTaskNumber(trimmed, "mark");
            String result = tasks.markTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return result;
        } else if (trimmed.startsWith("unmark ")) {
            int taskNumber = parseTaskNumber(trimmed, "unmark");
            String result = tasks.unmarkTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return result;
        } else if (trimmed.startsWith("delete ")) {
            int taskNumber = parseTaskNumber(trimmed, "delete");
            String result = tasks.deleteTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
            return result;
        } else if (trimmed.startsWith("todo ") || trimmed.startsWith("deadline ") || trimmed.startsWith("event ")) {
            String result = tasks.addTask(trimmed);
            storage.saveTasks(tasks.getTasks());
            return result;
        } else if (trimmed.startsWith("due ")) {
            String dateStr = extractAfterCommand(trimmed, "due");
            if (dateStr.isEmpty()) {
                throw new InvalidDateFormatException("due", "due [date] (e.g., due 2019-12-02)");
            }
            tasks.findTasksDue(dateStr);
            return "Tasks due on " + dateStr + " displayed";
        } else if (trimmed.startsWith("find ")) {
            String keyword = extractAfterCommand(trimmed, "find");
            if (keyword.isEmpty()) {
                throw new InvalidCommandException("Please provide a keyword to search for.\nUsage: find [keyword]");
            }
            return tasks.findTasks(keyword);
        } else {
            throw new InvalidCommandException(trimmed);
        }
    }

    /**
     * Parses a task number from a command string.
     *
     * @param input The full command string.
     * @param commandWord The command word (e.g., "mark", "delete").
     * @return The parsed task number.
     * @throws HoneyException If the number is invalid or missing.
     */
    private static int parseTaskNumber(String input, String commandWord) throws HoneyException {
        String numberPart = extractAfterCommand(input, commandWord);

        if (numberPart.isEmpty()) {
            throw new InvalidNumberFormatException(commandWord, "no number provided");
        }

        try {
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException(commandWord, numberPart);
        }
    }

    /**
     * Extracts the text after a command word.
     *
     * @param input The full input string.
     * @param command The command word to remove.
     * @return The text after the command word, trimmed.
     */
    private static String extractAfterCommand(String input, String command) {
        return input.substring(command.length()).trim();
    }
}
