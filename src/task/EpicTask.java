package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {
    private final List<Integer> subtasks;
    protected LocalDateTime endTime;


    public EpicTask(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        if (startTime != null) {
            endTime = startTime.plusMinutes(duration);
        } else {
            endTime = null;
        }
    }

    public EpicTask(String title, String description, int taskId, TaskStatus status) {
        super(title, description, taskId, status);
        this.subtasks = new ArrayList<>();
        if (startTime != null) {
            this.endTime = startTime.plusMinutes(duration);
        } else {
            endTime = null;
        }
    }

    public EpicTask(String title, String description, int taskId, TaskStatus status,
                    LocalDateTime startTime, int duration, LocalDateTime endTime) {
        super(title, description, taskId, status, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = endTime;
    }

    public List<Integer> getSubtasks() {
        return List.copyOf(subtasks);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void bindSubtask(int id) {
        subtasks.add(id);
    }

    public void unbindSubtask(int id) {
        subtasks.remove((Integer)id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        EpicTask anotherTask = (EpicTask) obj;
        return super.equals(anotherTask)
                && Objects.equals(this.subtasks, anotherTask.subtasks);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + subtasks.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
                + super.toString()
                + ", endTime='" + endTime
                + "', subtasks='" + subtasks + "'}";
    }
}
