package honey.task;

/**
 * Represents the different types of tasks in the Honey application.
 * Provides type safety and better structure for task categorization.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the TaskType corresponding to the given symbol.
     *
     * @param symbol The symbol to look up
     * @return The corresponding TaskType
     * @throws IllegalArgumentException if no matching TaskType is found
     */
    public static TaskType fromSymbol(String symbol) {
        for (TaskType type : values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown task type symbol: " + symbol);
    }
}
