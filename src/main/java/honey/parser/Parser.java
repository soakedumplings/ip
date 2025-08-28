package honey.parser;

import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidDateFormatException;
import honey.exceptions.InvalidNumberFormatException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

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
     * @param ui The user interface for output.
     * @param storage The storage for saving changes.
     * @return True if the command is an exit command, false otherwise.
     * @throws HoneyException If the command is invalid or execution fails.
     */
    public static boolean executeCommand(String input, TaskList tasks, Ui ui, Storage storage) throws HoneyException {
        String trimmed = input.trim();
        
        if (trimmed.equals("bye")) {
            return true;
        } else if (trimmed.equals("list")) {
            tasks.listTasks();
        } else if (trimmed.startsWith("mark ")) {
            int taskNumber = parseTaskNumber(trimmed, "mark");
            tasks.markTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
        } else if (trimmed.startsWith("unmark ")) {
            int taskNumber = parseTaskNumber(trimmed, "unmark");
            tasks.unmarkTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
        } else if (trimmed.startsWith("delete ")) {
            int taskNumber = parseTaskNumber(trimmed, "delete");
            tasks.deleteTask(taskNumber);
            storage.saveTasks(tasks.getTasks());
        } else if (trimmed.startsWith("todo ") || trimmed.startsWith("deadline ") || trimmed.startsWith("event ")) {
            tasks.addTask(trimmed);
            storage.saveTasks(tasks.getTasks());
        } else if (trimmed.startsWith("due ")) {
            String dateStr = extractAfterCommand(trimmed, "due");
            if (dateStr.isEmpty()) {
                throw new InvalidDateFormatException("due", "due [date] (e.g., due 2019-12-02)");
            }
            tasks.findTasksDue(dateStr);
        } else if (trimmed.startsWith("find ")) {
            String keyword = extractAfterCommand(trimmed, "find");
            if (keyword.isEmpty()) {
                throw new InvalidCommandException("Please provide a keyword to search for.\nUsage: find [keyword]");
            }
            tasks.findTasks(keyword);
        } else {
            throw new InvalidCommandException(trimmed);
        }
        
        return false;
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