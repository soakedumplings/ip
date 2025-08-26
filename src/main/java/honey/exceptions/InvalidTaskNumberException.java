package honey.exceptions;

public class InvalidTaskNumberException extends HoneyException {
    public InvalidTaskNumberException(String operation, int maxTasks) {
        super("OOPS!!! I can't " + operation + " that task!\n" +
              "Please provide a valid task number between 1 and " + maxTasks + ".\n" +
              "Use 'list' to see all your tasks with their numbers.");
    }
}