package task;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Task {
    private int taskId;
    private String title;
    private String description;
    private TaskStatus status;

    protected int duration;
    protected LocalDateTime startTime;

    protected Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    protected Task(String title, String description, int taskId, TaskStatus status) {
        this(title, description);
        this.taskId = taskId;
        this.status = status;
    }

    protected Task(String title, String description, LocalDateTime startTime, int durationInMinutes) {
        this(title, description);
        this.startTime =  startTime;
        this.duration = durationInMinutes;
    }

    protected  Task(String title, String description,
                    int taskId, TaskStatus status,
                    LocalDateTime startTime, int durationInMinutes) {
        this.title = title;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
        this.startTime = startTime;
        this.duration = durationInMinutes;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int taskId) {
        this.taskId = taskId;
    }

    public int getId() {
        return taskId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public void setDuration(int durationInMinutes) {
        this.duration = durationInMinutes;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean hasTimeIntersection(Task other) {
        boolean hasNo = false;
        boolean has = true;
        LocalDateTime end1;
        LocalDateTime end2;

        if (other == null) return hasNo;

        LocalDateTime start1 = this.getStartTime();
        if (start1 == null) {
            return hasNo;
        } else {
            end1 = start1.plusMinutes(this.getDuration());
        }

        LocalDateTime start2 = other.getStartTime();
        if (start2 == null) {
            return hasNo;
        } else {
            end2 = start2.plusMinutes(other.getDuration());
        }

        if(end1.isBefore(start2) || end1.isEqual(start2)
                || start1.isAfter(end2) || start1.isEqual(end2)) return hasNo;
        return has;
    }

    @Override
    public boolean equals(Object obj) {
        Task anotherTask = (Task) obj;
        return Objects.equals(this.title, anotherTask.title)
                && Objects.equals(this.description, anotherTask.description)
                && Objects.equals(this.startTime, anotherTask.startTime)
                && this.duration == anotherTask.duration
                && this.taskId == anotherTask.taskId
                && this.status == anotherTask.status;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash += taskId;
        if (title != null) hash += hash * 31 + title.hashCode();
        if (description != null) hash += hash * 31 + description.hashCode();
        if (startTime != null) hash += hash * 31 + startTime.hashCode();
        hash += hash * 31 + duration;
        hash += status.ordinal();
        return hash;
    }

    @Override
    public String toString() {
        return "title='" + title + "', id='" + taskId + "', status='" + status
                + "', startTime='" + startTime + "', duration='" + duration + "'";
    }
}
