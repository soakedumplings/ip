package honey.tasklist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import honey.exceptions.HoneyException;
import honey.exceptions.InvalidCommandException;
import honey.exceptions.InvalidTaskNumberException;
import honey.task.Deadline;
import honey.task.Event;
import honey.task.Task;
import honey.task.Todo;


public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

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

    public void addToList(Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int size() {
        return tasks.size();
    }
}