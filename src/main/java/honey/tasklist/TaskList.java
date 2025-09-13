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

/**
 * Manages a list of tasks and provides operations for adding, removing, and searching tasks.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList with the provided list of tasks.
     *
     * @param tasks The list of tasks to initialize with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a new task based on the description string.
     *
     * @param description The task description string.
     * @throws HoneyException If the task creation fails.
     */
    public String addTask(String description) throws HoneyException {
        if (description.startsWith("todo")) {
            Todo task = new Todo(description);
            return addToList(task);
        } else if (description.startsWith("deadline")) {
            Deadline task = new Deadline(description);
            return addToList(task);
        } else if (description.startsWith("event")) {
            Event task = new Event(description);
            return addToList(task);
        } else {
            throw new InvalidCommandException(description);
        }
    }

    /**
     * Adds a task to the list and displays confirmation message.
     *
     * @param task The task to add.
     */
    public String addToList(Task task) {
        tasks.add(task);
        return "Got it. I've added this task: " + "\n" + task + "\n"
                + "Now you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Marks a task as done.
     *
     * @param taskNumber The task number to mark (1-indexed).
     * @throws HoneyException If the task number is invalid.
     */
    public String markTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            task.markAsDone();
            return "Nice! I've marked this task as done:" + "\n" + task;

        } else {
            throw new InvalidTaskNumberException("mark", tasks.size());
        }
    }

    /**
     * Marks a task as not done.
     *
     * @param taskNumber The task number to unmark (1-indexed).
     * @throws HoneyException If the task number is invalid.
     */
    public String unmarkTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            task.markAsNotDone();
            return "OK, I've marked this task as not done yet: " + "\n" + task;
        } else {
            throw new InvalidTaskNumberException("unmark", tasks.size());
        }
    }

    /**
     * Deletes a task from the list.
     *
     * @param taskNumber The task number to delete (1-indexed).
     * @throws HoneyException If the task number is invalid.
     */
    public String deleteTask(int taskNumber) throws HoneyException {
        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            tasks.remove(taskNumber - 1);
            return "Noted. I've removed this task:" + "\n" + task
                    + "\n" + "Now you have " + tasks.size() + " tasks in the list.";
        } else {
            throw new InvalidTaskNumberException("delete", tasks.size());
        }
    }

    /**
     * Lists all tasks in the task list.
     */
    public String listTasks() {
        if (tasks.isEmpty()) {
            return "No tasks in your list!";
        } else {
            StringBuilder list = new StringBuilder();
            for (int i = 0; i < tasks.size(); i++) {
                list.append(i + 1).append(". ").append(tasks.get(i).toString()).append("\n");
            }
            return "Here are the tasks in your list:\n" + list;
        }
    }

    /**
     * Finds and displays tasks due on a specific date.
     *
     * @param dateStr The date string to search for (yyyy-MM-dd format).
     * @throws HoneyException If the date format is invalid.
     */
    public String findTasksDue(String dateStr) throws HoneyException {
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
                LocalDate deadlineDate = deadline.getDeadline().toLocalDate();
                if (deadlineDate.equals(queryDate)) {
                    dueTasks.add(task);
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                if ((event.getStartDate().equals(queryDate))
                        || (event.getEndDate().equals(queryDate))
                        || (queryDate.isAfter(event.getStartDate()) && queryDate.isBefore(event.getEndDate()))) {
                    dueTasks.add(task);
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        if (dueTasks.isEmpty()) {
            return " No tasks due on " + queryDate.format(formatter) + "!";
        } else {
            StringBuilder tasksDue = new StringBuilder();
            for (int i = 0; i < dueTasks.size(); i++) {
                tasksDue.append(i + 1).append(". ").append(dueTasks.get(i).toString()).append("\n");
            }
            return " Here are the tasks due on " + queryDate.format(formatter) + ":\n" + tasksDue;
        }
    }


    /**
     * Finds and displays tasks that contain the specified keyword in their description.
     * The search is case-insensitive.
     *
     * @param keyword The keyword to search for in task descriptions.
     */
    public String findTasks(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Task task : tasks) {
            String taskDescription = getTaskDisplayDescription(task).toLowerCase();
            if (taskDescription.contains(lowerKeyword)) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            return "No matching tasks found!";
        } else {
            StringBuilder list = new StringBuilder();
            for (int i = 0; i < matchingTasks.size(); i++) {
                list.append(i + 1).append(". ").append(matchingTasks.get(i).toString()).append("\n");
            }
            return list.toString();
        }
    }

    /**
     * Gets the display description of a task for searching purposes.
     * Extracts the actual task description without command prefixes.
     *
     * @param task The task to get the description from.
     * @return The display description of the task.
     */
    private String getTaskDisplayDescription(Task task) {
        if (task instanceof Todo) {
            return task.getDescription().substring(5); // Remove "todo " prefix
        } else if (task instanceof Deadline) {
            return ((Deadline) task).getTaskName();
        } else if (task instanceof Event) {
            return ((Event) task).getTaskName();
        }
        return task.getDescription();
    }

    /**
     * Gets the list of tasks.
     *
     * @return The list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Gets the number of tasks in the list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }
}
