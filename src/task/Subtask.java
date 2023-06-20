package task;

import java.time.LocalDateTime;

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

    public Subtask(int parentEpicId, String title, String description,
                   LocalDateTime startTime, int durationInMinutes) {
        super(title, description, startTime, durationInMinutes);
        this.parentEpicId = parentEpicId;
    }

    public Subtask(int parentEpicId, String title, String description,
                      int taskId, TaskStatus status,
                      LocalDateTime startTime, int durationInMinutes) {
        super(title, description, taskId, status, startTime, durationInMinutes);
        this.parentEpicId = parentEpicId;
    }

    public int getParentEpicId() {
        return parentEpicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask anotherTask = (Subtask) obj;
        return super.equals(anotherTask) && this.parentEpicId == anotherTask.parentEpicId;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + parentEpicId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + super.toString() + ", epic='" + parentEpicId + "'}";
    }
}
