package honey.task;

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
    protected TaskType taskType;

    /**
     * Constructs a new task with the specified description and type.
     * The task is initially marked as not done.
     *
     * @param description Description of the task.
     * @param taskType Type of the task.
     */
    public Task(String description, TaskType taskType) {
        assert description != null : "Task description cannot be null";
        assert taskType != null : "Task type cannot be null";
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
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
    public String getType() {
        return this.taskType.getSymbol();
    }

    /**
     * Returns the TaskType enum of this task.
     *
     * @return TaskType enum of the task.
     */
    public TaskType getTaskType() {
        return this.taskType;
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
