package task;

import java.time.LocalDateTime;

public class SimpleTask extends Task {
    public SimpleTask(String title, String description) {
        super(title, description);
    }

    public SimpleTask(String title, String description, int taskId, TaskStatus status) {
        super(title, description, taskId, status);
    }

    public SimpleTask(String title, String description, LocalDateTime startTime, int durationInMinutes) {
        super(title, description, startTime, durationInMinutes);
    }

    public SimpleTask(String title, String description,
                    int taskId, TaskStatus status,
                    LocalDateTime startTime, int durationInMinutes) {
        super(title, description, taskId, status, startTime, durationInMinutes);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + super.toString() + "}";
    }
}
