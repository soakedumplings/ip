package honey.command;

/**
 * Represents the result of executing a command.
 * Encapsulates the feedback message and any relevant tasks.
 */
public class CommandResult {
    private final String feedbackToUser;
    private final boolean isExit;

    /**
     * Constructs a CommandResult with only feedback to user.
     *
     * @param feedbackToUser The feedback message for the user
     */
    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
        this.isExit = false;
    }


    /**
     * Constructs a CommandResult with exit flag.
     *
     * @param feedbackToUser The feedback message for the user
     * @param isExit Whether this command result indicates application exit
     */
    public CommandResult(String feedbackToUser, boolean isExit) {
        this.feedbackToUser = feedbackToUser;
        this.isExit = isExit;
    }

    /**
     * Gets the feedback message for the user.
     *
     * @return The feedback message
     */
    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    /**
     * Returns whether this command result indicates application exit.
     *
     * @return true if the application should exit, false otherwise
     */
    public boolean isExit() {
        return isExit;
    }
}
