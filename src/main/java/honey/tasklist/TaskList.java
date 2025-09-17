package honey.tasklist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        assert tasks != null : "Task list cannot be null";
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
            throw new InvalidCommandException("Oh my! I'm a bit confused, sweetie. Could you help me understand what this means?\n" + description);
        }
    }

    /**
     * Adds a task to the list and displays confirmation message.
     *
     * @param task The task to add.
     */
    public String addToList(Task task) {
        assert task != null : "Task to add cannot be null";
        int sizeBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == sizeBefore + 1 : "Task list size should increase by 1 after adding";
        return "Perfect, my dear! ✨ I've lovingly added this to our hive: " + "\n" + task + "\n"
                + "Together we're managing " + tasks.size() + " sweet tasks! 🐝";
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
            return "Wonderful work, my sweet Bee! 🌟 Let's celebrate this accomplishment:" + "\n" + task + "\n" + "I'm so proud of us! 💕";

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
            return "No worries, darling! 💛 Sometimes we need more time, and that's perfectly okay:" + "\n" + task + "\n" + "We'll tackle it together when you're ready! 🤗";
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
            int sizeBefore = tasks.size();
            tasks.remove(taskNumber - 1);
            assert tasks.size() == sizeBefore - 1 : "Task list size should decrease by 1 after deletion";
            return "All done, sweetheart! 🌸 I've gently removed this from our hive:" + "\n" + task
                    + "\n" + "Now we're focusing on " + tasks.size() + " lovely tasks together! ✨";
        } else {
            throw new InvalidTaskNumberException("delete", tasks.size());
        }
    }

    /**
     * Lists all tasks in the task list.
     */
    public String listTasks() {
        if (tasks.isEmpty()) {
            return "What a peaceful moment! 🌺 Our hive is empty and ready for new adventures, my dear Bee! 🍯";
        }

        String taskList = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i).toString() + "\n")
                .collect(Collectors.joining());
        return "Here's our beautiful collection of tasks, sweetie! Let's see what we're working on together: 💖\n" + taskList;
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

        List<Task> dueTasks = tasks.stream()
                .filter(task -> {
                    if (task instanceof Deadline deadline) {
                        return deadline.getDeadline().toLocalDate().equals(queryDate);
                    } else if (task instanceof Event event) {
                        return event.getStartDate().equals(queryDate)
                                || event.getEndDate().equals(queryDate)
                                || (queryDate.isAfter(event.getStartDate()) && queryDate.isBefore(event.getEndDate()));
                    }
                    return false;
                })
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy");
        if (dueTasks.isEmpty()) {
            return "✨ How lovely! No urgent tasks on " + queryDate.format(formatter) + " - perfect time for some self-care, my dear! 🌸";
        }

        String taskList = IntStream.range(0, dueTasks.size())
                .mapToObj(i -> (i + 1) + ". " + dueTasks.get(i).toString() + "\n")
                .collect(Collectors.joining());
        return "💫 Here are our important tasks for " + queryDate.format(formatter) + ", darling! Let's tackle them together:\n" + taskList;
    }


    /**
     * Finds and displays tasks that contain the specified keyword in their description.
     * The search is case-insensitive.
     *
     * @param keyword The keyword to search for in task descriptions.
     */
    public String findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        List<Task> matchingTasks = tasks.stream()
                .filter(task -> getTaskDisplayDescription(task).toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());

        if (matchingTasks.isEmpty()) {
            return "Hmm, my sweet Bee! 🔍 I couldn't find any tasks with that keyword. Maybe we can try a different search? 💕";
        }

        return IntStream.range(0, matchingTasks.size())
                .mapToObj(i -> (i + 1) + ". " + matchingTasks.get(i).toString() + "\n")
                .collect(Collectors.joining());
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

    /**
     * Sorts and displays all deadline tasks by their deadline dates in ascending order.
     * Shows overdue tasks with an "OVERDUE" indication.
     *
     * @return A formatted string showing sorted deadline tasks with overdue indicators.
     */
    public String sortDeadlines() {
        List<Deadline> deadlineTasks = tasks.stream()
                .filter(task -> task instanceof Deadline)
                .map(task -> (Deadline) task)
                .sorted(Comparator.comparing(Deadline::getDeadline))
                .collect(Collectors.toList());

        if (deadlineTasks.isEmpty()) {
            return "How wonderful! 🎉 No deadlines to worry about right now, my dear! Time to relax and maybe add some new goals? 🌟";
        }

        return IntStream.range(0, deadlineTasks.size())
                .mapToObj(i -> {
                    Deadline deadline = deadlineTasks.get(i);
                    String taskString = (i + 1) + ". " + deadline.toString();
                    if (deadline.isOverdue()) {
                        taskString += " [OVERDUE]";
                    }
                    return taskString + "\n";
                })
                .collect(Collectors.joining("", "Here are our deadline tasks, beautifully organized by date, sweetheart! 📅✨\n", ""));
    }
}
