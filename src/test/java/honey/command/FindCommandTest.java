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
 * Comprehensive tests for FindCommand functionality.
 * Tests search functionality with various keywords and scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class FindCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tasks = new TaskList();
        File testFile = tempDir.resolve("find_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
        
        // Set up diverse sample tasks for testing
        tasks.addTask("todo read book about Java programming");
        tasks.addTask("todo read magazine");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("deadline submit report /by 2023-12-20");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        tasks.addTask("event client meeting /from 2023-12-12 /to 2023-12-13");
        tasks.addTask("todo write code for project");
        tasks.addTask("todo buy groceries");
    }

    // ====================== Basic Search Tests ======================
    
    @Test
    public void findCommand_singleMatch_returnsCorrectTask() throws Exception {
        FindCommand command = new FindCommand("groceries");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertFalse(result.isExit());
        
        // Should find the groceries task
        assertTrue(output.contains("groceries"));
        assertTrue(output.contains("1.")); // Should be numbered as 1
        
        // Should not find other tasks
        assertFalse(output.contains("read"));
        assertFalse(output.contains("meeting"));
        assertFalse(output.contains("assignment"));
    }
    
    @Test
    public void findCommand_multipleMatches_returnsAllMatches() throws Exception {
        FindCommand command = new FindCommand("read");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find both read tasks
        assertTrue(output.contains("book about Java"));
        assertTrue(output.contains("magazine"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        
        // Should not find non-matching tasks
        assertFalse(output.contains("assignment"));
        assertFalse(output.contains("meeting"));
        assertFalse(output.contains("groceries"));
    }
    
    @Test
    public void findCommand_noMatches_returnsNoMatchesMessage() throws Exception {
        FindCommand command = new FindCommand("nonexistent");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Hmm, my sweet Bee!"));
        assertTrue(output.contains("couldn't find any tasks"));
        assertTrue(output.contains("try a different search"));
    }

    // ====================== Case Sensitivity Tests ======================
    
    @Test
    public void findCommand_caseInsensitive_findsMatches() throws Exception {
        FindCommand command = new FindCommand("JAVA");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Java programming"));
    }
    
    @Test
    public void findCommand_mixedCase_findsMatches() throws Exception {
        FindCommand command = new FindCommand("ReAd");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("book"));
        assertTrue(output.contains("magazine"));
    }
    
    @Test
    public void findCommand_lowerCase_findsUpperCaseContent() throws Exception {
        // Add a task with uppercase content
        tasks.addTask("todo CLEAN HOUSE");
        
        FindCommand command = new FindCommand("clean");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("CLEAN HOUSE"));
    }

    // ====================== Partial Match Tests ======================
    
    @Test
    public void findCommand_partialWord_findsMatches() throws Exception {
        FindCommand command = new FindCommand("meet");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find both meeting tasks
        assertTrue(output.contains("team meeting"));
        assertTrue(output.contains("client meeting"));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
    }
    
    @Test
    public void findCommand_substring_findsMatches() throws Exception {
        FindCommand command = new FindCommand("mit");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find both submit tasks
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("submit report"));
    }

    // ====================== Different Task Type Tests ======================
    
    @Test
    public void findCommand_findsTodoTasks() throws Exception {
        FindCommand command = new FindCommand("code");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("write code"));
        assertTrue(output.contains("[T]")); // Todo format
    }
    
    @Test
    public void findCommand_findsDeadlineTasks() throws Exception {
        FindCommand command = new FindCommand("assignment");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("[D]")); // Deadline format
        assertTrue(output.contains("Dec 15 2023"));
    }
    
    @Test
    public void findCommand_findsEventTasks() throws Exception {
        FindCommand command = new FindCommand("team");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("team meeting"));
        assertTrue(output.contains("[E]")); // Event format
    }

    // ====================== Task Status Tests ======================
    
    @Test
    public void findCommand_findsMarkedTasks() throws Exception {
        // Mark a task as done
        tasks.markTask(1); // Mark "read book about Java programming"
        
        FindCommand command = new FindCommand("Java");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Java programming"));
        assertTrue(output.contains("[X]")); // Should show as completed
    }
    
    @Test
    public void findCommand_findsUnmarkedTasks() throws Exception {
        FindCommand command = new FindCommand("magazine");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("magazine"));
        assertTrue(output.contains("[ ]")); // Should show as not completed
    }
    
    @Test
    public void findCommand_findsBothMarkedAndUnmarked() throws Exception {
        // Mark one of the submit tasks
        tasks.markTask(3); // Mark "submit assignment"
        
        FindCommand command = new FindCommand("submit");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("submit assignment"));
        assertTrue(output.contains("submit report"));
        assertTrue(output.contains("[X]")); // Marked task
        assertTrue(output.contains("[ ]")); // Unmarked task
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void findCommand_emptyKeyword_findsNothing() throws Exception {
        FindCommand command = new FindCommand("");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Hmm, my sweet Bee!"));
    }
    
    @Test
    public void findCommand_whitespaceKeyword_findsNothing() throws Exception {
        FindCommand command = new FindCommand("   ");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Hmm, my sweet Bee!"));
    }
    
    @Test
    public void findCommand_specialCharacters_findsMatches() throws Exception {
        // Add task with special characters
        tasks.addTask("todo read C++ programming guide");
        
        FindCommand command = new FindCommand("C++");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("C++ programming"));
    }
    
    @Test
    public void findCommand_numbers_findsMatches() throws Exception {
        FindCommand command = new FindCommand("2023");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find tasks with dates
        assertTrue(output.contains("assignment"));
        assertTrue(output.contains("report"));
        assertTrue(output.contains("meeting"));
    }

    // ====================== Multiple Keywords Test ======================
    
    @Test
    public void findCommand_multipleWords_treatedAsOneKeyword() throws Exception {
        FindCommand command = new FindCommand("Java programming");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Java programming"));
    }

    // ====================== Empty Task List Tests ======================
    
    @Test
    public void findCommand_emptyTaskList_returnsNoMatches() throws Exception {
        TaskList emptyTasks = new TaskList();
        
        FindCommand command = new FindCommand("anything");
        command.setData(emptyTasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        assertTrue(output.contains("Hmm, my sweet Bee!"));
    }

    // ====================== Large Dataset Tests ======================
    
    @Test
    public void findCommand_manyMatches_returnsAllCorrectly() throws Exception {
        // Add more tasks with "task" keyword
        for (int i = 1; i <= 10; i++) {
            tasks.addTask("todo task number " + i);
        }
        
        FindCommand command = new FindCommand("task");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find all 10 new tasks
        for (int i = 1; i <= 10; i++) {
            assertTrue(output.contains("task number " + i));
        }
        
        // Check numbering
        assertTrue(output.contains("1."));
        assertTrue(output.contains("10."));
    }

    // ====================== Integration Tests ======================
    
    @Test
    public void findCommand_afterDeletingTasks_searchesRemainingTasks() throws Exception {
        // Delete a task that would match
        tasks.deleteTask(1); // Delete "read book about Java programming"
        
        FindCommand command = new FindCommand("read");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should only find the magazine task now
        assertTrue(output.contains("magazine"));
        assertFalse(output.contains("Java"));
        assertTrue(output.contains("1.")); // Should be numbered as 1
        assertFalse(output.contains("2.")); // Should not have a second result
    }
    
    @Test
    public void findCommand_afterAddingTasks_searchesAllTasks() throws Exception {
        // Add a new task that matches existing keyword
        tasks.addTask("todo read newspaper");
        
        FindCommand command = new FindCommand("read");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        String output = result.getFeedbackToUser();
        
        // Should find all three read tasks
        assertTrue(output.contains("Java"));
        assertTrue(output.contains("magazine"));
        assertTrue(output.contains("newspaper"));
        assertTrue(output.contains("3.")); // Should have three results
    }
}