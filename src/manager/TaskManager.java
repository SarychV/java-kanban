package manager;

import task.EpicTask;
import task.SimpleTask;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.Set;

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
    Set<Task> getPrioritizedTasks();
}

