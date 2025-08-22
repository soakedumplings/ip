public class Deadline extends Task {
    String deadline;
    String taskName;

    public Deadline(String description) {
        super(description);
        String[] tokens = description.split(" \\\\by ");
        this.deadline = "(" + "by: " + tokens[1].substring(0) + ")";
        this.taskName = tokens[0].substring(9);
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + taskName + " " + deadline;
    }
}
