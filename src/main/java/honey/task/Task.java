package task;

/**
 * Represents a task in the Honey task management system.
 * A task has a description, completion status, and type.
 * This is the base class for all specific task types.
 */
public class Task {
    /** Description of the task */
    protected String description;
    /** Completion status of the task */
    protected boolean isDone;
    /** Type of the task */
    protected TaskType type;

    /**
     * Constructs a new task with the specified description and type.
     * The task is initially marked as not done.
     *
     * @param description Description of the task.
     * @param type Type of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the status icon for this task.
     * Returns "X" if the task is done, " " otherwise.
     *
     * @return Status icon representing completion status.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     * Sets the completion status to true.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     * Sets the completion status to false.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the type of this task.
     *
     * @return Type of the task.
     */
    public TaskType getType() {
        return this.type;
    }

    /**
     * Returns whether this task is done.
     *
     * @return True if task is completed, false otherwise.
     */
    public boolean getIsDone() {
        return this.isDone;
    }
}

