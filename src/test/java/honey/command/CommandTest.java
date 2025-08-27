package honey.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import honey.exceptions.HoneyException;
import honey.storage.Storage;
import honey.tasklist.TaskList;
import honey.ui.Ui;
import task.Task;
import task.Todo;

public class CommandTest {
    private TaskList taskList;
    private Ui ui;
    private Storage storage;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        ui = new Ui();
        storage = new Storage("test_data/command_test.txt");
        System.setOut(new PrintStream(outContent));
    }
    
    @Test
    public void testExitCommand() throws HoneyException {
        ExitCommand exitCommand = new ExitCommand("bye");
        assertTrue(exitCommand.isExit());
        
        exitCommand.execute(taskList, ui, storage);
        // Should not throw any exception
    }
    
    @Test
    public void testAddCommand() throws HoneyException {
        AddCommand addCommand = new AddCommand("todo read book");
        assertFalse(addCommand.isExit());
        
        addCommand.execute(taskList, ui, storage);
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testListCommand() throws HoneyException {
        // Add a task first
        taskList.addTask("todo read book");
        outContent.reset();
        
        ListCommand listCommand = new ListCommand("list");
        assertFalse(listCommand.isExit());
        
        listCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("Here are the tasks in your list:"));
    }
    
    @Test
    public void testListCommandEmpty() throws HoneyException {
        ListCommand listCommand = new ListCommand("list");
        
        listCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("No tasks in your list!"));
    }
    
    @Test
    public void testMarkCommand() throws HoneyException {
        // Add a task first
        taskList.addTask("todo read book");
        outContent.reset();
        
        MarkCommand markCommand = new MarkCommand("mark 1");
        assertFalse(markCommand.isExit());
        
        markCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("Nice! I've marked this task as done:"));
        assertTrue(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testMarkCommandInvalidNumber() {
        MarkCommand markCommand = new MarkCommand("mark 1");
        
        assertThrows(HoneyException.class, () -> {
            markCommand.execute(taskList, ui, storage);
        });
    }
    
    @Test
    public void testUnmarkCommand() throws HoneyException {
        // Add and mark a task first
        taskList.addTask("todo read book");
        taskList.markTask(1);
        outContent.reset();
        
        UnmarkCommand unmarkCommand = new UnmarkCommand("unmark 1");
        assertFalse(unmarkCommand.isExit());
        
        unmarkCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("OK, I've marked this task as not done yet:"));
        assertFalse(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testDeleteCommand() throws HoneyException {
        // Add a task first
        taskList.addTask("todo read book");
        outContent.reset();
        
        DeleteCommand deleteCommand = new DeleteCommand("delete 1");
        assertFalse(deleteCommand.isExit());
        
        deleteCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("Noted. I've removed this task:"));
        assertEquals(0, taskList.size());
    }
    
    @Test
    public void testDueCommand() throws HoneyException {
        // Add some tasks first
        taskList.addTask("deadline return book /by 2019-10-15");
        taskList.addTask("todo read book");
        outContent.reset();
        
        DueCommand dueCommand = new DueCommand("due 2019-10-15");
        assertFalse(dueCommand.isExit());
        
        dueCommand.execute(taskList, ui, storage);
        assertTrue(outContent.toString().contains("Here are the tasks due on Oct 15 2019:") || 
                   outContent.toString().contains("No tasks due on Oct 15 2019!"));
    }
    
    @Test
    public void testDueCommandInvalidDate() {
        DueCommand dueCommand = new DueCommand("due invalid-date");
        
        assertThrows(HoneyException.class, () -> {
            dueCommand.execute(taskList, ui, storage);
        });
    }
    
    public void tearDown() {
        System.setOut(originalOut);
    }
}