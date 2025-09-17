package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import honey.task.Deadline;
import honey.task.Event;
import honey.task.Todo;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for DeleteCommand functionality.
 * Tests task deletion and all exception scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class DeleteCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tasks = new TaskList();
        File testFile = tempDir.resolve("delete_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
        
        // Set up sample tasks for testing
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        tasks.addTask("todo write code");
    }

    // ====================== Basic Delete Tests ======================
    
    @Test
    public void deleteCommand_validTaskNumber_deletesTask() throws Exception {
        assertEquals(4, tasks.size());
        
        DeleteCommand command = new DeleteCommand(2);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        // Check response message
        assertTrue(result.getFeedbackToUser().contains("All done, sweetheart!"));
        assertTrue(result.getFeedbackToUser().contains("gently removed this from our hive"));
        assertTrue(result.getFeedbackToUser().contains("Now we're focusing on 3 lovely tasks"));
        assertFalse(result.isExit());
        
        // Check task was deleted
        assertEquals(3, tasks.size());
        
        // Check that the correct task was deleted (deadline should be gone)
        assertTrue(tasks.getTasks().get(0) instanceof Todo);
        assertTrue(tasks.getTasks().get(1) instanceof Event);
        assertTrue(tasks.getTasks().get(2) instanceof Todo);
        
        // Check that remaining tasks are in correct order
        assertTrue(tasks.getTasks().get(0).getDescription().contains("read book"));
        assertTrue(tasks.getTasks().get(1).getDescription().contains("team meeting"));
        assertTrue(tasks.getTasks().get(2).getDescription().contains("write code"));
    }
    
    @Test
    public void deleteCommand_firstTask_deletesCorrectly() throws Exception {
        DeleteCommand command = new DeleteCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("All done, sweetheart!"));
        assertEquals(3, tasks.size());
        
        // First task should now be the former second task (deadline)
        assertTrue(tasks.getTasks().get(0) instanceof Deadline);
        assertTrue(tasks.getTasks().get(0).getDescription().contains("submit assignment"));
    }
    
    @Test
    public void deleteCommand_lastTask_deletesCorrectly() throws Exception {
        DeleteCommand command = new DeleteCommand(4);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("All done, sweetheart!"));
        assertEquals(3, tasks.size());
        
        // Last task should now be the event
        assertTrue(tasks.getTasks().get(2) instanceof Event);
        assertTrue(tasks.getTasks().get(2).getDescription().contains("team meeting"));
    }
    
    @Test
    public void deleteCommand_singleTask_deletesAndLeavesEmpty() throws Exception {
        // Create list with single task
        TaskList singleTaskList = new TaskList();
        singleTaskList.addTask("todo single task");
        assertEquals(1, singleTaskList.size());
        
        DeleteCommand command = new DeleteCommand(1);
        command.setData(singleTaskList, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Now we're focusing on 0 lovely tasks"));
        assertEquals(0, singleTaskList.size());
    }

    // ====================== Exception Tests ======================
    
    @Test
    public void deleteCommand_invalidTaskNumber_throwsException() {
        DeleteCommand command = new DeleteCommand(0);
        command.setData(tasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("Oh sweetie!"));
        assertTrue(exception.getMessage().contains("delete"));
        assertTrue(exception.getMessage().contains("between 1 and 4"));
    }
    
    @Test
    public void deleteCommand_taskNumberTooHigh_throwsException() {
        DeleteCommand command = new DeleteCommand(10);
        command.setData(tasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("between 1 and 4"));
        
        // Verify no tasks were deleted
        assertEquals(4, tasks.size());
    }
    
    @Test
    public void deleteCommand_negativeTaskNumber_throwsException() {
        DeleteCommand command = new DeleteCommand(-1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, command::execute);
        assertEquals(4, tasks.size()); // No tasks should be deleted
    }
    
    @Test
    public void deleteCommand_emptyTaskList_throwsException() {
        TaskList emptyTasks = new TaskList();
        assertEquals(0, emptyTasks.size());
        
        DeleteCommand command = new DeleteCommand(1);
        command.setData(emptyTasks, storage);
        
        InvalidTaskNumberException exception = assertThrows(
            InvalidTaskNumberException.class, 
            command::execute
        );
        assertTrue(exception.getMessage().contains("between 1 and 0"));
    }

    // ====================== State Preservation Tests ======================
    
    @Test
    public void deleteCommand_preservesTaskState_afterDeletion() throws Exception {
        // Mark some tasks as done
        tasks.markTask(1);
        tasks.markTask(3);
        
        assertTrue(tasks.getTasks().get(0).getIsDone()); // todo read book
        assertFalse(tasks.getTasks().get(1).getIsDone()); // deadline
        assertTrue(tasks.getTasks().get(2).getIsDone()); // event
        assertFalse(tasks.getTasks().get(3).getIsDone()); // todo write code
        
        // Delete the deadline (index 2, task number 2)
        DeleteCommand command = new DeleteCommand(2);
        command.setData(tasks, storage);
        command.execute();
        
        // Verify remaining tasks maintain their done status
        assertEquals(3, tasks.size());
        assertTrue(tasks.getTasks().get(0).getIsDone()); // todo read book
        assertTrue(tasks.getTasks().get(1).getIsDone()); // event (was at index 2)
        assertFalse(tasks.getTasks().get(2).getIsDone()); // todo write code (was at index 3)
    }
    
    @Test
    public void deleteCommand_taskNumberingUpdates_afterDeletion() throws Exception {
        // Delete middle task
        DeleteCommand command1 = new DeleteCommand(2);
        command1.setData(tasks, storage);
        command1.execute();
        assertEquals(3, tasks.size());
        
        // What was previously task 3 (event) is now task 2
        // What was previously task 4 (todo write code) is now task 3
        
        // Delete what is now task 2 (the event)
        DeleteCommand command2 = new DeleteCommand(2);
        command2.setData(tasks, storage);
        command2.execute();
        assertEquals(2, tasks.size());
        
        // Verify correct tasks remain
        assertTrue(tasks.getTasks().get(0).getDescription().contains("read book"));
        assertTrue(tasks.getTasks().get(1).getDescription().contains("write code"));
    }

    // ====================== Multiple Deletion Tests ======================
    
    @Test
    public void deleteCommand_multipleSequentialDeletions_worksCorrectly() throws Exception {
        assertEquals(4, tasks.size());
        
        // Delete from end to avoid index shifting issues
        DeleteCommand command4 = new DeleteCommand(4);
        command4.setData(tasks, storage);
        command4.execute();
        assertEquals(3, tasks.size());
        
        DeleteCommand command3 = new DeleteCommand(3);
        command3.setData(tasks, storage);
        command3.execute();
        assertEquals(2, tasks.size());
        
        DeleteCommand command2 = new DeleteCommand(2);
        command2.setData(tasks, storage);
        command2.execute();
        assertEquals(1, tasks.size());
        
        DeleteCommand command1 = new DeleteCommand(1);
        command1.setData(tasks, storage);
        command1.execute();
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void deleteCommand_deleteFromBeginning_shiftsIndexesCorrectly() throws Exception {
        // Store original descriptions for verification
        String task2Desc = tasks.getTasks().get(1).getDescription();
        String task3Desc = tasks.getTasks().get(2).getDescription();
        String task4Desc = tasks.getTasks().get(3).getDescription();
        
        // Delete first task
        DeleteCommand command = new DeleteCommand(1);
        command.setData(tasks, storage);
        command.execute();
        
        assertEquals(3, tasks.size());
        
        // Verify that tasks shifted correctly
        assertEquals(task2Desc, tasks.getTasks().get(0).getDescription());
        assertEquals(task3Desc, tasks.getTasks().get(1).getDescription());
        assertEquals(task4Desc, tasks.getTasks().get(2).getDescription());
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void deleteCommand_afterAddingNewTasks_correctTaskNumbers() throws Exception {
        // Delete a task
        DeleteCommand command = new DeleteCommand(2);
        command.setData(tasks, storage);
        command.execute();
        assertEquals(3, tasks.size());
        
        // Add new tasks
        tasks.addTask("todo new task 1");
        tasks.addTask("deadline new deadline /by 2023-12-20");
        assertEquals(5, tasks.size());
        
        // Delete one of the new tasks
        DeleteCommand newCommand = new DeleteCommand(5);
        newCommand.setData(tasks, storage);
        CommandResult result = newCommand.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Now we're focusing on 4 lovely tasks"));
        assertEquals(4, tasks.size());
        
        // Verify the new deadline was deleted
        assertFalse(tasks.getTasks().get(3).getDescription().contains("new deadline"));
    }
    
    @Test
    public void deleteCommand_boundaryValues_handledCorrectly() throws Exception {
        // Test minimum valid value
        DeleteCommand minCommand = new DeleteCommand(1);
        minCommand.setData(tasks, storage);
        minCommand.execute();
        assertEquals(3, tasks.size());
        
        // Test maximum valid value (which is now 3 after deletion)
        DeleteCommand maxCommand = new DeleteCommand(3);
        maxCommand.setData(tasks, storage);
        maxCommand.execute();
        assertEquals(2, tasks.size());
    }
    
    @Test
    public void deleteCommand_differentTaskTypes_deletesCorrectly() throws Exception {
        // Test deleting todo
        assertTrue(tasks.getTasks().get(0) instanceof Todo);
        DeleteCommand deleteTodo = new DeleteCommand(1);
        deleteTodo.setData(tasks, storage);
        deleteTodo.execute();
        assertEquals(3, tasks.size());
        
        // Test deleting deadline (now at index 0)
        assertTrue(tasks.getTasks().get(0) instanceof Deadline);
        DeleteCommand deleteDeadline = new DeleteCommand(1);
        deleteDeadline.setData(tasks, storage);
        deleteDeadline.execute();
        assertEquals(2, tasks.size());
        
        // Test deleting event (now at index 0)
        assertTrue(tasks.getTasks().get(0) instanceof Event);
        DeleteCommand deleteEvent = new DeleteCommand(1);
        deleteEvent.setData(tasks, storage);
        deleteEvent.execute();
        assertEquals(1, tasks.size());
        
        // Verify only the last todo remains
        assertTrue(tasks.getTasks().get(0) instanceof Todo);
        assertTrue(tasks.getTasks().get(0).getDescription().contains("write code"));
    }

    // ====================== Integration with Other Operations ======================
    
    @Test
    public void deleteCommand_afterMarkingTasks_deletesCorrectly() throws Exception {
        // Mark some tasks
        tasks.markTask(1);
        tasks.markTask(3);
        
        // Delete marked task
        DeleteCommand command = new DeleteCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("All done, sweetheart!"));
        assertEquals(3, tasks.size());
        
        // Verify remaining marked task is still marked (was task 3, now task 2)
        assertTrue(tasks.getTasks().get(1).getIsDone());
    }
}