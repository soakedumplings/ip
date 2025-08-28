package honey.tasklist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import honey.task.Task;
import honey.task.Todo;
import honey.task.Deadline;
import honey.task.Event;
import honey.exceptions.HoneyException;
import honey.exceptions.InvalidTaskNumberException;

public class TaskListTest {
    private TaskList taskList;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        System.setOut(new PrintStream(outContent));
    }
    
    @Test
    public void testEmptyTaskList() {
        assertEquals(0, taskList.size());
        assertTrue(taskList.getTasks().isEmpty());
    }
    
    @Test
    public void testTaskListWithInitialTasks() throws HoneyException {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("todo read book"));
        TaskList taskListWithTasks = new TaskList(initialTasks);
        assertEquals(1, taskListWithTasks.size());
    }
    
    @Test
    public void testAddTodoTask() throws HoneyException {
        taskList.addTask("todo read book");
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
        assertTrue(outContent.toString().contains("Now you have 1 tasks in the list."));
    }
    
    @Test
    public void testAddDeadlineTask() throws HoneyException {
        taskList.addTask("deadline return book /by 2019-10-15");
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testAddEventTask() throws HoneyException {
        taskList.addTask("event project meeting /from 2019-10-15 /to 2019-10-16");
        assertEquals(1, taskList.size());
        assertTrue(outContent.toString().contains("Got it. I've added this task:"));
    }
    
    @Test
    public void testMarkTask() throws HoneyException {
        taskList.addTask("todo read book");
        outContent.reset();
        taskList.markTask(1);
        assertTrue(outContent.toString().contains("Nice! I've marked this task as done:"));
        assertTrue(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testMarkInvalidTaskNumber() throws HoneyException {
        taskList.addTask("todo read book");
        assertThrows(InvalidTaskNumberException.class, () -> {
            taskList.markTask(2);
        });
        assertThrows(InvalidTaskNumberException.class, () -> {
            taskList.markTask(0);
        });
    }
    
    @Test
    public void testUnmarkTask() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.markTask(1);
        outContent.reset();
        taskList.unmarkTask(1);
        assertTrue(outContent.toString().contains("OK, I've marked this task as not done yet:"));
        assertFalse(taskList.getTasks().get(0).getIsDone());
    }
    
    @Test
    public void testUnmarkInvalidTaskNumber() throws HoneyException {
        taskList.addTask("todo read book");
        assertThrows(InvalidTaskNumberException.class, () -> {
            taskList.unmarkTask(2);
        });
    }
    
    @Test
    public void testDeleteTask() throws HoneyException {
        taskList.addTask("todo read book");
        outContent.reset();
        taskList.deleteTask(1);
        assertEquals(0, taskList.size());
        assertTrue(outContent.toString().contains("Noted. I've removed this task:"));
        assertTrue(outContent.toString().contains("Now you have 0 tasks in the list."));
    }
    
    @Test
    public void testDeleteInvalidTaskNumber() throws HoneyException {
        taskList.addTask("todo read book");
        assertThrows(InvalidTaskNumberException.class, () -> {
            taskList.deleteTask(2);
        });
    }
    
    @Test
    public void testListEmptyTasks() {
        taskList.listTasks();
        assertTrue(outContent.toString().contains("No tasks in your list!"));
    }
    
    @Test
    public void testListTasks() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.addTask("deadline return book /by 2019-10-15");
        outContent.reset();
        taskList.listTasks();
        assertTrue(outContent.toString().contains("Here are the tasks in your list:"));
        assertTrue(outContent.toString().contains("1."));
        assertTrue(outContent.toString().contains("2."));
    }
    
    @Test
    public void testFindTasksDue() throws HoneyException {
        taskList.addTask("deadline return book /by 2019-10-15");
        taskList.addTask("event project meeting /from 2019-10-15 /to 2019-10-16");
        outContent.reset();
        taskList.findTasksDue("2019-10-15");
        assertTrue(outContent.toString().contains("Here are the tasks due on Oct 15 2019:"));
    }
    
    @Test
    public void testFindTasksDueNoTasks() throws HoneyException {
        taskList.addTask("todo read book");
        outContent.reset();
        taskList.findTasksDue("2019-10-15");
        assertTrue(outContent.toString().contains("No tasks due on Oct 15 2019!"));
    }
    
    @Test
    public void testFindTasksDueInvalidDate() {
        assertThrows(HoneyException.class, () -> {
            taskList.findTasksDue("invalid-date");
        });
    }
    
    @Test
    public void testFindTasksWithMatches() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.addTask("deadline return book /by 2019-10-15");
        taskList.addTask("event meeting /from 2019-10-15 /to 2019-10-16");
        outContent.reset();
        
        taskList.findTasks("book");
        String output = outContent.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("return book"));
        assertFalse(output.contains("meeting"));
    }
    
    @Test
    public void testFindTasksCaseInsensitive() throws HoneyException {
        taskList.addTask("todo Read Book");
        taskList.addTask("deadline Return BOOK /by 2019-10-15");
        outContent.reset();
        
        taskList.findTasks("book");
        String output = outContent.toString();
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Read Book"));
        assertTrue(output.contains("Return BOOK"));
    }
    
    @Test
    public void testFindTasksNoMatches() throws HoneyException {
        taskList.addTask("todo read book");
        taskList.addTask("deadline submit report /by 2019-10-15");
        outContent.reset();
        
        taskList.findTasks("meeting");
        assertTrue(outContent.toString().contains("No matching tasks found!"));
    }
    
    @Test
    public void testFindTasksEmptyList() {
        outContent.reset();
        taskList.findTasks("book");
        assertTrue(outContent.toString().contains("No matching tasks found!"));
    }
    
    public void tearDown() {
        System.setOut(originalOut);
    }
}