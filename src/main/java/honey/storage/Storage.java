package honey.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import honey.exceptions.HoneyException;
import honey.task.Task;

/**
 * Handles loading and saving of tasks to persistent storage.
 * Manages file I/O operations for task data persistence.
 * Uses TaskEncoder and TaskDecoder for serialization/deserialization.
 */
public class Storage {
    /** Path to the data file */
    private final Path dataFilePath;
    /** Path to the data directory */
    private final Path dataDirPath;
    /** Encoder for converting tasks to string format */
    private final TaskEncoder encoder;
    /** Decoder for converting strings back to tasks */
    private final TaskDecoder decoder;

    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Storage(String filePath) {
        this.dataFilePath = Paths.get(filePath);
        this.dataDirPath = this.dataFilePath.getParent();
        this.encoder = new TaskEncoder();
        this.decoder = new TaskDecoder();
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
                    writer.write(encoder.encode(task));
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
                    Task task = decoder.decode(line.trim());
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

}
