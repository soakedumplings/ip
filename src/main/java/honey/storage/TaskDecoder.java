package honey.storage;

import honey.exceptions.HoneyException;
import honey.task.Deadline;
import honey.task.Event;
import honey.task.Task;
import honey.task.Todo;

/**
 * Handles decoding string data from files back into Task objects.
 * Reconstructs task objects from stored file data.
 */
public class TaskDecoder {

    /**
     * Parses a task from a file format string.
     * Reconstructs task objects from stored file data.
     *
     * @param line File format string representing a task.
     * @return Task object parsed from the string.
     * @throws HoneyException If the file format is invalid.
     */
    public Task decode(String line) throws HoneyException {
        assert line != null : "Input line cannot be null";
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
            task = new Event("event " + parts[2].trim() + " /from " + timeParts[0].trim()
                    + " /to " + timeParts[1].trim());
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
