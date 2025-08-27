package task;

import honey.exceptions.EmptyDescriptionException;
import honey.exceptions.InvalidDateFormatException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an event task with start and end dates.
 * An event task has a description, start date, and end date.
 */
public class Event extends Task {
    /** Start date of the event */
    public LocalDate startDate;
    /** End date of the event */
    public LocalDate endDate;
    /** Name of the event task */
    public String taskName;
    
    // User-friendly output format
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Constructs a new event task with the specified description.
     * Parses the start and end dates from the description.
     *
     * @param description Full command string including "event" prefix and "/from" and "/to" clauses.
     * @throws EmptyDescriptionException If the event description is empty.
     * @throws InvalidDateFormatException If the date format is invalid or start date is after end date.
     */
    public Event(String description) throws EmptyDescriptionException, InvalidDateFormatException {
        super(description, TaskType.EVENT);
        
        if (description.trim().equals("event") || description.length() <= 6) {
            throw new EmptyDescriptionException("event");
        }
        
        if (!description.contains("/from ") || !description.contains("/to ")) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        String[] firstSplit = description.split(" /from ");
        if (firstSplit.length != 2) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        String[] tokens = firstSplit[1].split(" /to ");
        if (tokens.length != 2 || tokens[0].trim().isEmpty() || tokens[1].trim().isEmpty()) {
            throw new InvalidDateFormatException("event", "event [description] /from [start] /to [end]");
        }
        
        this.taskName = firstSplit[0].substring(6).trim();
        if (this.taskName.isEmpty()) {
            throw new EmptyDescriptionException("event");
        }
        
        // Parse dates using LocalDate - clean and simple like the example
        try {
            this.startDate = LocalDate.parse(tokens[0].trim());
            this.endDate = LocalDate.parse(tokens[1].trim());
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("event",
                "Please use date format: yyyy-MM-dd (e.g., 2019-12-02)");
        }
        
        // Validate date order
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateFormatException("event", "Start date cannot be after end date");
        }
    }

    /**
     * Returns the string representation of this event task.
     * Format: [E][status] taskName (from: startDate to: endDate) or (on: date) for single-day events.
     *
     * @return String representation of the event task.
     */
    @Override
    public String toString() {
        // Format dates in user-friendly way: "MMM dd yyyy"
        String formattedStart = startDate.format(OUTPUT_FORMAT);
        String formattedEnd = endDate.format(OUTPUT_FORMAT);
        
        if (startDate.equals(endDate)) {
            return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + taskName + " (on: " + formattedStart + ")";
        } else {
            return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + taskName + " (from: " + formattedStart + " to: " + formattedEnd + ")";
        }
    }
}
