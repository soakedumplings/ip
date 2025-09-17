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
 * Comprehensive tests for MarkCommand functionality.
 * Tests marking tasks as done and all exception scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class MarkCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tasks = new TaskList();
        File testFile = tempDir.resolve("mark_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
        
        // Set up sample tasks for testing
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
    }

    // ====================== Basic Mark Tests ======================
    
    @Test
    public void execute_validTaskNumber_marksTaskAsDone() throws Exception {
        MarkCommand command = new MarkCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        // Check response message
        assertTrue(result.getFeedbackToUser().contains("Wonderful work, my sweet Bee!"));
        assertTrue(result.getFeedbackToUser().contains("I'm so proud of us!"));
        assertFalse(result.isExit());
        
        // Check task is marked as done
        assertTrue(tasks.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void execute_lastTask_marksTaskAsDone() throws Exception {
        MarkCommand command = new MarkCommand(3);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Wonderful work, my sweet Bee!"));
        assertTrue(tasks.getTasks().get(2).getIsDone());
        
        // Check other tasks remain unchanged
        assertFalse(tasks.getTasks().get(0).getIsDone());
        assertFalse(tasks.getTasks().get(1).getIsDone());
    }
    
    @Test
    public void execute_alreadyDoneTask_stillMarksAsDone() throws Exception {
        // First mark the task
        tasks.markTask(1);
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Mark it again
        MarkCommand command = new MarkCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Wonderful work, my sweet Bee!"));
        assertTrue(tasks.getTasks().get(0).getIsDone());
    }

    // ====================== Exception Tests ======================
    
    @Test
    public void execute_invalidTaskNumber_throwsException() {
        MarkCommand command = new MarkCommand(0);
        command.setData(tasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("Oh sweetie!"));
        assertTrue(exception.getMessage().contains("mark"));
    }
    
    @Test
    public void execute_taskNumberTooHigh_throwsException() {
        MarkCommand command = new MarkCommand(10);
        command.setData(tasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("between 1 and 3"));
    }
    
    @Test
    public void execute_negativeTaskNumber_throwsException() {
        MarkCommand command = new MarkCommand(-1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, command::execute);
    }
    
    @Test
    public void execute_emptyTaskList_throwsException() {
        TaskList emptyTasks = new TaskList();
        MarkCommand command = new MarkCommand(1);
        command.setData(emptyTasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("between 1 and 0"));
    }

    // ====================== Integration Tests ======================
    
    @Test
    public void execute_afterAddingNewTask_correctTaskNumbering() throws Exception {
        // Mark first task
        MarkCommand command = new MarkCommand(1);
        command.setData(tasks, storage);
        command.execute();
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Add new task
        tasks.addTask("todo new task");
        
        // Mark the new task (should be task number 4)
        MarkCommand newCommand = new MarkCommand(4);
        newCommand.setData(tasks, storage);
        newCommand.execute();
        assertTrue(tasks.getTasks().get(3).getIsDone());
        
        // Check other tasks remain unchanged
        assertTrue(tasks.getTasks().get(0).getIsDone());
        assertFalse(tasks.getTasks().get(1).getIsDone());
        assertFalse(tasks.getTasks().get(2).getIsDone());
    }
    
    @Test
    public void execute_boundaryValues_handledCorrectly() throws Exception {
        // Test minimum valid value
        MarkCommand minCommand = new MarkCommand(1);
        minCommand.setData(tasks, storage);
        minCommand.execute();
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Test maximum valid value
        MarkCommand maxCommand = new MarkCommand(3);
        maxCommand.setData(tasks, storage);
        maxCommand.execute();
        assertTrue(tasks.getTasks().get(2).getIsDone());
    }
    
    @Test
    public void execute_differentTaskTypes_worksForAllTypes() throws Exception {
        // Test todo task
        MarkCommand markTodo = new MarkCommand(1);
        markTodo.setData(tasks, storage);
        markTodo.execute();
        assertTrue(tasks.getTasks().get(0).getIsDone());
        
        // Test deadline task
        MarkCommand markDeadline = new MarkCommand(2);
        markDeadline.setData(tasks, storage);
        markDeadline.execute();
        assertTrue(tasks.getTasks().get(1).getIsDone());
        
        // Test event task
        MarkCommand markEvent = new MarkCommand(3);
        markEvent.setData(tasks, storage);
        markEvent.execute();
        assertTrue(tasks.getTasks().get(2).getIsDone());
    }
}