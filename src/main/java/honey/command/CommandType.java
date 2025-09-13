package honey.command;

import java.util.Arrays;

/**
 * Represents the different types of commands available in the Honey application.
 * Provides type safety and better structure for command categorization.
 */
public enum CommandType {
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    FIND("find"),
    DUE("due"),
    SORT("sort"),
    BYE("bye");

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }


    /**
     * Returns the CommandType corresponding to the given command word.
     *
     * @param commandWord The command word to look up
     * @return The corresponding CommandType, or null if no match is found
     */
    public static CommandType fromCommandWord(String commandWord) {
        return Arrays.stream(values())
                .filter(type -> type.commandWord.equals(commandWord))
                .findFirst()
                .orElse(null);
    }

}
