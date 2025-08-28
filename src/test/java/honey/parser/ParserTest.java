package honey.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;

public class ParserTest {
    private TaskList taskList;
    private Ui ui;
    private Storage storage;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        ui = new Ui();
        storage = new Storage("test_data/parser_test.txt");
        System.setOut(new PrintStream(outContent));
    }
    
    @Test
    public void testExecuteByeCommand() throws HoneyException {
        boolean isExit = Parser.executeCommand("bye", taskList, ui, storage);
        assertTrue(isExit);
    }
    
    @Test
    public void testExecuteListCommand() throws HoneyException {
        boolean isExit = Parser.executeCommand("list", taskList, ui, storage);
        assertFalse(isExit);
        assertTrue(outContent.toString().contains("No tasks in your list!"));
    }
    
    @Test
    public void testExecuteAddTodoCommand() throws HoneyException {
        boolean isExit = Parser.executeCommand("todo read book", taskList, ui, storage);
        assertFalse(isExit);
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testExecuteAddDeadlineCommand() throws HoneyException {
        boolean isExit = Parser.executeCommand("deadline return book /by 2019-10-15", taskList, ui, storage);
        assertFalse(isExit);
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testExecuteAddEventCommand() throws HoneyException {
        boolean isExit = Parser.executeCommand("event meeting /from 2019-10-15 /to 2019-10-16", taskList, ui, storage);
        assertFalse(isExit);
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testExecuteMarkCommand() throws HoneyException {
        taskList.addTask("todo read book");
        outContent.reset();
        
        boolean isExit = Parser.executeCommand("mark 1", taskList, ui, storage);
        assertFalse(isExit);
        assertTrue(outContent.toString().contains("Nice! I've marked this task as done:"));
        assertTrue(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testExecuteUnmarkCommand() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.markTask(1);
        outContent.reset();
        
        boolean isExit = Parser.executeCommand("unmark 1", taskList, ui, storage);
        assertFalse(isExit);
        assertTrue(outContent.toString().contains("OK, I've marked this task as not done yet:"));
        assertFalse(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testExecuteDeleteCommand() throws HoneyException {
        taskList.addTask("todo read book");
        outContent.reset();
        
        boolean isExit = Parser.executeCommand("delete 1", taskList, ui, storage);
        assertFalse(isExit);
        assertEquals(0, taskList.size());
        assertTrue(outContent.toString().contains("Noted. I've removed this task:"));
    }
    
    @Test
    public void testExecuteFindCommand() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.addTask("todo write report");
        outContent.reset();
        
        boolean isExit = Parser.executeCommand("find book", taskList, ui, storage);
        assertFalse(isExit);
        assertTrue(outContent.toString().contains("Here are the matching tasks in your list:"));
        assertTrue(outContent.toString().contains("read book"));
        assertFalse(outContent.toString().contains("write report"));
    }
    
    @Test
    public void testExecuteDueCommand() throws HoneyException {
        taskList.addTask("deadline return book /by 2019-10-15");
        outContent.reset();
        
        boolean isExit = Parser.executeCommand("due 2019-10-15", taskList, ui, storage);
        assertFalse(isExit);
        assertTrue(outContent.toString().contains("Here are the tasks due on Oct 15 2019:") ||
                   outContent.toString().contains("No tasks due on Oct 15 2019!"));
    }
    
    @Test
    public void testExecuteInvalidCommand() {
        assertThrows(HoneyException.class, () -> {
            Parser.executeCommand("invalid command", taskList, ui, storage);
        });
    }
    
    @Test
    public void testExecuteMarkWithInvalidNumber() {
        assertThrows(HoneyException.class, () -> {
            Parser.executeCommand("mark abc", taskList, ui, storage);
        });
    }
    
    @Test
    public void testExecuteMarkWithMissingNumber() {
        assertThrows(HoneyException.class, () -> {
            Parser.executeCommand("mark", taskList, ui, storage);
        });
    }
    
    @Test
    public void testExecuteFindWithEmptyKeyword() {
        assertThrows(HoneyException.class, () -> {
            Parser.executeCommand("find", taskList, ui, storage);
        });
    }
    
    @Test
    public void testExecuteDueWithEmptyDate() {
        assertThrows(HoneyException.class, () -> {
            Parser.executeCommand("due", taskList, ui, storage);
        });
    }
    
    public void tearDown() {
        System.setOut(originalOut);
    }
}