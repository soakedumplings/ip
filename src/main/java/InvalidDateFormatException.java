public class InvalidDateFormatException extends HoneyException {
    public InvalidDateFormatException(String taskType, String expectedFormat) {
        super("OOPS!!! The " + taskType + " format is incorrect!\n" +
              "Please use: " + expectedFormat + "\n" +
              "Example: " + getExample(taskType));
    }
    
    private static String getExample(String taskType) {
        if (taskType.equals("deadline")) {
            return "deadline submit report /by 2023-10-15";
        } else if (taskType.equals("event")) {
            return "event team meeting /from 2023-10-20 /to 2023-10-21";
        }
        return "";
    }
}