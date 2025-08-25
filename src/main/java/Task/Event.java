package Task;

import Exceptions.EmptyDescriptionException;
import Exceptions.InvalidDateFormatException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task {
    public LocalDate startDate;
    public LocalDate endDate;
    public String taskName;
    
    // User-friendly output format
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

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
