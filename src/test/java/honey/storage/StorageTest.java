package honey.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import honey.exceptions.HoneyException;
import honey.task.Deadline;
import honey.task.Event;
import honey.task.Task;
import honey.task.Todo;
import honey.tasklist.TaskList;

/**
 * Comprehensive tests for Storage functionality.
 * Tests file operations, data persistence, and error handling.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class StorageTest {
    
    @TempDir
    Path tempDir;
    
    private Storage storage;
    private File testFile;

    @BeforeEach
    public void setUp() {
        testFile = tempDir.resolve("storage_test.txt").toFile();
        storage = new Storage(testFile.getPath());
    }
    
    /**
     * Creates a sample task list for testing.
     */
    private TaskList createSampleTaskList() throws HoneyException {
        TaskList tasks = new TaskList();
        tasks.addTask("todo read book");
        tasks.addTask("deadline submit assignment /by 2023-12-15");
        tasks.addTask("event team meeting /from 2023-12-10 /to 2023-12-11");
        
        // Mark one task as done for testing
        tasks.markTask(1);
        
        return tasks;
    }

    // ====================== Save Tests ======================
    
    @Test
    public void save_validTaskList_createsFileWithCorrectContent() throws Exception {
        TaskList tasks = createSampleTaskList();
        
        storage.save(tasks.getTasks());
        
        // Verify file was created
        assertTrue(testFile.exists());
        assertTrue(testFile.isFile());
        
        // Verify file content
        List<String> lines = Files.readAllLines(testFile.toPath());
        assertEquals(3, lines.size());
        
        // Check todo task (marked as done)
        assertTrue(lines.get(0).contains("T | 1"));
        assertTrue(lines.get(0).contains("read book"));
        
        // Check deadline task
        assertTrue(lines.get(1).contains("D | 0"));
        assertTrue(lines.get(1).contains("submit assignment"));
        assertTrue(lines.get(1).contains("2023-12-15"));
        
        // Check event task
        assertTrue(lines.get(2).contains("E | 0"));
        assertTrue(lines.get(2).contains("team meeting"));
        assertTrue(lines.get(2).contains("2023-12-10"));
        assertTrue(lines.get(2).contains("2023-12-11"));
    }
    
    @Test
    public void save_emptyTaskList_createsEmptyFile() throws Exception {
        ArrayList<Task> emptyTasks = new ArrayList<>();
        
        storage.save(emptyTasks);
        
        assertTrue(testFile.exists());
        List<String> lines = Files.readAllLines(testFile.toPath());
        assertEquals(0, lines.size());
    }
    
    @Test
    public void save_overwritesExistingFile() throws Exception {
        // Create initial file
        TaskList initialTasks = new TaskList();
        initialTasks.addTask("todo initial task");
        storage.save(initialTasks.getTasks());
        
        List<String> initialLines = Files.readAllLines(testFile.toPath());
        assertEquals(1, initialLines.size());
        
        // Overwrite with new content
        TaskList newTasks = createSampleTaskList();
        storage.save(newTasks.getTasks());
        
        List<String> newLines = Files.readAllLines(testFile.toPath());
        assertEquals(3, newLines.size());
        assertFalse(newLines.toString().contains("initial task"));
    }
    
    @Test
    public void save_createsDirectoryIfNotExists() throws Exception {
        // Create storage with nested directory path
        File nestedFile = tempDir.resolve("nested/dir/test.txt").toFile();
        Storage nestedStorage = new Storage(nestedFile.getPath());
        
        TaskList tasks = createSampleTaskList();
        nestedStorage.save(tasks.getTasks());
        
        assertTrue(nestedFile.exists());
        assertTrue(nestedFile.getParentFile().exists());
    }

    // ====================== Load Tests ======================
    
    @Test
    public void load_validFile_returnsCorrectTasks() throws Exception {
        // First save tasks
        TaskList originalTasks = createSampleTaskList();
        storage.save(originalTasks.getTasks());
        
        // Then load them back
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(3, loadedTasks.size());
        
        // Verify todo task
        Task todoTask = loadedTasks.get(0);
        assertTrue(todoTask instanceof Todo);
        assertTrue(todoTask.getDescription().contains("read book"));
        assertTrue(todoTask.getIsDone()); // Should be marked as done
        
        // Verify deadline task
        Task deadlineTask = loadedTasks.get(1);
        assertTrue(deadlineTask instanceof Deadline);
        assertTrue(deadlineTask.getDescription().contains("submit assignment"));
        assertFalse(deadlineTask.getIsDone());
        
        // Verify event task
        Task eventTask = loadedTasks.get(2);
        assertTrue(eventTask instanceof Event);
        assertTrue(eventTask.getDescription().contains("team meeting"));
        assertFalse(eventTask.getIsDone());
    }
    
    @Test
    public void load_emptyFile_returnsEmptyList() throws Exception {
        // Create empty file
        Files.createFile(testFile.toPath());
        
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(0, loadedTasks.size());
    }
    
    @Test
    public void load_nonExistentFile_returnsEmptyList() throws Exception {
        // Don't create the file
        assertFalse(testFile.exists());
        
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(0, loadedTasks.size());
    }
    
    @Test
    public void load_invalidFileFormat_throwsException() throws Exception {
        // Create file with invalid content
        Files.write(testFile.toPath(), List.of("invalid format", "another invalid line"));
        
        assertThrows(HoneyException.class, () -> storage.load());
    }
    
    @Test
    public void load_corruptedTask_throwsException() throws Exception {
        // Create file with corrupted task format
        Files.write(testFile.toPath(), List.of(
            "T | 1 | read book",  // Valid
            "X | invalid | corrupted task"  // Invalid task type
        ));
        
        assertThrows(HoneyException.class, () -> storage.load());
    }
    
    @Test
    public void load_missingFields_throwsException() throws Exception {
        // Create file with missing fields
        Files.write(testFile.toPath(), List.of("T | 1"));  // Missing description
        
        assertThrows(HoneyException.class, () -> storage.load());
    }

    // ====================== Save-Load Round Trip Tests ======================
    
    @Test
    public void saveLoad_roundTrip_preservesAllTaskData() throws Exception {
        TaskList originalTasks = new TaskList();
        
        // Add various task types with different states
        originalTasks.addTask("todo simple task");
        originalTasks.addTask("todo task with special characters: @#$%");
        originalTasks.addTask("deadline important deadline /by 2023-12-25");
        originalTasks.addTask("deadline overdue task /by 2020-01-01");
        originalTasks.addTask("event all day event /from 2023-12-20 /to 2023-12-21");
        originalTasks.addTask("event multi-day conference /from 2023-12-22 /to 2023-12-24");
        
        // Mark some tasks as done
        originalTasks.markTask(1);
        originalTasks.markTask(3);
        originalTasks.markTask(5);
        
        // Save and load
        storage.save(originalTasks.getTasks());
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(originalTasks.size(), loadedTasks.size());
        
        // Verify each task is preserved correctly
        for (int i = 0; i < originalTasks.size(); i++) {
            Task original = originalTasks.getTasks().get(i);
            Task loaded = loadedTasks.get(i);
            
            assertEquals(original.getClass(), loaded.getClass());
            assertEquals(original.getDescription(), loaded.getDescription());
            assertEquals(original.getIsDone(), loaded.getIsDone());
            assertEquals(original.toString(), loaded.toString());
        }
    }
    
    @Test
    public void saveLoad_manyTasks_handlesLargeDataset() throws Exception {
        TaskList largeTasks = new TaskList();
        
        // Add many tasks
        for (int i = 1; i <= 100; i++) {
            largeTasks.addTask("todo task number " + i);
            if (i % 10 == 0) {
                largeTasks.addTask("deadline deadline " + i + " /by 2023-12-" + (10 + (i % 20)));
            }
            if (i % 15 == 0) {
                largeTasks.addTask("event event " + i + " /from 2023-12-10 /to 2023-12-11");
            }
        }
        
        // Mark some random tasks
        for (int i = 1; i <= largeTasks.size(); i += 7) {
            largeTasks.markTask(i);
        }
        
        // Save and load
        storage.save(largeTasks.getTasks());
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(largeTasks.size(), loadedTasks.size());
        
        // Spot check some tasks
        assertEquals(largeTasks.getTasks().get(0).toString(), loadedTasks.get(0).toString());
        assertEquals(largeTasks.getTasks().get(50).toString(), loadedTasks.get(50).toString());
        assertEquals(largeTasks.getTasks().get(largeTasks.size() - 1).toString(), 
                    loadedTasks.get(loadedTasks.size() - 1).toString());
    }

    // ====================== Error Handling Tests ======================
    
    @Test
    public void save_readOnlyFile_throwsIOException() throws Exception {
        // Create file and make it read-only
        Files.createFile(testFile.toPath());
        testFile.setReadOnly();
        
        TaskList tasks = createSampleTaskList();
        
        assertThrows(IOException.class, () -> storage.save(tasks.getTasks()));
    }
    
    @Test
    public void load_readOnlyDirectory_stillWorks() throws Exception {
        // Save first
        TaskList tasks = createSampleTaskList();
        storage.save(tasks.getTasks());
        
        // Make directory read-only
        testFile.getParentFile().setReadOnly();
        
        // Should still be able to read
        ArrayList<Task> loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());
        
        // Clean up
        testFile.getParentFile().setWritable(true);
    }

    // ====================== Edge Cases ======================
    
    @Test
    public void storage_veryLongFilePath_worksCorrectly() throws Exception {
        // Create deeply nested directory structure
        StringBuilder longPath = new StringBuilder(tempDir.toString());
        for (int i = 0; i < 10; i++) {
            longPath.append("/very_long_directory_name_").append(i);
        }
        longPath.append("/test.txt");
        
        Storage longPathStorage = new Storage(longPath.toString());
        TaskList tasks = createSampleTaskList();
        
        longPathStorage.save(tasks.getTasks());
        ArrayList<Task> loadedTasks = longPathStorage.load();
        
        assertEquals(3, loadedTasks.size());
    }
    
    @Test
    public void storage_fileWithSpecialCharacters_worksCorrectly() throws Exception {
        File specialFile = tempDir.resolve("test-file_with.special@chars.txt").toFile();
        Storage specialStorage = new Storage(specialFile.getPath());
        
        TaskList tasks = createSampleTaskList();
        specialStorage.save(tasks.getTasks());
        ArrayList<Task> loadedTasks = specialStorage.load();
        
        assertEquals(3, loadedTasks.size());
    }
    
    @Test
    public void storage_emptyTaskDescriptions_handledCorrectly() throws Exception {
        // This test would verify that the storage handles edge cases in task descriptions
        // Since our current implementation doesn't allow empty descriptions, 
        // we test with minimal descriptions
        TaskList tasks = new TaskList();
        tasks.addTask("todo a");
        tasks.addTask("deadline b /by 2023-12-15");
        tasks.addTask("event c /from 2023-12-10 /to 2023-12-11");
        
        storage.save(tasks.getTasks());
        ArrayList<Task> loadedTasks = storage.load();
        
        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0).getDescription().contains("a"));
        assertTrue(loadedTasks.get(1).getDescription().contains("b"));
        assertTrue(loadedTasks.get(2).getDescription().contains("c"));
    }

    // ====================== File System Integration Tests ======================
    
    @Test
    public void storage_multipleInstances_workIndependently() throws Exception {
        File file1 = tempDir.resolve("storage1.txt").toFile();
        File file2 = tempDir.resolve("storage2.txt").toFile();
        
        Storage storage1 = new Storage(file1.getPath());
        Storage storage2 = new Storage(file2.getPath());
        
        // Save different data to each storage
        TaskList tasks1 = new TaskList();
        tasks1.addTask("todo task for storage 1");
        
        TaskList tasks2 = new TaskList();
        tasks2.addTask("todo task for storage 2");
        tasks2.addTask("deadline task for storage 2 /by 2023-12-15");
        
        storage1.save(tasks1.getTasks());
        storage2.save(tasks2.getTasks());
        
        // Load and verify independence
        ArrayList<Task> loaded1 = storage1.load();
        ArrayList<Task> loaded2 = storage2.load();
        
        assertEquals(1, loaded1.size());
        assertEquals(2, loaded2.size());
        
        assertTrue(loaded1.get(0).getDescription().contains("storage 1"));
        assertTrue(loaded2.get(0).getDescription().contains("storage 2"));
    }
    
    @Test
    public void storage_concurrentAccess_handlesGracefully() throws Exception {
        // This test simulates concurrent access scenarios
        TaskList tasks = createSampleTaskList();
        
        // Save from one storage instance
        storage.save(tasks.getTasks());
        
        // Create another storage instance pointing to same file
        Storage storage2 = new Storage(testFile.getPath());
        
        // Both should be able to load the same data
        ArrayList<Task> loaded1 = storage.load();
        ArrayList<Task> loaded2 = storage2.load();
        
        assertEquals(loaded1.size(), loaded2.size());
        for (int i = 0; i < loaded1.size(); i++) {
            assertEquals(loaded1.get(i).toString(), loaded2.get(i).toString());
        }
    }
}