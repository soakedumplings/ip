package honey.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import honey.command.AddCommand;
import honey.command.Command;
import honey.command.CommandType;
import honey.command.DeleteCommand;
import honey.command.DueCommand;
import honey.command.ExitCommand;
import honey.command.FindCommand;
import honey.command.IncorrectCommand;
import honey.command.ListCommand;
import honey.command.MarkCommand;
import honey.command.UnmarkCommand;
import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidDateFormatException;
import honey.exceptions.InvalidNumberFormatException;

/**
 * Handles parsing user input and creating appropriate Command objects.
 * This parser follows the Command pattern where parsing creates command objects
 * that can be executed later, rather than directly executing the commands.
 */
public class Parser {

    private static final Pattern COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input and returns the corresponding Command object.
     *
     * @param input The user input string
     * @return Command object representing the user's intent
     */
    public Command parseCommand(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new IncorrectCommand("Please enter a command.");
        }

        final Matcher matcher = COMMAND_FORMAT.matcher(trimmed);
        if (!matcher.matches()) {
            return new IncorrectCommand("Invalid command format.");
        }

        final String commandWord = matcher.group("commandWord").toLowerCase();
        final String arguments = matcher.group("arguments").trim();

        CommandType commandType = CommandType.fromCommandWord(commandWord);

        if (commandType == null) {
            return new IncorrectCommand("Unknown command: " + commandWord);
        }

        try {
            switch (commandType) {
            case BYE:
                return new ExitCommand();

            case LIST:
                return new ListCommand();

            case MARK:
                return prepareMarkCommand(arguments);

            case UNMARK:
                return prepareUnmarkCommand(arguments);

            case DELETE:
                return prepareDeleteCommand(arguments);

            case TODO:
            case DEADLINE:
            case EVENT:
                return prepareAddCommand(trimmed);

            case FIND:
                return prepareFindCommand(arguments);

            case DUE:
                return prepareDueCommand(arguments);

            default:
                return new IncorrectCommand("Unknown command: " + commandWord);
            }
        } catch (HoneyException e) {
            return new IncorrectCommand(e.getMessage());
        }
    }

    /**
     * Prepares a MarkCommand with the specified task number.
     */
    private Command prepareMarkCommand(String arguments) throws HoneyException {
        int taskNumber = parseTaskNumber(arguments, "mark");
        return new MarkCommand(taskNumber);
    }

    /**
     * Prepares an UnmarkCommand with the specified task number.
     */
    private Command prepareUnmarkCommand(String arguments) throws HoneyException {
        int taskNumber = parseTaskNumber(arguments, "unmark");
        return new UnmarkCommand(taskNumber);
    }

    /**
     * Prepares a DeleteCommand with the specified task number.
     */
    private Command prepareDeleteCommand(String arguments) throws HoneyException {
        int taskNumber = parseTaskNumber(arguments, "delete");
        return new DeleteCommand(taskNumber);
    }

    /**
     * Prepares an AddCommand with the task description.
     */
    private Command prepareAddCommand(String taskDescription) {
        return new AddCommand(taskDescription);
    }

    /**
     * Prepares a FindCommand with the search keyword.
     */
    private Command prepareFindCommand(String arguments) throws HoneyException {
        if (arguments.isEmpty()) {
            throw new InvalidCommandException("Please provide a keyword to search for.\nUsage: find [keyword]");
        }
        return new FindCommand(arguments);
    }

    /**
     * Prepares a DueCommand with the date string.
     */
    private Command prepareDueCommand(String arguments) throws HoneyException {
        if (arguments.isEmpty()) {
            throw new InvalidDateFormatException("due", "due [date] (e.g., due 2019-12-02)");
        }
        return new DueCommand(arguments);
    }

    /**
     * Parses a task number from command arguments.
     *
     * @param arguments The command arguments
     * @param commandWord The command word for error messages
     * @return The parsed task number
     * @throws HoneyException If the number is invalid or missing
     */
    private int parseTaskNumber(String arguments, String commandWord) throws HoneyException {
        if (arguments.isEmpty()) {
            throw new InvalidNumberFormatException(commandWord, "no number provided");
        }

        try {
            return Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException(commandWord, arguments);
        }
    }
}
