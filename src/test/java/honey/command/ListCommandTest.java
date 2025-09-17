package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Comprehensive tests for ListCommand functionality.
 * Tests listing tasks in various scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class ListCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        File testFile = tempDir.resolve("list_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
    }

    // ====================== Basic List Tests ======================
    
    @Test
    public void execute_emptyList_returnsNoTasksMessage() throws Exception {
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertEquals("What a peaceful moment! 🌺 Our hive is empty and ready for new adventures, my dear Bee! 🍯", 
                    result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_singleTask_returnsFormattedList() throws Exception {
        tasks.addTask("todo read book");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here's our beautiful collection"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("[T]"));
        assertTrue(output.contains("[ ]")); // Should be unmarked
    }
    
    @Test
    public void execute_multipleTasks_returnsAllTasksNumbered() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here's our beautiful collection"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        assertTrue(output.contains("3."));
        
        // Check all tasks are present
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("team meeting"));
        
        // Check task types are shown
        assertTrue(output.contains("[T]"));
        assertTrue(output.contains("[D]"));
        assertTrue(output.contains("[E]"));
    }

    // ====================== Task Status Tests ======================
    
    @Test
    public void execute_mixedTaskStatuses_showsCorrectMarkers() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        
        // Mark some tasks as done
        tasks.markTask(1);
        tasks.markTask(3);
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Check for correct status markers
        assertTrue(output.contains("[X]")); // Marked tasks
        assertTrue(output.contains("[ ]")); // Unmarked task
        
        // Verify specific tasks have correct status
        String[] lines = output.split("\n");
        boolean foundMarkedTodo = false;
        boolean foundUnmarkedDeadline = false;
        boolean foundMarkedEvent = false;
        
        for (String line : lines) {
            if (line.contains("read book") && line.contains("[X]")) {
                foundMarkedTodo = true;
            }
            if (line.contains("submit assignment") && line.contains("[ ]")) {
                foundUnmarkedDeadline = true;
            }
            if (line.contains("team meeting") && line.contains("[X]")) {
                foundMarkedEvent = true;
            }
        }
        
        assertTrue(foundMarkedTodo);
        assertTrue(foundUnmarkedDeadline);
        assertTrue(foundMarkedEvent);
    }
    
    @Test
    public void execute_allTasksMarked_showsAllWithXMarker() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        
        // Mark all tasks
        tasks.markTask(1);
        tasks.markTask(2);
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Count X markers (should be 2)
        int xCount = output.split("\\[X\\]", -1).length - 1;
        assertEquals(2, xCount);
        
        // Should not contain any unmarked markers
        assertFalse(output.contains("[ ]"));
    }

    // ====================== Task Type Display Tests ======================
    
    @Test
    public void execute_todoTasks_displaysCorrectFormat() throws Exception {
        tasks.addTask("todo simple task");
        tasks.addTask("todo task with special characters: @#$%");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("[T][ ] simple task"));
        assertTrue(output.contains("[T][ ] task with special characters: @#$%"));
    }
    
    @Test
    public void execute_deadlineTasks_displaysWithDates() throws Exception {
        tasks.addTask("deadline submit report /by 2023-12-15");
        tasks.addTask("deadline buy gift /by 15/12/2023 1800");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("[D]"));
        assertTrue(output.contains("submit report"));
        assertTrue(output.contains("buy gift"));
        
        // Should contain formatted dates
        assertTrue(output.contains("Dec 15 2023"));
    }
    
    @Test
    public void execute_eventTasks_displaysWithDateRange() throws Exception {
        tasks.addTask("event conference /from 2023-12-10 /to 2023-12-12");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("[E]"));
        assertTrue(output.contains("conference"));
        assertTrue(output.contains("Dec 10 2023"));
        assertTrue(output.contains("Dec 12 2023"));
    }

    // ====================== Large Dataset Tests ======================
    
    @Test
    public void execute_manyTasks_handlesLargeList() throws Exception {
        // Add many tasks
        for (int i = 1; i <= 50; i++) {
            tasks.addTask("todo task number " + i);
        }
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here's our beautiful collection"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("50."));
        assertTrue(output.contains("task number 1"));
        assertTrue(output.contains("task number 50"));
        
        // Count the number of numbered lines
        String[] lines = output.split("\n");
        int numberedLines = 0;
        for (String line : lines) {
            if (line.matches("\\d+\\..*")) {
                numberedLines++;
            }
        }
        assertEquals(50, numberedLines);
    }

    // ====================== Integration Tests ======================
    
    @Test
    public void execute_afterTaskOperations_reflectsCurrentState() throws Exception {
        // Add initial tasks
        tasks.addTask("todo task 1");
        tasks.addTask("todo task 2");
        tasks.addTask("todo task 3");
        
        // Mark one task
        tasks.markTask(2);
        
        // Delete one task
        tasks.deleteTask(1);
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should show 2 remaining tasks
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        assertFalse(output.contains("3."));
        
        // Task 2 (now task 1) should be marked
        assertTrue(output.contains("task 2") && output.contains("[X]"));
        
        // Task 3 (now task 2) should be unmarked
        assertTrue(output.contains("task 3") && output.contains("[ ]"));
        
        // Task 1 should not appear (was deleted)
        assertFalse(output.contains("task 1"));
    }
    
    @Test
    public void execute_doesNotRequireStorage() throws Exception {
        // Test that ListCommand works even without proper storage setup
        tasks.addTask("todo test task");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, null); // Pass null storage
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("test task"));
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void execute_taskWithLongDescription_displaysCorrectly() throws Exception {
        String longDescription = "very ".repeat(50) + "long task description";
        tasks.addTask("todo " + longDescription);
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains(longDescription));
        assertTrue(output.contains("1."));
    }
    
    @Test
    public void execute_taskWithSpecialCharacters_displaysCorrectly() throws Exception {
        tasks.addTask("todo task with émojis 🎉 and spéciâl chars: @#$%^&*()");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("émojis 🎉"));
        assertTrue(output.contains("spéciâl chars"));
        assertTrue(output.contains("@#$%^&*()"));
    }
    
    @Test
    public void execute_consecutiveInvocations_consistentResults() throws Exception {
        tasks.addTask("todo consistent task");
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        
        // Execute multiple times
        CommandResult result1 = command.execute();
        CommandResult result2 = command.execute();
        CommandResult result3 = command.execute();
        
        // Results should be identical
        assertEquals(result1.getFeedbackToUser(), result2.getFeedbackToUser());
        assertEquals(result2.getFeedbackToUser(), result3.getFeedbackToUser());
    }
}