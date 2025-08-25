import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Storage {
    private static final Path DATA_DIR_PATH = Paths.get("data");
    private static final Path DATA_FILE_PATH = Paths.get("data", "honey.txt");
    
    public static void saveTasks(ArrayList<Task> tasks) {
        try {
            // Create directory if it doesn't exist
            Path dataDir = DATA_DIR_PATH;
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Write tasks to file
            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE_PATH)) {
                for (Task task : tasks) {
                    writer.write(taskToFileFormat(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println(" Warning: Could not save tasks to file. " + e.getMessage());
        }
    }
    
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Path dataFile = DATA_FILE_PATH;
        
        // Return empty list if file doesn't exist
        if (!Files.exists(dataFile)) {
            return tasks;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
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
    
    private static String taskToFileFormat(Task task) { // from user interface to saved file
        StringBuilder sb = new StringBuilder();
        sb.append(task.type.toString()).append(" | ");
        sb.append(task.isDone ? "1" : "0").append(" | ");
        
        if (task instanceof Todo) {
            sb.append(task.description.substring(5));
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            sb.append(deadline.taskName).append(" | ");
            sb.append(deadline.deadline.substring(5, deadline.deadline.length() - 1)); // Remove "(by: " and ")"
        } else if (task instanceof Event) {
            Event event = (Event) task;
            sb.append(event.taskName).append(" | ");
            sb.append(event.duration.substring(7, event.duration.length() - 1)); // Remove "(from: " and ")"
        }
        
        return sb.toString();
    }
    
    private static Task parseTaskFromFile(String line) throws HoneyException { // from saved file to user interface
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
            task = new Deadline("deadline " + parts[2].trim() + " /by " + parts[3].trim());
            break;

        case "E":
            if (parts.length != 4) {
                throw new RuntimeException("Invalid EVENT format");
            }
            // Parse "start to: end" format
            String[] timeParts = parts[3].trim().split(" to: ");
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