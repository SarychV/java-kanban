public abstract class Task {
    int taskId;
    private String title;
    private String description;
    private TaskStatus status;

    protected Task(String title, String description) {
        this.taskId = 0;
        this.status = TaskStatus.NEW;
        this.title = title;
        this.description = description;
    }

    protected Task(String title, String description, int taskId, TaskStatus status) {
        this.taskId = taskId;
        this.status = status;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return taskId;
    }

    public void setId(int taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "title='" + title +"', taskId='" + taskId + "', status='" + status + "'";
    }

}
