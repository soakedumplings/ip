class Task {
    protected String description;
    protected boolean isDone;
    private static Task[] tasks = new Task[100];
    private static int taskCount = 0;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
        tasks[taskCount] = new Task(description);
        taskCount++;
        System.out.println(" added: " + description);
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

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}