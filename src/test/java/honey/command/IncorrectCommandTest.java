package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for IncorrectCommand functionality.
 * Tests error message handling for invalid commands.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class IncorrectCommandTest {

    // ====================== Basic Incorrect Command Tests ======================
    
    @Test
    public void execute_returnsErrorMessage() {
        String errorMessage = "Unknown command: invalid";
        IncorrectCommand command = new IncorrectCommand(errorMessage);
        CommandResult result = command.execute();
        
        assertEquals(errorMessage, result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_preservesOriginalMessage() {
        String originalMessage = "Custom error message";
        IncorrectCommand command = new IncorrectCommand(originalMessage);
        CommandResult result = command.execute();
        
        assertEquals(originalMessage, result.getFeedbackToUser());
    }
    
    @Test
    public void execute_emptyMessage_returnsEmptyString() {
        IncorrectCommand command = new IncorrectCommand("");
        CommandResult result = command.execute();
        
        assertEquals("", result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_nullMessage_returnsNull() {
        IncorrectCommand command = new IncorrectCommand(null);
        CommandResult result = command.execute();
        
        assertEquals(null, result.getFeedbackToUser());
        assertFalse(result.isExit());
    }

    // ====================== Message Preservation Tests ======================
    
    @Test
    public void execute_longMessage_preservesCompletely() {
        String longMessage = "This is a very long error message that should be preserved completely " +
                           "without any truncation or modification regardless of its length and content.";
        IncorrectCommand command = new IncorrectCommand(longMessage);
        CommandResult result = command.execute();
        
        assertEquals(longMessage, result.getFeedbackToUser());
    }
    
    @Test
    public void execute_specialCharacters_preservesExactly() {
        String specialMessage = "Error: Invalid command @#$%^&*()[]{}|\\:;\"'<>,.?/~`";
        IncorrectCommand command = new IncorrectCommand(specialMessage);
        CommandResult result = command.execute();
        
        assertEquals(specialMessage, result.getFeedbackToUser());
    }
    
    @Test
    public void execute_multilineMessage_preservesFormatting() {
        String multilineMessage = "First line\nSecond line\nThird line";
        IncorrectCommand command = new IncorrectCommand(multilineMessage);
        CommandResult result = command.execute();
        
        assertEquals(multilineMessage, result.getFeedbackToUser());
    }

    // ====================== Behavioral Tests ======================
    
    @Test
    public void execute_neverReturnsExit() {
        // Test various messages to ensure exit is always false
        String[] messages = {
            "Unknown command",
            "bye",
            "exit", 
            "quit",
            "Error message"
        };
        
        for (String message : messages) {
            IncorrectCommand command = new IncorrectCommand(message);
            CommandResult result = command.execute();
            assertFalse(result.isExit(), "Should never return exit=true for message: " + message);
        }
    }
    
    @Test
    public void execute_doesNotRequireData() {
        // IncorrectCommand should work without setData being called
        IncorrectCommand command = new IncorrectCommand("Test message");
        CommandResult result = command.execute();
        
        assertEquals("Test message", result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
    
    @Test
    public void execute_multipleInvocations_consistent() {
        String message = "Consistent error message";
        IncorrectCommand command = new IncorrectCommand(message);
        
        CommandResult result1 = command.execute();
        CommandResult result2 = command.execute();
        CommandResult result3 = command.execute();
        
        assertEquals(result1.getFeedbackToUser(), result2.getFeedbackToUser());
        assertEquals(result2.getFeedbackToUser(), result3.getFeedbackToUser());
        assertEquals(result1.isExit(), result2.isExit());
        assertEquals(result2.isExit(), result3.isExit());
    }

    // ====================== Independence Tests ======================
    
    @Test
    public void constructor_differentInstances_independent() {
        IncorrectCommand command1 = new IncorrectCommand("Message 1");
        IncorrectCommand command2 = new IncorrectCommand("Message 2");
        
        CommandResult result1 = command1.execute();
        CommandResult result2 = command2.execute();
        
        assertEquals("Message 1", result1.getFeedbackToUser());
        assertEquals("Message 2", result2.getFeedbackToUser());
    }
    
    @Test
    public void execute_withNullData_stillWorks() {
        IncorrectCommand command = new IncorrectCommand("Test with null data");
        command.setData(null, null);
        
        CommandResult result = command.execute();
        
        assertEquals("Test with null data", result.getFeedbackToUser());
        assertFalse(result.isExit());
    }
}