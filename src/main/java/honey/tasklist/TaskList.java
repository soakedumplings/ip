package honey.tasklist;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import task.Task;
import task.Todo;
import task.Deadline;
import task.Event;
import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidTaskNumberException;

/**
 * Represents a list of tasks in the Honey application.
 * Manages the collection of tasks and provides operations to add, delete, mark, and find tasks.
 */
public class TaskList {
    /** List storing all tasks */
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list with the specified tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a new task based on the description string.
     * Parses the description to determine task type and creates appropriate task.
     *
     * @param description Full command string describing the task.
     * @throws HoneyException If the task cannot be created due to invalid format.
     */
    public void addTask(String description) throws HoneyException {
        if (description.startsWith("todo")) {
            Todo task = new Todo(description);
            addToList(task);
        } else if (description.startsWith("deadline")) {
            Deadline task = new Deadline(description);
            addToList(task);
        } else if (description.startsWith("event")) {
            Event task = new Event(description);
            addToList(task);
        } else {
            throw new InvalidCommandException(description);
        }
    }

    /**
     * Adds the specified task to the task list.
     * Displays confirmation message with current task count.
     *
     * @param task Task to be added to the list.
     */
    public void addToList(Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Marks the specified task as done.
     *
     * @param taskNumber One-based index of the task to mark.
     * @throws HoneyException If the task number is invalid.
     */
    public void markTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            task.markAsDone();
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + task);
        } else {
            throw new InvalidTaskNumberException("mark", tasks.size());
        }
    }

    /**
     * Marks the specified task as not done.
     *
     * @param taskNumber One-based index of the task to unmark.
     * @throws HoneyException If the task number is invalid.
     */
    public void unmarkTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            task.markAsNotDone();
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + task);
        } else {
            throw new InvalidTaskNumberException("unmark", tasks.size());
        }
    }

    /**
     * Deletes the specified task from the list.
     *
     * @param taskNumber One-based index of the task to delete.
     * @throws HoneyException If the task number is invalid.
     */
    public void deleteTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            tasks.remove(taskNumber - 1);
            System.out.println(" Noted. I've removed this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        } else {
            throw new InvalidTaskNumberException("delete", tasks.size());
        }
    }

    /**
     * Displays all tasks in the list.
     * Shows a numbered list of all tasks with their status.
     */
    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println(" No tasks in your list!");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i+1) + ". " + tasks.get(i).toString());
            }
        }
    }

    /**
     * Finds and displays tasks due on the specified date.
     * Includes deadlines due on the date and events occurring on the date.
     *
     * @param dateStr Date string in yyyy-MM-dd format.
     * @throws HoneyException If the date format is invalid.
     */
    public void findTasksDue(String dateStr) throws HoneyException {
        LocalDate queryDate;
        
        try {
            queryDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidCommandException("Invalid date format. Please use yyyy-MM-dd");
        }
        
        ArrayList<Task> dueTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                LocalDate deadlineDate = deadline.deadline.toLocalDate();
                if (deadlineDate.equals(queryDate)) {
                    dueTasks.add(task);
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                if ((event.startDate.equals(queryDate)) || 
                    (event.endDate.equals(queryDate)) ||
                    (queryDate.isAfter(event.startDate) && queryDate.isBefore(event.endDate))) {
                    dueTasks.add(task);
                }
            }
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        if (dueTasks.isEmpty()) {
            System.out.println(" No tasks due on " + queryDate.format(formatter) + "!");
        } else {
            System.out.println(" Here are the tasks due on " + queryDate.format(formatter) + ":");
            for (int i = 0; i < dueTasks.size(); i++) {
                System.out.println(" " + (i+1) + ". " + dueTasks.get(i).toString());
            }
        }
    }

    /**
     * Returns the list of tasks.
     *
     * @return ArrayList containing all tasks.
     */
    public ArrayList<Task> getTasks() {

        return tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Size of the task list.
     */
    public int size() {
        return tasks.size();
    }
}