public class SimpleTask extends Task {
    public SimpleTask(String title, String description) {
        super(title, description);
    }

    public SimpleTask(String title, String description, int taskId, TaskStatus status) {
        super(title, description, taskId, status);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + super.toString() + "}";
    }
}
