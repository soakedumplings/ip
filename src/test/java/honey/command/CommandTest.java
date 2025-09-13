package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import honey.storage.Storage;
import honey.tasklist.TaskList;

/**
 * Simple tests for key commands.
 * Tests command execution logic only.
 */
public class CommandTest {
    private TaskList tasks;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        storage = new Storage("test_data/command_test.txt");
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
}
