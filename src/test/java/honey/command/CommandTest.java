package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import honey.exceptions.HoneyException;
import honey.exceptions.InvalidTaskNumberException;
import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for all command classes.
 * Tests both successful execution and exception handling scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class CommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        File testFile = tempDir.resolve("command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
    }
    
    /**
     * Creates a task list with sample data for testing.
     */
    private TaskList createSampleTaskList() throws HoneyException {
        TaskList sampleTasks = new TaskList();
        sampleTasks.addTask("todo read book");
        sampleTasks.addTask("deadline submit assignment /by 2023-12-15");
        sampleTasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        return sampleTasks;
    }

    @Test
    public void listCommand_emptyList_returnsNoTasksMessage() throws Exception {
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        assertEquals("No tasks in your list!", result.getFeedbackToUser());
    }

    @Test
    public void addCommand_validTodo_addsTask() throws Exception {
        AddCommand command = new AddCommand("todo read book");
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        assertTrue(result.getFeedbackToUser().contains("Got it. I've added this task"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void markCommand_validTask_marksAsDone() throws Exception {
        // Setup - add a task first
        tasks.addTask("todo read book");

        MarkCommand command = new MarkCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        assertTrue(result.getFeedbackToUser().contains("Nice! I've marked this task as done"));
        assertTrue(tasks.getTasks().get(0).getIsDone());
    }

    @Test
    public void exitCommand_returnsExitMessage() throws Exception {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();

        assertTrue(result.getFeedbackToUser().contains("Bye Bee Bee"));
        assertTrue(result.isExit());
    }

    @Test
    public void sortCommand_noDeadlines_returnsNoDeadlinesMessage() throws Exception {
        // Add non-deadline tasks
        tasks.addTask("todo read book");
        tasks.addTask("event meeting /from 2023-12-01 /to 2023-12-02");

        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        assertEquals("No deadline tasks found in your list!", result.getFeedbackToUser());
    }

    @Test
    public void sortCommand_validDeadlines_sortsCorrectly() throws Exception {
        // Add deadline tasks in unsorted order
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("deadline buy gift /by 2023-12-10");
        tasks.addTask("deadline prepare presentation /by 2023-12-20");

        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here are your deadline tasks sorted by date:"));
        // Check that the earlier deadline appears first in the output
        int giftIndex = output.indexOf("buy gift");
        int assignmentIndex = output.indexOf("submit assignment");
        int presentationIndex = output.indexOf("prepare presentation");
        assertTrue(giftIndex < assignmentIndex);
        assertTrue(assignmentIndex < presentationIndex);
    }

    @Test
    public void sortCommand_invalidSortType_returnsErrorMessage() throws Exception {
        SortCommand command = new SortCommand("todo");
        command.setData(tasks, storage);
        CommandResult result = command.execute();

        assertTrue(result.getFeedbackToUser().contains("Sorry, I can only sort 'deadline' tasks for now"));
    }

    // ====================== Detailed Exception Tests ======================
    
    @Test
    public void markCommand_invalidTaskNumber_throwsException() throws Exception {
        MarkCommand command = new MarkCommand(1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, () -> command.execute());
    }
    
    @Test
    public void markCommand_taskNumberTooHigh_throwsException() throws Exception {
        tasks.addTask("todo read book");
        
        MarkCommand command = new MarkCommand(5);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, () -> command.execute());
    }
    
    @Test
    public void markCommand_taskNumberZero_throwsException() throws Exception {
        tasks.addTask("todo read book");
        
        MarkCommand command = new MarkCommand(0);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, () -> command.execute());
    }
    
    @Test
    public void unmarkCommand_invalidTaskNumber_throwsException() throws Exception {
        UnmarkCommand command = new UnmarkCommand(1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, () -> command.execute());
    }
    
    @Test
    public void unmarkCommand_validTask_unmarksTask() throws Exception {
        tasks.addTask("todo read book");
        tasks.markTask(1); // First mark it as done
        
        UnmarkCommand command = new UnmarkCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("No worries, darling!"));
        assertFalse(tasks.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void deleteCommand_invalidTaskNumber_throwsException() throws Exception {
        DeleteCommand command = new DeleteCommand(1);
        command.setData(tasks, storage);
        
        assertThrows(InvalidTaskNumberException.class, () -> command.execute());
    }
    
    @Test
    public void deleteCommand_validTask_deletesTask() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("todo write code");
        assertEquals(2, tasks.size());
        
        DeleteCommand command = new DeleteCommand(1);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("All done, sweetheart!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0).getDescription().contains("write code"));
    }
    
    @Test
    public void findCommand_noMatches_returnsNoMatchesMessage() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        
        FindCommand command = new FindCommand("xyz");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Hmm, my sweet Bee!"));
    }
    
    @Test
    public void findCommand_hasMatches_returnsMatchingTasks() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("todo read magazine");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        
        FindCommand command = new FindCommand("read");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("book"));
        assertTrue(output.contains("magazine"));
        assertFalse(output.contains("assignment"));
    }
    
    @Test
    public void dueCommand_noTasksDue_returnsNoTasksMessage() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        
        DueCommand command = new DueCommand("2023-12-20");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("How lovely! No urgent tasks"));
    }
    
    @Test
    public void dueCommand_hasTasksDue_returnsTasksDue() throws Exception {
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("deadline buy gift /by 2023-12-15");
        tasks.addTask("event meeting /from 2023-12-15 /to 2023-12-16");
        
        DueCommand command = new DueCommand("2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here are our important tasks"));
        assertTrue(output.contains("assignment"));
        assertTrue(output.contains("gift"));
        assertTrue(output.contains("meeting"));
    }
    
    @Test
    public void listCommand_withTasks_returnsFormattedList() throws Exception {
        tasks = createSampleTaskList();
        
        ListCommand command = new ListCommand();
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Here's our beautiful collection"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        assertTrue(output.contains("3."));
    }
    
    @Test
    public void addCommand_validDeadline_addsDeadlineTask() throws Exception {
        AddCommand command = new AddCommand("deadline submit report /by 2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0).getDescription().contains("deadline"));
    }
    
    @Test
    public void addCommand_validEvent_addsEventTask() throws Exception {
        AddCommand command = new AddCommand("event team meeting /from 2023-12-10 /to 2023-12-11");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0).getDescription().contains("event"));
    }
    
    @Test
    public void addCommand_invalidTaskType_throwsException() throws Exception {
        AddCommand command = new AddCommand("invalid task description");
        command.setData(tasks, storage);
        
        assertThrows(HoneyException.class, () -> command.execute());
    }
    
    @Test
    public void incorrectCommand_execute_returnsErrorMessage() throws Exception {
        IncorrectCommand command = new IncorrectCommand("Unknown command: invalid");
        CommandResult result = command.execute();
        
        assertEquals("Unknown command: invalid", result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
    
    @Test
    public void exitCommand_doesNotRequireData() {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Sweet dreams, my dear Bee!"));
        assertTrue(result.isExit());
    }
    
    @Test
    public void commandResult_isExitFalseByDefault() throws Exception {
        AddCommand command = new AddCommand("todo read book");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertFalse(result.isExit());
    }
    
    @Test
    public void sortCommand_mixedTaskTypes_sortsOnlyDeadlines() throws Exception {
        tasks.addTask("todo read book");
        tasks.addTask("deadline project due /by 2023-12-20");
        tasks.addTask("event meeting /from 2023-12-10 /to 2023-12-11");
        tasks.addTask("deadline assignment due /by 2023-12-15");
        
        SortCommand command = new SortCommand("deadline");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("beautifully organized"));
        assertTrue(output.contains("assignment"));
        assertTrue(output.contains("project"));
        assertFalse(output.contains("read book"));
        assertFalse(output.contains("meeting"));
        
        // Verify sorting order
        int assignmentIndex = output.indexOf("assignment");
        int projectIndex = output.indexOf("project");
        assertTrue(assignmentIndex < projectIndex);
    }
}
