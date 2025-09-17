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
 * Comprehensive tests for SortCommand functionality.
 * Tests sorting deadlines by date and error handling.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class SortCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        File testFile = tempDir.resolve("sort_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
    }

    // ====================== Basic Sort Tests ======================
    
    @Test
    public void execute_noDeadlines_returnsNoDeadlinesMessage() throws Exception {
        // Add non-deadline tasks
        tasks.addTask("todo read book");
        tasks.addTask("event meeting /from 2023-12-01 /to 2023-12-02");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How wonderful! 🎉 No deadlines to worry about right now"));
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_singleDeadline_returnsSortedList() throws Exception {
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here are our deadline tasks, beautifully organized by date"));
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("1."));
    }
    
    @Test
    public void execute_multipleDeadlines_sortsCorrectly() throws Exception {
        // Add deadline tasks in unsorted order
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("deadline buy gift /by 2023-12-10");
        tasks.addTask("deadline prepare presentation /by 2023-12-20");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("beautifully organized by date"));
        
        // Check that the earlier deadline appears first in the output
        int giftIndex = output.indexOf("buy gift");
        int assignmentIndex = output.indexOf("submit assignment");
        int presentationIndex = output.indexOf("prepare presentation");
        
        assertTrue(giftIndex < assignmentIndex);
        assertTrue(assignmentIndex < presentationIndex);
        assertTrue(giftIndex > 0); // Should actually be found
        assertTrue(assignmentIndex > 0);
        assertTrue(presentationIndex > 0);
    }

    // ====================== Mixed Task Types Tests ======================
    
    @Test
    public void execute_mixedTaskTypes_sortsOnlyDeadlines() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline project due /by 2023-12-20");
        tasks.addTask("event meeting /from 2023-12-10 /to 2023-12-11");
        tasks.addTask("deadline assignment due /by 2023-12-15");
        tasks.addTask("todo write code");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("beautifully organized"));
        
        // Should contain deadlines
        assertTrue(output.contains("assignment"));
        assertTrue(output.contains("project"));
        
        // Should NOT contain non-deadline tasks
        assertFalse(output.contains("read book"));
        assertFalse(output.contains("meeting"));
        assertFalse(output.contains("write code"));
        
        // Verify sorting order (assignment comes before project)
        int assignmentIndex = output.indexOf("assignment");
        int projectIndex = output.indexOf("project");
        assertTrue(assignmentIndex < projectIndex);
    }

    // ====================== Task Status Tests ======================
    
    @Test
    public void execute_markedAndUnmarkedDeadlines_showsCorrectStatus() throws Exception {
        tasks.addTask("deadline task1 /by 2023-12-10");
        tasks.addTask("deadline task2 /by 2023-12-15");
        tasks.addTask("deadline task3 /by 2023-12-20");
        
        // Mark one task as done
        tasks.markTask(2);
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should show both marked and unmarked status
        assertTrue(output.contains("[X]")); // Marked task
        assertTrue(output.contains("[ ]")); // Unmarked tasks
        
        // Check that the marked task (task2) still appears with correct status
        assertTrue(output.contains("task2") && output.contains("[X]"));
    }

    // ====================== Overdue Tasks Tests ======================
    
    @Test
    public void execute_overdueDeadlines_showsOverdueIndicator() throws Exception {
        // Add overdue deadline (past date)
        tasks.addTask("deadline overdue task /by 2020-01-01");
        tasks.addTask("deadline future task /by 2025-12-15");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should show overdue indicator for past deadline
        assertTrue(output.contains("[OVERDUE]"));
        assertTrue(output.contains("overdue task"));
        
        // Future task should not have overdue indicator
        int futureTaskIndex = output.indexOf("future task");
        int overdueIndicatorAfterFuture = output.indexOf("[OVERDUE]", futureTaskIndex);
        assertTrue(overdueIndicatorAfterFuture == -1); // No overdue indicator after future task
    }
    
    @Test
    public void execute_mixedOverdueAndFuture_sortsChronologically() throws Exception {
        tasks.addTask("deadline future deadline /by 2025-12-15");
        tasks.addTask("deadline old overdue /by 2020-01-01");
        tasks.addTask("deadline recent overdue /by 2022-01-01");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Check chronological order
        int oldIndex = output.indexOf("old overdue");
        int recentIndex = output.indexOf("recent overdue");
        int futureIndex = output.indexOf("future deadline");
        
        assertTrue(oldIndex < recentIndex);
        assertTrue(recentIndex < futureIndex);
        
        // Both overdue tasks should have overdue indicator
        String beforeFuture = output.substring(0, futureIndex);
        assertTrue(beforeFuture.split("\\[OVERDUE\\]").length >= 3); // At least 2 overdue indicators
    }

    // ====================== Error Handling Tests ======================
    
    @Test
    public void execute_invalidSortType_returnsErrorMessage() throws Exception {
        SortCommand command = new SortCommand("todo");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Sorry, I can only sort 'deadline' tasks for now"));
    }
    
    @Test
    public void execute_emptySortType_returnsErrorMessage() throws Exception {
        SortCommand command = new SortCommand("");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Sorry, I can only sort 'deadline' tasks for now"));
    }
    
    @Test
    public void execute_nullSortType_returnsErrorMessage() throws Exception {
        SortCommand command = new SortCommand(null);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Sorry, I can only sort 'deadline' tasks for now"));
    }

    // ====================== Case Sensitivity Tests ======================
    
    @Test
    public void execute_caseInsensitiveDeadline_worksCorrectly() throws Exception {
        tasks.addTask("deadline test task /by 2023-12-15");
        
        // Test different cases
        SortCommand commandLower = new SortCommand("deadline");
        commandLower.setData(tasks, storage);
        CommandResult resultLower = commandLower.execute();
        
        SortCommand commandUpper = new SortCommand("DEADLINE");
        commandUpper.setData(tasks, storage);
        CommandResult resultUpper = commandUpper.execute();
        
        SortCommand commandMixed = new SortCommand("DeAdLiNe");
        commandMixed.setData(tasks, storage);
        CommandResult resultMixed = commandMixed.execute();
        
        // All should work the same way
        assertTrue(resultLower.getFeedbackToUser().contains("test task"));
        assertTrue(resultUpper.getFeedbackToUser().contains("test task"));
        assertTrue(resultMixed.getFeedbackToUser().contains("test task"));
    }

    // ====================== Large Dataset Tests ======================
    
    @Test
    public void execute_manyDeadlines_sortsAllCorrectly() throws Exception {
        // Add many deadlines in random order
        String[] dates = {
            "2023-12-25", "2023-12-01", "2023-12-15", "2023-12-08",
            "2023-12-31", "2023-12-03", "2023-12-20", "2023-12-12",
            "2023-12-05", "2023-12-18"
        };
        
        for (int i = 0; i < dates.length; i++) {
            tasks.addTask("deadline task" + i + " /by " + dates[i]);
        }
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should contain all tasks
        for (int i = 0; i < dates.length; i++) {
            assertTrue(output.contains("task" + i));
        }
        
        // Check that December 1st comes before December 25th
        int dec01Index = output.indexOf("task1"); // task1 has 2023-12-01
        int dec25Index = output.indexOf("task0"); // task0 has 2023-12-25
        assertTrue(dec01Index < dec25Index);
    }

    // ====================== Integration Tests ======================
    
    @Test
    public void execute_afterTaskOperations_reflectsCurrentState() throws Exception {
        // Add initial deadlines
        tasks.addTask("deadline task1 /by 2023-12-20");
        tasks.addTask("deadline task2 /by 2023-12-10");
        tasks.addTask("deadline task3 /by 2023-12-15");
        
        // Delete middle task
        tasks.deleteTask(3); // Remove task3
        
        // Mark remaining task
        tasks.markTask(1); // Mark task1
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should only show 2 tasks
        assertTrue(output.contains("task1"));
        assertTrue(output.contains("task2"));
        assertFalse(output.contains("task3")); // Was deleted
        
        // Should show task2 first (earlier date)
        int task2Index = output.indexOf("task2");
        int task1Index = output.indexOf("task1");
        assertTrue(task2Index < task1Index);
        
        // task1 should be marked
        assertTrue(output.contains("task1") && output.contains("[X]"));
    }
    
    @Test
    public void execute_emptyTaskList_returnsNoDeadlinesMessage() throws Exception {
        // Empty task list
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How wonderful! 🎉 No deadlines to worry about"));
    }
}