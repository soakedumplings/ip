public class Deadline extends Task {
    public String deadline;
    public String taskName;

    public Deadline(String description) throws EmptyDescriptionException, InvalidDateFormatException {
        super(description, TaskType.DEADLINE);
        
        if (description.trim().equals("deadline") || description.length() <= 9) {
            throw new EmptyDescriptionException("deadline");
        }
        
        if (!description.contains("/by ")) {
            throw new InvalidDateFormatException("deadline", "deadline [description] /by [date]");
        }
        
        String[] tokens = description.split(" /by ");
        if (tokens.length != 2 || tokens[1].trim().isEmpty()) {
            throw new InvalidDateFormatException("deadline", "deadline [description] /by [date]");
        }
        
        this.deadline = "(" + "by: " + tokens[1].trim() + ")";
        this.taskName = tokens[0].substring(9).trim();
        
        if (this.taskName.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + taskName + " " + deadline;
    }
}
