package honey.exceptions;

/**
 * Exception thrown when an invalid date format is provided for task operations.
 */
public class InvalidDateFormatException extends HoneyException {
    /**
     * Constructs an InvalidDateFormatException with the specified task type and expected format.
     *
     * @param taskType The type of task with invalid date format.
     * @param expectedFormat The expected format for the date.
     */
    public InvalidDateFormatException(String taskType, String expectedFormat) {
        super("Oh darling! 📅 I'm having a tiny bit of trouble with the " + taskType + " date format!\n"
              + "Could you please use: " + expectedFormat + " 💝\n"
              + "Here's a sweet example: " + getExample(taskType));
    }

    /**
     * Returns an example command for the specified task type.
     *
     * @param taskType The type of task to get example for.
     * @return Example command string.
     */
    private static String getExample(String taskType) {
        if (taskType.equals("deadline")) {
            return "deadline submit report /by 2019-12-02 or deadline submit report /by 2/12/2019 1800";
        } else if (taskType.equals("event")) {
            return "event team meeting /from 2019-10-20 /to 2019-10-21";
        }
        return "";
    }
}
