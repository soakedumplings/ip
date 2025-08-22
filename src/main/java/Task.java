import java.util.ArrayList;

class Task {
    protected String description;
    protected boolean isDone;
    protected boolean isTodo;
    protected boolean isDeadline;
    protected boolean isEvent;

    private static final ArrayList<Task> tasks = new ArrayList<>();

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.isTodo = false;
        this.isDeadline = false;
        this.isEvent = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public String getDescription() {
        return this.description;
    }

    public static void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println(" No tasks in your list!");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i+1) + ". " + tasks.get(i).toString());
            }
        }
    }

    public static void addTask(String description) throws HoneyException {
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

    public static void addToList(Task task) {
        tasks.add(task);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    public static void markTask(String input) throws HoneyException {
        if (input.trim().equals("mark")) {
            throw new InvalidNumberFormatException("mark", "no number provided");
        }
        
        try {
            int taskNumber = Integer.parseInt(input.substring(5).trim()) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                Task task = tasks.get(taskNumber);
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + task);
            } else {
                throw new InvalidTaskNumberException("mark", tasks.size());
            }
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException("mark", input.substring(5).trim());
        }
    }

    public static void unmarkTask(String input) throws HoneyException {
        if (input.trim().equals("unmark")) {
            throw new InvalidNumberFormatException("unmark", "no number provided");
        }
        
        try {
            int taskNumber = Integer.parseInt(input.substring(7).trim()) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                Task task = tasks.get(taskNumber);
                task.markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + task);
            } else {
                throw new InvalidTaskNumberException("unmark", tasks.size());
            }
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException("unmark", input.substring(7).trim());
        }
    }

    public static void deleteTask(String input) throws HoneyException {
        if (input.trim().equals("delete")) {
            throw new InvalidNumberFormatException("delete", "no number provided");
        }

        try {
            int taskNumber = Integer.parseInt(input.substring(7).trim()) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                Task task = tasks.get(taskNumber);
                tasks.remove(taskNumber);
                System.out.println(" Noted. I've removed this task:");
                System.out.println("   " + task);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            } else {
                throw new InvalidTaskNumberException("delete", tasks.size());
            }
        } catch (NumberFormatException e) {
            throw new InvalidNumberFormatException("delete", input.substring(7).trim());
        }
    }

}

