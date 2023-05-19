package manager;

import task.Task;
import task.SimpleTask;
import task.EpicTask;
import task.Subtask;

import java.util.List;

public interface TaskManager {
    int addTask(Task task) throws ManagerException;
    void updateTask(Task task) throws ManagerException;

    SimpleTask getSimpleTask(int id);
    List<SimpleTask> getAllSimpleTasks();
    void removeSimpleTask(int id);
    void removeAllSimpleTasks();

    EpicTask getEpicTask(int id);
    List<EpicTask> getAllEpicTasks();
    void removeEpicTask(int id);
    void removeAllEpicTasks();

    Subtask getSubtask(int id);
    List<Subtask> getAllSubtasks();
    void removeSubtask(int id);
    void removeAllSubtasks();

    public List<Task> getHistory();
}

