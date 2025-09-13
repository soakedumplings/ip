package honey.storage;

import honey.task.Deadline;
import honey.task.Event;
import honey.task.Task;
import honey.task.Todo;

/**
 * Handles encoding Task objects into string format for file storage.
 * Converts task objects into a standardized file format.
 */
public class TaskEncoder {

    /**
     * Converts a task object to file format string.
     * Transforms task data into a format suitable for file storage.
     *
     * @param task Task to convert to file format.
     * @return String representation of the task for file storage.
     */
    public String encode(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getType()).append(" | ");
        sb.append(task.getIsDone() ? "1" : "0").append(" | ");

        if (task instanceof Todo) {
            sb.append(task.getDescription().substring(5));
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            sb.append(deadline.getTaskName()).append(" | ");
            sb.append(deadline.getDeadline().toString()); // Store as ISO format: yyyy-MM-ddTHH:mm
        } else if (task instanceof Event) {
            Event event = (Event) task;
            sb.append(event.getTaskName()).append(" | ");
            sb.append(event.getStartDate().toString()).append(" to ").append(event.getEndDate().toString());
        }

        return sb.toString();
    }
}
