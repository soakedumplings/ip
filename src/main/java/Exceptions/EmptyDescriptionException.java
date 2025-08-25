package Exceptions;

public class EmptyDescriptionException extends HoneyException {
    public EmptyDescriptionException(String taskType) {
        super("OOPS!!! The description of a " + taskType + " cannot be empty.\n" +
              "Please provide a description like: " + getExample(taskType));
    }
    
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