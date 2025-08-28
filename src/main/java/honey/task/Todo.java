package honey.task;

import honey.exceptions.EmptyDescriptionException;

public class Todo extends Task {
    public Todo(String description) throws EmptyDescriptionException {
        super(description, "T");
        if (description.trim().equals("todo") || description.length() <= 5) {
            throw new EmptyDescriptionException("todo");
        }
    }

    @Override
    public String toString() {
        return "[" + type + "][" + getStatusIcon() + "] " + description.substring(5);
    }
}
