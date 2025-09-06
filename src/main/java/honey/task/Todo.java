package honey.task;

import honey.exceptions.EmptyDescriptionException;

/**
 * Represents a simple todo task.
 * A todo task only has a description and completion status.
 */
public class Todo extends Task {
    /**
     * Constructs a new todo task with the specified description.
     * Validates that the description is not empty.
     *
     * @param description Full command string including "todo" prefix.
     * @throws EmptyDescriptionException If the todo description is empty.
     */
    public Todo(String description) throws EmptyDescriptionException {
        super(description, "T");
        if (description.trim().equals("todo") || description.length() <= 5) {
            throw new EmptyDescriptionException("todo");
        }
    }

    /**
     * Returns the string representation of this todo task.
     * Format: [T][status] description
     *
     * @return String representation of the todo task.
     */
    @Override
    public String toString() {
        return "[" + type + "][" + getStatusIcon() + "] " + description.substring(5);
    }
}

