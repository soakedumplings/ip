package honey.task;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import honey.exceptions.EmptyDescriptionException;

public class TodoTest {
    
    @Test
    public void testTodoCreation() throws EmptyDescriptionException {
        Todo todo = new Todo("todo read book");
        assertEquals("read book", todo.getDescription().substring(5));
        assertEquals("T", todo.getType());
        assertFalse(todo.getIsDone());
        assertEquals(" ", todo.getStatusIcon());
    }
    
    @Test
    public void testTodoEmptyDescription() {
        assertThrows(EmptyDescriptionException.class, () -> {
            new Todo("todo");
        });
        
        assertThrows(EmptyDescriptionException.class, () -> {
            new Todo("todo ");
        });
    }
    
    @Test
    public void testTaskMarkAsDone() throws EmptyDescriptionException {
        Todo todo = new Todo("todo read book");
        todo.markAsDone();
        assertTrue(todo.getIsDone());
        assertEquals("X", todo.getStatusIcon());
    }
    
    @Test
    public void testTaskMarkAsNotDone() throws EmptyDescriptionException {
        Todo todo = new Todo("todo read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertFalse(todo.getIsDone());
        assertEquals(" ", todo.getStatusIcon());
    }
    
    @Test
    public void testTodoToString() throws EmptyDescriptionException {
        Todo todo = new Todo("todo read book");
        assertEquals("[T][ ] read book", todo.toString());
        
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }
}