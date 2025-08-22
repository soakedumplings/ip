public class Event extends Task {
    String duration;
    String taskName;

    public Event(String description) {
        super(description);
        String[] firstSplit = description.split(" \\\\from ");
        String[] tokens = firstSplit[1].split(" \\\\to ");
        this.duration = "(" + "from: " + tokens[0] + " to: " + tokens[1] + ")";
        this.taskName = firstSplit[0].substring(6);
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + taskName + " " + duration;
    }
}
