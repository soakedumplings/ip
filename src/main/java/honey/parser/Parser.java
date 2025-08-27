package honey.parser;

import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidDateFormatException;
import honey.exceptions.InvalidNumberFormatException;
import honey.command.Command;
import honey.command.ExitCommand;
import honey.command.ListCommand;
import honey.command.AddCommand;
import honey.command.MarkCommand;
import honey.command.UnmarkCommand;
import honey.command.DeleteCommand;
import honey.command.DueCommand;

import java.util.Map;
import java.util.Optional;

/**
 * Parses user input and creates appropriate Command objects.
 * Handles command recognition and parameter extraction for the Honey application.
 */
public class Parser {
    private static final Map<String, CommandType> EXACT_COMMANDS = Map.of(
            "bye", CommandType.EXIT,
            "list", CommandType.LIST
    );

    private static final Map<String, CommandType> PREFIX_COMMANDS = Map.of(
            "mark", CommandType.MARK,
            "unmark", CommandType.UNMARK,
            "delete", CommandType.DELETE,
            "todo", CommandType.TODO,
            "deadline", CommandType.DEADLINE,
            "event", CommandType.EVENT,
            "due", CommandType.DUE
    );

    private static final Map<CommandType, CommandFactory> COMMAND_FACTORIES = Map.of(
            CommandType.EXIT, ExitCommand::new,
            CommandType.LIST, ListCommand::new,
            CommandType.MARK, MarkCommand::new,
            CommandType.UNMARK, UnmarkCommand::new,
            CommandType.DELETE, DeleteCommand::new,
            CommandType.DUE, DueCommand::new
    );

    /**
     * Determines the command type from user input.
     * Recognizes both exact commands and prefix commands.
     *
     * @param input User input string.
     * @return CommandType representing the parsed command.
     */
    public static CommandType getCommandType(String input) {
        String command = input.trim().toLowerCase();

        return Optional.ofNullable(EXACT_COMMANDS.get(command))
                .or(() -> findPrefixCommand(command))
                .orElse(CommandType.UNKNOWN);
    }

    /**
     * Finds commands that start with a specific prefix.
     * Used for commands that take parameters like "mark 1" or "todo read book".
     *
     * @param command Command string to search.
     * @return Optional CommandType if a prefix match is found.
     */
    private static Optional<CommandType> findPrefixCommand(String command) {
        return PREFIX_COMMANDS.entrySet().stream()
                .filter(entry -> command.startsWith(entry.getKey() + " "))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    /**
     * Parses user input and creates the appropriate Command object.
     *
     * @param input User input string.
     * @return Command object representing the user's intent.
     * @throws HoneyException If the command is invalid or cannot be parsed.
     */
    public static Command parse(String input) throws HoneyException {
        CommandType commandType = getCommandType(input);

        if (commandType == CommandType.UNKNOWN) {
            throw new InvalidCommandException(input);
        }

        if (isAddCommand(commandType)) {
            return new AddCommand(input);
        }

        return COMMAND_FACTORIES.get(commandType).create(input);
    }

    /**
     * Checks if the command type represents an add command.
     * Add commands include TODO, DEADLINE, and EVENT.
     *
     * @param type CommandType to check.
     * @return True if it's an add command, false otherwise.
     */
    private static boolean isAddCommand(CommandType type) {
        return type == CommandType.TODO ||
                type == CommandType.DEADLINE ||
                type == CommandType.EVENT;
    }

    /**
     * Parses the task number from a command string.
     * Used for commands like "mark 1", "delete 2", etc.
     *
     * @param input Full command string.
     * @param commandWord Command word to extract number after.
     * @return Task number as integer.
     * @throws HoneyException If the number format is invalid.
     */
    public static int parseTaskNumber(String input, String commandWord) throws HoneyException {
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
     * Parses the date from a "due" command.
     *
     * @param input Full command string starting with "due".
     * @return Date string extracted from the command.
     * @throws HoneyException If the date format is invalid.
     */
    public static String parseDueDate(String input) throws HoneyException {
        String datePart = extractAfterCommand(input, "due");

        if (datePart.isEmpty()) {
            throw new InvalidDateFormatException("due", "due [date] (e.g., due 2019-12-02)");
        }

        return datePart;
    }

    /**
     * Extracts the substring after a command word.
     * Helper method for parsing command parameters.
     *
     * @param input Full input string.
     * @param command Command word to extract after.
     * @return Trimmed substring after the command word.
     */
    private static String extractAfterCommand(String input, String command) {
        return input.substring(command.length()).trim();
    }

    @FunctionalInterface
    private interface CommandFactory {
        Command create(String input) throws HoneyException;
    }
}