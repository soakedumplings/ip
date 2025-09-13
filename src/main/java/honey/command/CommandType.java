package honey.command;

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
        for (CommandType type : values()) {
            if (type.commandWord.equals(commandWord)) {
                return type;
            }
        }
        return null;
    }

}
