public class Event extends Task {
    String duration;
    String taskName;

    public Event(String description) throws EmptyDescriptionException, InvalidDateFormatException {
        super(description);
        
        if (description.trim().equals("event") || description.length() <= 6) {
            throw new EmptyDescriptionException("event");
        }
        
        if (!description.contains("/from ") || !description.contains("/to ")) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        String[] firstSplit = description.split(" /from ");
        if (firstSplit.length != 2) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        String[] tokens = firstSplit[1].split(" /to ");
        if (tokens.length != 2 || tokens[0].trim().isEmpty() || tokens[1].trim().isEmpty()) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        this.duration = "(" + "from: " + tokens[0].trim() + " to: " + tokens[1].trim() + ")";
        this.taskName = firstSplit[0].substring(6).trim();
        
        if (this.taskName.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + taskName + " " + duration;
    }
}
