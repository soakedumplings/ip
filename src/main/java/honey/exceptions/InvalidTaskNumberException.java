package honey.exceptions;

/**
 * Exception thrown when an invalid task number is provided.
 */
public class InvalidTaskNumberException extends HoneyException {
    /**
     * Constructs an InvalidTaskNumberException with the specified operation and maximum tasks.
     *
     * @param operation The operation that failed due to invalid task number.
     * @param maxTasks The maximum number of tasks available.
     */
    public InvalidTaskNumberException(String operation, int maxTasks) {
        super("OOPS!!! I can't " + operation + " that task!\n"
              + "Please provide a valid task number between 1 and " + maxTasks + ".\n"
              + "Use 'list' to see all your tasks with their numbers.");
    }
}
