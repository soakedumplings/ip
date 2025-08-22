class Task {
    protected String description;
    protected boolean isDone;
    protected boolean isTodo;
    protected boolean isDeadline;
    protected boolean isEvent;

    private static Task[] tasks = new Task[100];
    private static int taskCount = 0;

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
        if (taskCount == 0) {
            System.out.println(" No tasks in your list!");
        } else {
            System.out.println(" Here are the tasks in your list:");
            for (int i = 0; i < taskCount; i++) {
                System.out.println(" " + (i+1) + ". " + tasks[i].toString());
            }
        }
    }

    public static void addTask(String description) {
        taskCount++;
        if (description.startsWith("todo ")) {
            Todo task = new Todo(description);
            addToList(task);
        } else if (description.startsWith("deadline ")) {
            Deadline task = new Deadline(description);
            addToList(task);
        } else if (description.startsWith("event ")) {
            Event task = new Event(description);
            addToList(task);
        } else {
            System.out.println(" Please specify type of Task- Todo, Deadline, Event.");
        }
    }

    public static void addToList(Task task) {
        tasks[taskCount - 1] = task;
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
    }

    public static void markTask(String input) {
        try {
            int taskNumber = Integer.parseInt(input.substring(5)) - 1;
            if (taskNumber >= 0 && taskNumber < taskCount) {
                Task task = tasks[taskNumber];
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + task);
            } else {
                System.out.println(" Task number out of range!");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Please provide a valid task number!");
        }
    }

    public static void unmarkTask(String input) {
        try {
            int taskNumber = Integer.parseInt(input.substring(7)) - 1;
            if (taskNumber >= 0 && taskNumber < taskCount) {
                Task task = tasks[taskNumber];
                task.markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + task);
            } else {
                System.out.println(" Task number out of range!");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Please provide a valid task number!");
        }
    }

}

