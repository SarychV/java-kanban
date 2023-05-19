package task;

public class Subtask extends Task {
    private final int parentEpicId;

    public Subtask(int parentEpicId, String title, String description) {
        super(title, description);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(int parentEpicId, String title, String description, int taskId, TaskStatus status) {
        super(title, description, taskId, status);
        this.parentEpicId = parentEpicId;
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + super.toString() + ", epic='" + parentEpicId + "'}";
    }
}
