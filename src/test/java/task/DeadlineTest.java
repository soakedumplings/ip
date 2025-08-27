package task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import honey.exceptions.EmptyDescriptionException;
import honey.exceptions.InvalidDateFormatException;

public class DeadlineTest {
    
    @Test
    public void testDeadlineCreation() throws Exception {
        Deadline deadline = new Deadline("deadline return book /by 2019-10-15");
        assertEquals("return book", deadline.taskName);
        assertEquals(TaskType.DEADLINE, deadline.getType());
        assertFalse(deadline.getIsDone());
        assertTrue(deadline.toString().contains("return book"));
        assertTrue(deadline.toString().contains("Oct 15 2019"));
    }
    
    @Test
    public void testDeadlineWithTime() throws Exception {
        Deadline deadline = new Deadline("deadline submit assignment /by 2019-10-15 1800");
        assertEquals("submit assignment", deadline.taskName);
        assertTrue(deadline.toString().contains("6:00PM"));
    }
    
    @Test
    public void testDeadlineWithTimeColonFormat() throws Exception {
        Deadline deadline = new Deadline("deadline meeting /by 2019-10-15 18:00");
        assertEquals("meeting", deadline.taskName);
        assertTrue(deadline.toString().contains("6:00PM"));
    }
    
    @Test
    public void testDeadlineDifferentDateFormats() throws Exception {
        Deadline deadline1 = new Deadline("deadline task1 /by 2/12/2019 1800");
        assertEquals("task1", deadline1.taskName);
        
        Deadline deadline2 = new Deadline("deadline task2 /by 2019-12-02");
        assertEquals("task2", deadline2.taskName);
    }
    
    @Test
    public void testDeadlineInvalidFormat() {
        assertThrows(InvalidDateFormatException.class, () -> {
            new Deadline("deadline return book");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            new Deadline("deadline return book /by");
        });
        
        assertThrows(InvalidDateFormatException.class, () -> {
            new Deadline("deadline return book /by invalid-date");
        });
    }
    
    @Test
    public void testDeadlineEmptyDescription() {
        assertThrows(EmptyDescriptionException.class, () -> {
            new Deadline("deadline");
        });
        
        assertThrows(EmptyDescriptionException.class, () -> {
            new Deadline("deadline  /by 2019-10-15");
        });
    }
    
    @Test
    public void testDeadlineMarkAsDone() throws Exception {
        Deadline deadline = new Deadline("deadline return book /by 2019-10-15");
        deadline.markAsDone();
        assertTrue(deadline.getIsDone());
        assertEquals("X", deadline.getStatusIcon());
        assertTrue(deadline.toString().contains("[X]"));
    }
}