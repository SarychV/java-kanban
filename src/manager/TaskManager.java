package manager;

import java.util.List;

import task.Task;
import task.SimpleTask;
import task.EpicTask;
import task.Subtask;

public interface TaskManager {
    int addTask(Task task);
    void updateTask(Task task);
    void removeTask(Task task);
    Task getTask(int id);

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

    List<Task> getHistory();
}

