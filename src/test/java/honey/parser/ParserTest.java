package honey.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import honey.command.AddCommand;
import honey.command.Command;
import honey.command.DeleteCommand;
import honey.command.ExitCommand;
import honey.command.FindCommand;
import honey.command.IncorrectCommand;
import honey.command.ListCommand;
import honey.command.MarkCommand;

/**
 * Simple, focused test for Parser.
 * Tests only parsing logic - does input create correct Command objects?
 */
public class ParserTest {
    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void parseCommand_listCommand_returnsListCommand() {
        Command result = parser.parseCommand("list");
        assertTrue(result instanceof ListCommand);
    }

    @Test
    public void parseCommand_byeCommand_returnsExitCommand() {
        Command result = parser.parseCommand("bye");
        assertTrue(result instanceof ExitCommand);
    }

    @Test
    public void parseCommand_todoCommand_returnsAddCommand() {
        Command result = parser.parseCommand("todo read book");
        assertTrue(result instanceof AddCommand);
    }

    @Test
    public void parseCommand_markCommand_returnsMarkCommand() {
        Command result = parser.parseCommand("mark 1");
        assertTrue(result instanceof MarkCommand);
    }

    @Test
    public void parseCommand_deleteCommand_returnsDeleteCommand() {
        Command result = parser.parseCommand("delete 1");
        assertTrue(result instanceof DeleteCommand);
    }

    @Test
    public void parseCommand_findCommand_returnsFindCommand() {
        Command result = parser.parseCommand("find book");
        assertTrue(result instanceof FindCommand);
    }

    @Test
    public void parseCommand_invalidCommand_returnsIncorrectCommand() {
        Command result = parser.parseCommand("invalid");
        assertTrue(result instanceof IncorrectCommand);
    }

    @Test
    public void parseCommand_emptyInput_returnsIncorrectCommand() {
        Command result = parser.parseCommand("");
        assertTrue(result instanceof IncorrectCommand);
    }

    @Test
    public void parseCommand_markWithoutNumber_returnsIncorrectCommand() {
        Command result = parser.parseCommand("mark");
        assertTrue(result instanceof IncorrectCommand);
    }
}
