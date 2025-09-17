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

import honey.exceptions.EmptyDescriptionException;
import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidDateFormatException;
import honey.storage.Storage;
import honey.task.Deadline;
import honey.task.Event;
import honey.task.Todo;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for AddCommand functionality.
 * Tests all task types (Todo, Deadline, Event) and their exception scenarios.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class AddCommandTest {
    private TaskList tasks;
    private Storage storage;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        File testFile = tempDir.resolve("add_command_test.txt").toFile();
        storage = new Storage(testFile.getPath());
    }

    // ====================== Todo Task Tests ======================
    
    @Test
    public void addCommand_validTodo_addsTaskCorrectly() throws Exception {
        AddCommand command = new AddCommand("todo read book");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        // Check response message
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertTrue(result.getFeedbackToUser().contains("Together we're managing 1 sweet tasks!"));
        assertFalse(result.isExit());
        
        // Check task was added
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0) instanceof Todo);
        assertEquals("todo read book", tasks.getTasks().get(0).getDescription());
        assertFalse(tasks.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void addCommand_todoWithMultipleWords_addsTaskCorrectly() throws Exception {
        AddCommand command = new AddCommand("todo read book about machine learning");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertEquals("todo read book about machine learning", tasks.getTasks().get(0).getDescription());
    }
    
    @Test
    public void addCommand_emptyTodoDescription_throwsException() throws Exception {
        AddCommand command = new AddCommand("todo");
        command.setData(tasks, storage);
        
        assertThrows(EmptyDescriptionException.class, command::execute);
    }
    
    @Test
    public void addCommand_todoWithOnlySpaces_throwsException() throws Exception {
        AddCommand command = new AddCommand("todo   ");
        command.setData(tasks, storage);
        
        assertThrows(EmptyDescriptionException.class, command::execute);
    }

    // ====================== Deadline Task Tests ======================
    
    @Test
    public void addCommand_validDeadline_addsTaskCorrectly() throws Exception {
        AddCommand command = new AddCommand("deadline submit assignment /by 2023-12-15");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0) instanceof Deadline);
        assertTrue(tasks.getTasks().get(0).toString().contains("submit assignment"));
        assertTrue(tasks.getTasks().get(0).toString().contains("Dec 15 2023"));
    }
    
    @Test
    public void addCommand_deadlineWithTime_addsTaskCorrectly() throws Exception {
        AddCommand command = new AddCommand("deadline submit report /by 15/12/2023 1800");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0) instanceof Deadline);
    }
    
    @Test
    public void addCommand_emptyDeadlineDescription_throwsException() throws Exception {
        AddCommand command = new AddCommand("deadline");
        command.setData(tasks, storage);
        
        assertThrows(EmptyDescriptionException.class, command::execute);
    }
    
    @Test
    public void addCommand_deadlineWithoutBy_throwsException() throws Exception {
        AddCommand command = new AddCommand("deadline submit assignment");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }
    
    @Test
    public void addCommand_deadlineWithInvalidDate_throwsException() throws Exception {
        AddCommand command = new AddCommand("deadline submit assignment /by invalid-date");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }
    
    @Test
    public void addCommand_deadlineWithEmptyDate_throwsException() throws Exception {
        AddCommand command = new AddCommand("deadline submit assignment /by");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }

    // ====================== Event Task Tests ======================
    
    @Test
    public void addCommand_validEvent_addsTaskCorrectly() throws Exception {
        AddCommand command = new AddCommand("event team meeting /from 2023-12-10 /to 2023-12-11");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0) instanceof Event);
        assertTrue(tasks.getTasks().get(0).toString().contains("team meeting"));
    }
    
    @Test
    public void addCommand_emptyEventDescription_throwsException() throws Exception {
        AddCommand command = new AddCommand("event");
        command.setData(tasks, storage);
        
        assertThrows(EmptyDescriptionException.class, command::execute);
    }
    
    @Test
    public void addCommand_eventWithoutFrom_throwsException() throws Exception {
        AddCommand command = new AddCommand("event team meeting /to 2023-12-11");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }
    
    @Test
    public void addCommand_eventWithoutTo_throwsException() throws Exception {
        AddCommand command = new AddCommand("event team meeting /from 2023-12-10");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }
    
    @Test
    public void addCommand_eventWithInvalidFromDate_throwsException() throws Exception {
        AddCommand command = new AddCommand("event team meeting /from invalid-date /to 2023-12-11");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }
    
    @Test
    public void addCommand_eventWithInvalidToDate_throwsException() throws Exception {
        AddCommand command = new AddCommand("event team meeting /from 2023-12-10 /to invalid-date");
        command.setData(tasks, storage);
        
        assertThrows(InvalidDateFormatException.class, command::execute);
    }

    // ====================== Invalid Task Type Tests ======================
    
    @Test
    public void addCommand_invalidTaskType_throwsException() throws Exception {
        AddCommand command = new AddCommand("invalid task description");
        command.setData(tasks, storage);
        
        assertThrows(InvalidCommandException.class, command::execute);
    }
    
    @Test
    public void addCommand_emptyInput_throwsException() throws Exception {
        AddCommand command = new AddCommand("");
        command.setData(tasks, storage);
        
        assertThrows(InvalidCommandException.class, command::execute);
    }
    
    @Test
    public void addCommand_nullInput_throwsException() throws Exception {
        assertThrows(Exception.class, () -> new AddCommand(null));
    }

    // ====================== Multiple Tasks Tests ======================
    
    @Test
    public void addCommand_multipleTasks_addsAllCorrectly() throws Exception {
        // Add first task
        AddCommand command1 = new AddCommand("todo read book");
        command1.setData(tasks, storage);
        command1.execute();
        
        // Add second task
        AddCommand command2 = new AddCommand("deadline submit assignment /by 2023-12-15");
        command2.setData(tasks, storage);
        command2.execute();
        
        // Add third task
        AddCommand command3 = new AddCommand("event team meeting /from 2023-12-10 /to 2023-12-11");
        command3.setData(tasks, storage);
        CommandResult result = command3.execute();
        
        assertEquals(3, tasks.size());
        assertTrue(result.getFeedbackToUser().contains("Together we're managing 3 sweet tasks!"));
        
        // Verify task types
        assertTrue(tasks.getTasks().get(0) instanceof Todo);
        assertTrue(tasks.getTasks().get(1) instanceof Deadline);
        assertTrue(tasks.getTasks().get(2) instanceof Event);
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void addCommand_taskWithSpecialCharacters_addsCorrectly() throws Exception {
        AddCommand command = new AddCommand("todo read book: \"Advanced Java\" (2nd edition)");
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0).getDescription().contains("\"Advanced Java\""));
    }
    
    @Test
    public void addCommand_veryLongDescription_addsCorrectly() throws Exception {
        String longDescription = "todo " + "very ".repeat(50) + "long task description";
        AddCommand command = new AddCommand(longDescription);
        command.setData(tasks, storage);
        CommandResult result = command.execute();
        
        assertTrue(result.getFeedbackToUser().contains("Perfect, my dear!"));
        assertEquals(1, tasks.size());
        assertTrue(tasks.getTasks().get(0).getDescription().length() > 200);
    }
}