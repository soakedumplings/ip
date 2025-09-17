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
        super("Oh sweetie! 💛 I'm having trouble finding that task to " + operation + "!\n"
              + "Could you please choose a number between 1 and " + maxTasks + "? \n"
              + "Try using 'list' to see all our lovely tasks with their numbers! ✨");
    }
}
