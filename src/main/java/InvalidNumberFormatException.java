public class InvalidNumberFormatException extends HoneyException {
    public InvalidNumberFormatException(String operation, String providedInput) {
        super("OOPS!!! I need a valid number to " + operation + " a task!\n" +
              "You provided: '" + providedInput + "'\n" +
              "Please use: " + operation + " [number]\n" +
              "Example: " + operation + " 1");
    }
}