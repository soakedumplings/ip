package honey.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import honey.exceptions.EmptyDescriptionException;
import honey.exceptions.InvalidDateFormatException;

/**
 * Represents a deadline task with a due date and optional time.
 * A deadline task has a description and a deadline by which it should be completed.
 */
public class Deadline extends Task {
    // Input formats we'll try
    private static final DateTimeFormatter[] INPUT_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"), // 2019-12-02 1800
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"), // 2/12/2019 1800
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), // 2019-12-02 18:00
        DateTimeFormatter.ofPattern("yyyy-MM-dd") // 2019-12-02 (date only)
    };

    // User-friendly output formats
    private static final DateTimeFormatter DATE_OUTPUT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATETIME_OUTPUT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /** Deadline date and time for this task */
    private LocalDateTime deadline;
    /** Name of the deadline task */
    private String taskName;

    /**
     * Constructs a new deadline task with the specified description.
     * Parses the deadline date/time from the description.
     *
     * @param description Full command string including "deadline" prefix and "/by" clause.
     * @throws EmptyDescriptionException If the deadline description is empty.
     * @throws InvalidDateFormatException If the date format is invalid.
     */
    public Deadline(String description) throws EmptyDescriptionException, InvalidDateFormatException {
        super(description, TaskType.DEADLINE);

        if (description.trim().equals("deadline") || description.length() <= 9) {
            throw new EmptyDescriptionException("deadline");
        }

        if (!description.contains("/by ")) {
            throw new InvalidDateFormatException("deadline", "deadline [description] /by [date]");
        }

        String[] tokens = description.split(" /by ");
        if (tokens.length != 2 || tokens[1].trim().isEmpty()) {
            throw new InvalidDateFormatException("deadline", "deadline [description] /by [date]");
        }

        this.taskName = tokens[0].substring(9).trim();
        if (this.taskName.isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        // Parse date/time using multiple formats
        String dateTimeInput = tokens[1].trim();
        this.deadline = null;

        for (DateTimeFormatter formatter : INPUT_FORMATS) {
            try {
                if (dateTimeInput.matches(".*\\d{4}$") || dateTimeInput.matches(".*\\d{2}:\\d{2}$")) {
                    // Contains time (either HHmm or HH:mm format)
                    this.deadline = LocalDateTime.parse(dateTimeInput, formatter);
                } else {
                    // Date-only format, set time to start of day
                    this.deadline = LocalDate.parse(dateTimeInput, formatter).atStartOfDay();
                }
                break;
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        if (this.deadline == null) {
            throw new InvalidDateFormatException("deadline",
                "Please use date formats: yyyy-MM-dd, yyyy-MM-dd HHmm, d/M/yyyy HHmm, or yyyy-MM-dd HH:mm");
        }
    }

    /**
     * Returns the string representation of this deadline task.
     * Format: [D][status] taskName (by: formattedDateTime)
     *
     * @return String representation of the deadline task.
     */
    @Override
    public String toString() {
        // Check if time component is present (not midnight)
        if (deadline.getHour() == 0 && deadline.getMinute() == 0) {
            // Date only - format as "MMM dd yyyy"
            String formattedDate = deadline.format(DATE_OUTPUT);
            return "[" + getType() + "][" + getStatusIcon() + "] " + taskName + " (by: " + formattedDate + ")";
        } else {
            // Date and time - format as "MMM dd yyyy, h:mma"
            String formattedDateTime = deadline.format(DATETIME_OUTPUT);
            return "[" + getType() + "][" + getStatusIcon() + "] " + taskName + " (by: " + formattedDateTime + ")";
        }
    }

    /**
     * Gets the deadline date and time of this deadline task.
     *
     * @return The deadline date and time.
     */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    /**
     * Gets the task name of this deadline task.
     *
     * @return The task name.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Checks if this deadline task is overdue (deadline has passed).
     *
     * @return true if the deadline has passed the current date/time, false otherwise.
     */
    public boolean isOverdue() {
        return deadline.isBefore(LocalDateTime.now());
    }
}

