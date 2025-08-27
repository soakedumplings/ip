package task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import honey.exceptions.EmptyDescriptionException;
import honey.exceptions.InvalidDateFormatException;

public class EventTest {
    
    @Test
    public void testEventCreation() throws Exception {
        Event event = new Event("event project meeting /from 2019-10-15 /to 2019-10-16");
        assertEquals("project meeting", event.taskName);
        assertEquals(TaskType.EVENT, event.getType());
        assertFalse(event.getIsDone());
        assertTrue(event.toString().contains("project meeting"));
        assertTrue(event.toString().contains("Oct 15 2019"));
        assertTrue(event.toString().contains("Oct 16 2019"));
    }
    
    @Test
    public void testEventSameDay() throws Exception {
        Event event = new Event("event workshop /from 2019-10-15 /to 2019-10-15");
        assertTrue(event.toString().contains("(on: Oct 15 2019)"));
        assertFalse(event.toString().contains("from:"));
        assertFalse(event.toString().contains("to:"));
    }
    
    @Test
    public void testEventMultipleDays() throws Exception {
        Event event = new Event("event conference /from 2019-10-15 /to 2019-10-17");
        assertTrue(event.toString().contains("(from: Oct 15 2019 to: Oct 17 2019)"));
    }
    
    @Test
    public void testEventInvalidFormat() {
        assertThrows(InvalidDateFormatException.class, () -> {
            new Event("event project meeting");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            new Event("event project meeting /from 2019-10-15");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            new Event("event project meeting /from 2019-10-15 /to");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            new Event("event project meeting /from 2019-10-15 /to invalid-date");
        });
    }
    
    @Test
    public void testEventInvalidDateOrder() {
        assertThrows(InvalidDateFormatException.class, () -> {
            new Event("event project meeting /from 2019-10-16 /to 2019-10-15");
        });
    }
    
    @Test
    public void testEventEmptyDescription() {
        assertThrows(EmptyDescriptionException.class, () -> {
            new Event("event");
        });
        
        assertThrows(EmptyDescriptionException.class, () -> {
            new Event("event  /from 2019-10-15 /to 2019-10-16");
        });
    }
    
    @Test
    public void testEventMarkAsDone() throws Exception {
        Event event = new Event("event conference /from 2019-10-15 /to 2019-10-17");
        event.markAsDone();
        assertTrue(event.getIsDone());
        assertEquals("X", event.getStatusIcon());
        assertTrue(event.toString().contains("[X]"));
    }
}