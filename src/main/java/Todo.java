public class Todo extends Task {
    public Todo(String description) throws EmptyDescriptionException {
        super(description);
        if (description.trim().equals("todo") || description.length() <= 5) {
            throw new EmptyDescriptionException("todo");
        }
    }

    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description.substring(5);
    }
}
