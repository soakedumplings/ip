package honey.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import honey.task.Task;
import honey.task.Todo;
import honey.task.Deadline;
import honey.task.Event;
import honey.exceptions.HoneyException;

public class StorageTest {
    private Storage storage;
    private final String testFilePath = "test_data/test_honey.txt";
    private final Path testFile = Paths.get(testFilePath);
    
    @BeforeEach
    public void setUp() throws IOException {
        storage = new Storage(testFilePath);
        // Clean up any existing test files recursively
        cleanupDirectory(testFile);
    }
    
    private void cleanupDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                Files.list(path).forEach(subPath -> {
                    try {
                        cleanupDirectory(subPath);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
            }
            Files.deleteIfExists(path);
        }
        if (path.getParent() != null && Files.exists(path.getParent())) {
            try {
                Files.deleteIfExists(path.getParent());
            } catch (IOException e) {
                // Ignore if directory is not empty
            }
        }
    }
    
    @AfterEach
    public void tearDown() throws IOException {
        // Clean up test files recursively
        cleanupDirectory(testFile);
    }
    
    @Test
    public void testLoadFromNonExistentFile() throws HoneyException {
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }
    
    @Test
    public void testSaveAndLoadTodoTask() throws HoneyException {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo todo = new Todo("todo read book");
        todo.markAsDone();
        originalTasks.add(todo);
        
        storage.saveTasks(originalTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertTrue(loadedTasks.get(0).getIsDone());
        assertTrue(loadedTasks.get(0).toString().contains("read book"));
    }
    
    @Test
    public void testSaveAndLoadDeadlineTask() throws HoneyException {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Deadline deadline = new Deadline("deadline return book /by 2019-10-15");
        originalTasks.add(deadline);
        
        storage.saveTasks(originalTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Deadline);
        Deadline loadedDeadline = (Deadline) loadedTasks.get(0);
        assertEquals("return book", loadedDeadline.taskName);
    }
    
    @Test
    public void testSaveAndLoadDeadlineWithTime() throws HoneyException {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Deadline deadline = new Deadline("deadline submit assignment /by 2019-10-15 1800");
        originalTasks.add(deadline);
        
        storage.saveTasks(originalTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Deadline);
        Deadline loadedDeadline = (Deadline) loadedTasks.get(0);
        assertEquals("submit assignment", loadedDeadline.taskName);
        assertTrue(loadedDeadline.toString().contains("6:00PM"));
    }
    
    @Test
    public void testSaveAndLoadEventTask() throws HoneyException {
        ArrayList<Task> originalTasks = new ArrayList<>();
        Event event = new Event("event project meeting /from 2019-10-15 /to 2019-10-16");
        event.markAsDone();
        originalTasks.add(event);
        
        storage.saveTasks(originalTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Event);
        assertTrue(loadedTasks.get(0).getIsDone());
        Event loadedEvent = (Event) loadedTasks.get(0);
        assertEquals("project meeting", loadedEvent.taskName);
    }
    
    @Test
    public void testSaveAndLoadMultipleTasks() throws HoneyException {
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("todo read book"));
        originalTasks.add(new Deadline("deadline return book /by 2019-10-15"));
        originalTasks.add(new Event("event meeting /from 2019-10-15 /to 2019-10-16"));
        
        storage.saveTasks(originalTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertTrue(loadedTasks.get(1) instanceof Deadline);
        assertTrue(loadedTasks.get(2) instanceof Event);
    }
    
    @Test
    public void testSaveEmptyTaskList() throws HoneyException {
        ArrayList<Task> emptyTasks = new ArrayList<>();
        storage.saveTasks(emptyTasks);
        
        ArrayList<Task> loadedTasks = storage.load();
        assertTrue(loadedTasks.isEmpty());
    }
    
    @Test
    public void testLoadWithCorruptedData() throws IOException, HoneyException {
        // Create test file with corrupted data
        Files.createDirectories(testFile.getParent());
        Files.write(testFile, "invalid data\nT | 1 | valid todo\nmore invalid data".getBytes());
        
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
    }
    
    @Test
    public void testDirectoryCreation() throws HoneyException {
        Storage deepStorage = new Storage("test_data/deep/path/honey.txt");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("todo test"));
        
        deepStorage.saveTasks(tasks);
        assertTrue(Files.exists(Paths.get("test_data/deep/path")));
        
        // Clean up
        try {
            Files.delete(Paths.get("test_data/deep/path/honey.txt"));
            Files.delete(Paths.get("test_data/deep/path"));
            Files.delete(Paths.get("test_data/deep"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
}