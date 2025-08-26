package task;

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