import java.util.List;

public interface TaskManager {
    int addTask(Task task) throws ManagerException;
    void updateTask(Task task) throws ManagerException;

    SimpleTask getSimpleTask(int id);
    List<Integer> getAllSimpleTaskIds();
    void removeSimpleTask(int id);
    void removeAllSimpleTasks();

    EpicTask getEpicTask(int id);
    List<Integer> getAllEpicTasksIds();
    void removeEpicTask(int id);
    void removeAllEpicTasks();

    Subtask getSubtask(int id);
    List<Integer> getAllSubtaskIds();
    void removeSubtask(int id);
    void removeAllSubtasks();
}
