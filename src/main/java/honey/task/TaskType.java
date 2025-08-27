package honey.task;

/**
 * Represents the different types of tasks supported by the Honey application.
 * Each task type has an associated symbol for display purposes.
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
    
    @Override
    public String toString() {
        return symbol;
    }
}