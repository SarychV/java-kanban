import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subtasks;
    private Manager manager; // Используется для получения доступа к своим подзадачам

    public EpicTask(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public EpicTask(Manager manager, String title, String description) {
        this(title, description);
        this.manager = manager;
    }

    public EpicTask(Manager manager, String title, String description, int taskId) {
        this(manager, title, description);
        this.taskId = taskId;
    }


    public void bindSubtask(int id) {
        subtasks.add(id);
    }

    public void unbindSubtask(int id) {
        subtasks.remove((Integer)id);
    }

    public List<Integer> getSubtasks() {
        return List.copyOf(subtasks);
    }

    void updateStatus() {
        if (manager != null) {
            if (subtasks.size() == 0) {
                this.setStatus(TaskStatus.NEW);
            } else {
                this.setStatus(calculateStatus(getSubtaskStatuses()));
            }
        } else {
            System.err.println("updateStatus: отсутствует доступ к объекту manager");
        }
    }

    private List<TaskStatus> getSubtaskStatuses() {
        List<TaskStatus> result = new ArrayList<>();
        if (manager != null) {
            for (int id: subtasks) {
                TaskStatus subtaskStatus = manager.getSubtask(id).getStatus();
                result.add(subtaskStatus);
            }
        }
        return result;
    }

    private TaskStatus calculateStatus(List<TaskStatus> statuses) {
        TaskStatus result = statuses.get(0);
        for (TaskStatus status: statuses) {
            if (result != status) {
                result = TaskStatus.IN_PROGRESS;
                break;
            }
        }
        return result;
    }

    public void bindToManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
                + super.toString()
                + ", subtasks='" + subtasks + "'}";
    }

    public static void main(String[] args) {
        List<TaskStatus> testStatuses;
        //testStatuses = new ArrayList<>();
        testStatuses = List.of(TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE);
        EpicTask epic = new EpicTask("Test", "Test status calculation.");
        System.out.println(epic.calculateStatus(testStatuses));
    }

}
