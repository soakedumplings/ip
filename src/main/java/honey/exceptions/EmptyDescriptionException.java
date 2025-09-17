package honey.exceptions;

/**
 * Exception thrown when a task is created with an empty description.
 * Provides helpful examples for different task types.
 */
public class EmptyDescriptionException extends HoneyException {
    /**
     * Constructs an EmptyDescriptionException for the specified task type.
     *
     * @param taskType Type of task that has empty description.
     */
    public EmptyDescriptionException(String taskType) {
        super("Oh my dear! 🌸 I need a little more detail for this " + taskType + " to help you properly!\n"
              + "Could you try something sweet like: " + getExample(taskType) + " 💕");
    }

    /**
     * Returns an example command for the specified task type.
     *
     * @param taskType Type of task to get example for.
     * @return Example command string.
     */
    private static String getExample(String taskType) {
        switch (taskType.toLowerCase()) {
        case "todo":
            return "todo read book";
        case "deadline":
            return "deadline submit report /by 2023-10-15";
        case "event":
            return "event team meeting /from 2023-10-20 /to 2023-10-21";
        default:
            return taskType + " [description]";
        }
    }
}
