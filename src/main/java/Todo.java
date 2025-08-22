public class Todo extends Task {
    public Todo(String description) throws EmptyDescriptionException {
        super(description, TaskType.TODO);
        if (description.trim().equals("todo") || description.length() <= 5) {
            throw new EmptyDescriptionException("todo");
        }
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description.substring(5);
    }
}
