package honey.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import honey.exceptions.InvalidTaskNumberException;
import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for UnmarkCommand functionality.
 * Tests unmarking tasks and all exception scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class UnmarkCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tasks = new TaskList();
        File testFile = tempDir.resolve("unmark_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
        
        // Set up sample tasks for testing
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
    }

    // ====================== Basic Unmark Tests ======================
    
    @Test
    public void execute_markedTask_unmarksTask() throws Exception {
        // First mark a task
        tasks.markTask(1);
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Then unmark it
        UnmarkCommand command = new UnmarkCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        // Check response message
        assertTrue(result.getFeedbackToUser().contains("No worries, darling!"));
        assertTrue(result.getFeedbackToUser().contains("We'll tackle it together when you're ready!"));
        assertFalse(result.isExit());
        
        // Check task is unmarked
        assertFalse(tasks.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void execute_alreadyUnmarkedTask_stillUnmarksTask() throws Exception {
        // Task is already unmarked by default
        assertFalse(tasks.getTasks().get(1).getIsDone());
        
        UnmarkCommand command = new UnmarkCommand(2);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("No worries, darling!"));
        assertFalse(tasks.getTasks().get(1).getIsDone());
    }
    
    @Test
    public void execute_lastTask_unmarksCorrectly() throws Exception {
        // Mark and then unmark the last task
        tasks.markTask(3);
        assertTrue(tasks.getTasks().get(2).getIsDone());
        
        UnmarkCommand command = new UnmarkCommand(3);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("No worries, darling!"));
        assertFalse(tasks.getTasks().get(2).getIsDone());
        
        // Check other tasks remain unchanged
        assertFalse(tasks.getTasks().get(0).getIsDone());
        assertFalse(tasks.getTasks().get(1).getIsDone());
    }

    // ====================== Exception Tests ======================
    
    @Test
    public void execute_invalidTaskNumber_throwsException() {
        UnmarkCommand command = new UnmarkCommand(0);
        command.setData(tasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("Oh sweetie!"));
        assertTrue(exception.getMessage().contains("unmark"));
    }
    
    @Test
    public void execute_taskNumberTooHigh_throwsException() {
        UnmarkCommand command = new UnmarkCommand(5);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, command::execute);
    }
    
    @Test
    public void execute_negativeTaskNumber_throwsException() {
        UnmarkCommand command = new UnmarkCommand(-1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, command::execute);
    }
    
    @Test
    public void execute_emptyTaskList_throwsException() {
        TaskList emptyTasks = new TaskList();
        UnmarkCommand command = new UnmarkCommand(1);
        command.setData(emptyTasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, command::execute);
    }

    // ====================== Integration Tests ======================
    
    @Test
    public void execute_markUnmarkSequence_correctlyTogglesTaskStatus() throws Exception {
        // Initially unmarked
        assertFalse(tasks.getTasks().get(0).getIsDone());
        
        // Mark the task
        tasks.markTask(1);
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Unmark the task
        UnmarkCommand unmarkCommand = new UnmarkCommand(1);
        unmarkCommand.setData(tasks, storage);
        unmarkCommand.execute();
        assertFalse(tasks.getTasks().get(0).getIsDone());
        
        // Mark again
        tasks.markTask(1);
        assertTrue(tasks.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void execute_unmarksOnlySpecifiedTask() throws Exception {
        // Mark all tasks
        tasks.markTask(1);
        tasks.markTask(2);
        tasks.markTask(3);
        
        // Verify all are marked
        assertTrue(tasks.getTasks().get(0).getIsDone());
        assertTrue(tasks.getTasks().get(1).getIsDone());
        assertTrue(tasks.getTasks().get(2).getIsDone());
        
        // Unmark middle task
        UnmarkCommand command = new UnmarkCommand(2);
        command.setData(tasks, storage);
        command.execute();
        
        // Verify only middle task is unmarked
        assertTrue(tasks.getTasks().get(0).getIsDone());
        assertFalse(tasks.getTasks().get(1).getIsDone());
        assertTrue(tasks.getTasks().get(2).getIsDone());
    }
    
    @Test
    public void execute_differentTaskTypes_worksForAllTypes() throws Exception {
        // Mark all tasks first
        tasks.markTask(1);
        tasks.markTask(2);
        tasks.markTask(3);
        
        // Unmark todo task
        UnmarkCommand unmarkTodo = new UnmarkCommand(1);
        unmarkTodo.setData(tasks, storage);
        unmarkTodo.execute();
        assertFalse(tasks.getTasks().get(0).getIsDone());
        
        // Unmark deadline task
        UnmarkCommand unmarkDeadline = new UnmarkCommand(2);
        unmarkDeadline.setData(tasks, storage);
        unmarkDeadline.execute();
        assertFalse(tasks.getTasks().get(1).getIsDone());
        
        // Unmark event task
        UnmarkCommand unmarkEvent = new UnmarkCommand(3);
        unmarkEvent.setData(tasks, storage);
        unmarkEvent.execute();
        assertFalse(tasks.getTasks().get(2).getIsDone());
    }
    
    @Test
    public void execute_afterAddingNewTask_correctTaskNumbering() throws Exception {
        // Mark and unmark first task
        tasks.markTask(1);
        UnmarkCommand command = new UnmarkCommand(1);
        command.setData(tasks, storage);
        command.execute();
        assertFalse(tasks.getTasks().get(0).getIsDone());
        
        // Add new task and mark it
        tasks.addTask("todo new task");
        tasks.markTask(4);
        
        // Unmark the new task (should be task number 4)
        UnmarkCommand newCommand = new UnmarkCommand(4);
        newCommand.setData(tasks, storage);
        newCommand.execute();
        assertFalse(tasks.getTasks().get(3).getIsDone());
    }
    
    @Test
    public void execute_boundaryValues_handledCorrectly() throws Exception {
        // Mark all tasks first
        tasks.markTask(1);
        tasks.markTask(3);
        
        // Test minimum valid value
        UnmarkCommand minCommand = new UnmarkCommand(1);
        minCommand.setData(tasks, storage);
        minCommand.execute();
        assertFalse(tasks.getTasks().get(0).getIsDone());
        
        // Test maximum valid value
        UnmarkCommand maxCommand = new UnmarkCommand(3);
        maxCommand.setData(tasks, storage);
        maxCommand.execute();
        assertFalse(tasks.getTasks().get(2).getIsDone());
    }
}