package honey.storage;

import honey.exceptions.HoneyException;
import task.Task;
import task.Todo;
import task.Deadline;
import task.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;

/**
 * Handles loading and saving of tasks to persistent storage.
 * Manages file I/O operations for task data persistence.
 */
public class Storage {
    /** Path to the data file */
    private final Path dataFilePath;
    /** Path to the data directory */
    private final Path dataDirPath;
    
    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Storage(String filePath) {
        this.dataFilePath = Paths.get(filePath);
        this.dataDirPath = this.dataFilePath.getParent();
    }
    
    /**
     * Saves the list of tasks to the data file.
     * Creates the directory if it doesn't exist.
     *
     * @param tasks List of tasks to save.
     */
    public void saveTasks(ArrayList<Task> tasks) {
        try {
            // Create directory if it doesn't exist
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            // Write tasks to file
            try (BufferedWriter writer = Files.newBufferedWriter(dataFilePath)) {
                for (Task task : tasks) {
                    writer.write(taskToFileFormat(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println(" Warning: Could not save tasks to file. " + e.getMessage());
        }
    }
    
    /**
     * Loads tasks from the data file.
     * Returns an empty list if the file doesn't exist.
     *
     * @return List of tasks loaded from the file.
     * @throws HoneyException If there are issues reading the file.
     */
    public ArrayList<Task> load() throws HoneyException {
        ArrayList<Task> tasks = new ArrayList<>();
        
        // Return empty list if file doesn't exist
        if (!Files.exists(dataFilePath)) {
            return tasks;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(dataFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = parseTaskFromFile(line.trim());
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    System.out.println(" Warning: Skipping corrupted line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println(" Warning: Could not load tasks from file. " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * Converts a task object to file format string.
     * Transforms task data into a format suitable for file storage.
     *
     * @param task Task to convert to file format.
     * @return String representation of the task for file storage.
     */
    private String taskToFileFormat(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getType().toString()).append(" | ");
        sb.append(task.getIsDone() ? "1" : "0").append(" | ");
        
        if (task instanceof Todo) {
            sb.append(task.getDescription().substring(5));
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            sb.append(deadline.taskName).append(" | ");
            sb.append(deadline.deadline.toString()); // Store as ISO format: yyyy-MM-ddTHH:mm
        } else if (task instanceof Event) {
            Event event = (Event) task;
            sb.append(event.taskName).append(" | ");
            sb.append(event.startDate.toString()).append(" to ").append(event.endDate.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * Parses a task from a file format string.
     * Reconstructs task objects from stored file data.
     *
     * @param line File format string representing a task.
     * @return Task object parsed from the string.
     * @throws HoneyException If the file format is invalid.
     */
    private Task parseTaskFromFile(String line) throws HoneyException {
        if (line.isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new RuntimeException("Invalid format: insufficient parts");
        }
        
        String typeStr = parts[0].trim();
        boolean isDone = "1".equals(parts[1].trim());
        
        Task task = null;
        
        switch (typeStr) {
        case "T":
            if (parts.length != 3) {
                throw new RuntimeException("Invalid TODO format");
            }
            task = new Todo("todo " + parts[2].trim());
            break;
                
        case "D":
            if (parts.length != 4) {
                throw new RuntimeException("Invalid DEADLINE format");
            }
            // Parse stored ISO datetime format (yyyy-MM-ddTHH:mm or yyyy-MM-dd)
            String storedDateTime = parts[3].trim();
            if (storedDateTime.contains("T")) {
                // Has time component, convert back to our input format
                storedDateTime = storedDateTime.replace("T", " ").replace(":", "");
                // Remove seconds if present
                if (storedDateTime.length() > 15) {
                    storedDateTime = storedDateTime.substring(0, 15);
                }
            }
            task = new Deadline("deadline " + parts[2].trim() + " /by " + storedDateTime);
            break;

        case "E":
            if (parts.length != 4) {
                throw new RuntimeException("Invalid EVENT format");
            }
            // Parse "start to end" format (both in ISO format)
            String[] timeParts = parts[3].trim().split(" to ");
            if (timeParts.length != 2) {
                throw new RuntimeException("Invalid EVENT time format");
            }
            task = new Event("event " + parts[2].trim() + " /from " + timeParts[0].trim() + " /to " + timeParts[1].trim());
            break;

        default:
            throw new RuntimeException("Unknown task type: " + typeStr);
        }
        
        if (task != null && isDone) {
            task.markAsDone();
        }
        
        return task;
    }
}