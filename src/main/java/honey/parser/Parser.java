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

    public static CommandType getCommandType(String input) {
        String command = input.trim().toLowerCase();

        return Optional.ofNullable(EXACT_COMMANDS.get(command))
                .or(() -> findPrefixCommand(command))
                .orElse(CommandType.UNKNOWN);
    }

    private static Optional<CommandType> findPrefixCommand(String command) {
        return PREFIX_COMMANDS.entrySet().stream()
                .filter(entry -> command.startsWith(entry.getKey() + " "))
                .map(Map.Entry::getValue)
                .findFirst();
    }

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

    private static boolean isAddCommand(CommandType type) {
        return type == CommandType.TODO ||
                type == CommandType.DEADLINE ||
                type == CommandType.EVENT;
    }

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

    public static String parseDueDate(String input) throws HoneyException {
        String datePart = extractAfterCommand(input, "due");

        if (datePart.isEmpty()) {
            throw new InvalidDateFormatException("due", "due [date] (e.g., due 2019-12-02)");
        }

        return datePart;
    }

    private static String extractAfterCommand(String input, String command) {
        return input.substring(command.length()).trim();
    }

    @FunctionalInterface
    private interface CommandFactory {
        Command create(String input) throws HoneyException;
    }
}