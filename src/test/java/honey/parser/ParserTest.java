package honey.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidNumberFormatException;
import honey.exceptions.InvalidDateFormatException;
import honey.command.Command;
import honey.command.ExitCommand;
import honey.command.ListCommand;
import honey.command.AddCommand;
import honey.command.MarkCommand;
import honey.command.UnmarkCommand;
import honey.command.DeleteCommand;
import honey.command.DueCommand;

public class ParserTest {
    
    @Test
    public void testGetCommandTypeExact() {
        assertEquals(CommandType.EXIT, Parser.getCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(CommandType.EXIT, Parser.getCommandType("BYE"));
        assertEquals(CommandType.LIST, Parser.getCommandType("LIST"));
    }
    
    @Test
    public void testGetCommandTypePrefix() {
        assertEquals(CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.getCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.getCommandType("delete 1"));
        assertEquals(CommandType.TODO, Parser.getCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.getCommandType("deadline return book /by 2019-10-15"));
        assertEquals(CommandType.EVENT, Parser.getCommandType("event meeting /from 2019-10-15 /to 2019-10-16"));
        assertEquals(CommandType.DUE, Parser.getCommandType("due 2019-10-15"));
    }
    
    @Test
    public void testGetCommandTypeUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("unknown"));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType(""));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("mark"));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("todo"));
    }
    
    @Test
    public void testParseExitCommand() throws HoneyException {
        Command command = Parser.parse("bye");
        assertTrue(command instanceof ExitCommand);
    }
    
    @Test
    public void testParseListCommand() throws HoneyException {
        Command command = Parser.parse("list");
        assertTrue(command instanceof ListCommand);
    }
    
    @Test
    public void testParseAddCommands() throws HoneyException {
        Command todoCommand = Parser.parse("todo read book");
        assertTrue(todoCommand instanceof AddCommand);
        
        Command deadlineCommand = Parser.parse("deadline return book /by 2019-10-15");
        assertTrue(deadlineCommand instanceof AddCommand);
        
        Command eventCommand = Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16");
        assertTrue(eventCommand instanceof AddCommand);
    }
    
    @Test
    public void testParseMarkCommand() throws HoneyException {
        Command command = Parser.parse("mark 1");
        assertTrue(command instanceof MarkCommand);
    }
    
    @Test
    public void testParseUnmarkCommand() throws HoneyException {
        Command command = Parser.parse("unmark 1");
        assertTrue(command instanceof UnmarkCommand);
    }
    
    @Test
    public void testParseDeleteCommand() throws HoneyException {
        Command command = Parser.parse("delete 1");
        assertTrue(command instanceof DeleteCommand);
    }
    
    @Test
    public void testParseDueCommand() throws HoneyException {
        Command command = Parser.parse("due 2019-10-15");
        assertTrue(command instanceof DueCommand);
    }
    
    @Test
    public void testParseInvalidCommand() {
        assertThrows(InvalidCommandException.class, () -> {
            Parser.parse("invalid command");
        });
        
        assertThrows(InvalidCommandException.class, () -> {
            Parser.parse("");
        });
    }
    
    @Test
    public void testParseTaskNumber() throws HoneyException {
        assertEquals(1, Parser.parseTaskNumber("mark 1", "mark"));
        assertEquals(5, Parser.parseTaskNumber("delete 5", "delete"));
        assertEquals(10, Parser.parseTaskNumber("unmark 10", "unmark"));
    }
    
    @Test
    public void testParseTaskNumberInvalid() {
        assertThrows(InvalidNumberFormatException.class, () -> {
            Parser.parseTaskNumber("mark abc", "mark");
        });
        
        assertThrows(InvalidNumberFormatException.class, () -> {
            Parser.parseTaskNumber("mark", "mark");
        });
        
        assertThrows(InvalidNumberFormatException.class, () -> {
            Parser.parseTaskNumber("mark ", "mark");
        });
    }
    
    @Test
    public void testParseDueDate() throws HoneyException {
        assertEquals("2019-10-15", Parser.parseDueDate("due 2019-10-15"));
        assertEquals("2020-12-25", Parser.parseDueDate("due 2020-12-25"));
    }
    
    @Test
    public void testParseDueDateInvalid() {
        assertThrows(InvalidDateFormatException.class, () -> {
            Parser.parseDueDate("due");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            Parser.parseDueDate("due ");
        });
    }
    
    @Test
    public void testParseWhitespaceHandling() throws HoneyException {
        assertEquals(CommandType.LIST, Parser.getCommandType("  list  "));
        assertEquals(CommandType.MARK, Parser.getCommandType("  mark 1  "));
        
        Command command = Parser.parse("  list  ");
        assertTrue(command instanceof ListCommand);
        
        // Test with properly trimmed input
        assertEquals(1, Parser.parseTaskNumber("mark 1", "mark"));
    }
}