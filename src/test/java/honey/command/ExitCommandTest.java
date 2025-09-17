package honey.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for ExitCommand functionality.
 * Tests exit behavior and message handling.
 * Follows detailed testing patterns from AddressBook Level 2.
 */
public class ExitCommandTest {

    // ====================== Basic Exit Tests ======================
    
    @Test
    public void execute_returnsCorrectExitMessage() {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        assertEquals("Sweet dreams, my dear Bee! 🌙 Until we meet again, keep being amazing! 💕", 
                    result.getFeedbackToUser());
        assertTrue(result.isExit());
    }
    
    @Test
    public void execute_alwaysReturnsExit() {
        ExitCommand command = new ExitCommand();
        
        // Execute multiple times to ensure consistency
        CommandResult result1 = command.execute();
        CommandResult result2 = command.execute();
        CommandResult result3 = command.execute();
        
        assertTrue(result1.isExit());
        assertTrue(result2.isExit());
        assertTrue(result3.isExit());
    }
    
    @Test
    public void execute_messageConsistency() {
        ExitCommand command = new ExitCommand();
        
        // Execute multiple times to ensure message consistency
        CommandResult result1 = command.execute();
        CommandResult result2 = command.execute();
        
        assertEquals(result1.getFeedbackToUser(), result2.getFeedbackToUser());
    }

    // ====================== Independence Tests ======================
    
    @Test
    public void execute_doesNotRequireData() {
        // ExitCommand should work without setData being called
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        assertTrue(result.isExit());
        assertTrue(result.getFeedbackToUser().contains("Sweet dreams"));
    }
    
    @Test
    public void execute_multipleInstances_independent() {
        ExitCommand command1 = new ExitCommand();
        ExitCommand command2 = new ExitCommand();
        
        CommandResult result1 = command1.execute();
        CommandResult result2 = command2.execute();
        
        // Both should behave identically
        assertEquals(result1.getFeedbackToUser(), result2.getFeedbackToUser());
        assertEquals(result1.isExit(), result2.isExit());
    }

    // ====================== Message Content Tests ======================
    
    @Test
    public void execute_containsExpectedElements() {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        String message = result.getFeedbackToUser();
        
        // Check for expected emotional elements
        assertTrue(message.contains("Sweet dreams"));
        assertTrue(message.contains("my dear Bee"));
        assertTrue(message.contains("amazing"));
        
        // Check for emoji presence
        assertTrue(message.contains("🌙"));
        assertTrue(message.contains("💕"));
    }
    
    @Test
    public void execute_messageNotEmpty() {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        String message = result.getFeedbackToUser();
        assertTrue(message != null && !message.trim().isEmpty());
        assertTrue(message.length() > 10); // Should be a meaningful message
    }

    // ====================== Behavioral Tests ======================
    
    @Test
    public void execute_isStateless() {
        ExitCommand command = new ExitCommand();
        
        // First execution
        CommandResult result1 = command.execute();
        
        // Second execution should be identical (stateless)
        CommandResult result2 = command.execute();
        
        assertEquals(result1.getFeedbackToUser(), result2.getFeedbackToUser());
        assertEquals(result1.isExit(), result2.isExit());
    }
    
    @Test
    public void constructor_createsValidCommand() {
        ExitCommand command = new ExitCommand();
        
        // Should be able to create command without issues
        assertTrue(command instanceof ExitCommand);
        assertTrue(command instanceof Command);
    }

    // ====================== Integration Context Tests ======================
    
    @Test
    public void execute_withNullData_stillWorks() {
        ExitCommand command = new ExitCommand();
        command.setData(null, null);
        
        CommandResult result = command.execute();
        
        assertTrue(result.isExit());
        assertTrue(result.getFeedbackToUser().contains("Sweet dreams"));
    }
    
    @Test
    public void execute_messageFollowsPersonality() {
        ExitCommand command = new ExitCommand();
        CommandResult result = command.execute();
        
        String message = result.getFeedbackToUser();
        
        // Should follow Honey's caring personality
        assertTrue(message.toLowerCase().contains("dear") || 
                  message.toLowerCase().contains("sweet") ||
                  message.toLowerCase().contains("amazing"));
        
        // Should be positive and encouraging
        assertFalse(message.toLowerCase().contains("error"));
        assertFalse(message.toLowerCase().contains("fail"));
        assertFalse(message.toLowerCase().contains("wrong"));
    }
}