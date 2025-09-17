package honey.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for DueCommand functionality.
 * Tests finding tasks due on specific dates.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class DueCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tasks = new TaskList();
        File testFile = tempDir.resolve("due_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
        
        // Set up sample tasks with various dates
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("deadline buy gift /by 2023-12-15");
        tasks.addTask("event meeting /from 2023-12-15 /to 2023-12-16");
        tasks.addTask("deadline different date /by 2023-12-20");
    }

    // ====================== Basic Due Tests ======================
    
    @Test
    public void execute_noTasksDue_returnsNoTasksMessage() throws Exception {
        DueCommand command = new DueCommand("2023-12-25");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How lovely! No urgent tasks"));
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_hasTasksDue_returnsTasksDue() throws Exception {
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here are our important tasks"));
        assertTrue(output.contains("assignment"));
        assertTrue(output.contains("gift"));
        assertTrue(output.contains("meeting"));
        
        // Should not contain tasks with different dates
        assertFalse(output.contains("different date"));
    }
    
    @Test
    public void execute_deadlineTasksDue_includesDeadlines() throws Exception {
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("buy gift"));
    }
    
    @Test
    public void execute_eventTasksDue_includesEvents() throws Exception {
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("meeting"));
    }

    // ====================== Date Format Tests ======================
    
    @Test
    public void execute_validDateFormat_works() throws Exception {
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertFalse(result.getFeedbackToUser().contains("Invalid date format"));
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void execute_emptyTaskList_returnsNoTasks() throws Exception {
        TaskList emptyTasks = new TaskList();
        
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(emptyTasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How lovely! No urgent tasks"));
    }
    
    @Test
    public void execute_onlyTodoTasks_returnsNoTasks() throws Exception {
        TaskList todoOnlyTasks = new TaskList();
        todoOnlyTasks.addTask("todo task 1");
        todoOnlyTasks.addTask("todo task 2");
        
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(todoOnlyTasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How lovely! No urgent tasks"));
    }
}