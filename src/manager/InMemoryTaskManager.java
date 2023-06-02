package manager;

import task.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, SimpleTask> simpleTasks;
    protected final Map<Integer, EpicTask> epicTasks;
    protected final Map<Integer, Subtask> subtasks;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public int addTask(Task task) {
        return addTaskInMemory(task);
    }

    protected int addTaskInMemory(Task task) {
        int taskId = 0;
        try {
            if (task == null) {
                throw new NullPointerException();     // NullArgument
            }
            if (task instanceof SimpleTask) {
                taskId = addSimpleTask((SimpleTask) task);
            }
            if (task instanceof EpicTask) {
                taskId = addEpicTask((EpicTask) task);
            }
            if (task instanceof Subtask) {
                taskId = addSubtask((Subtask) task);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return taskId;
    }

    @Override
    public void updateTask(Task task) {
        try {
            if (task == null) {
                throw new NullPointerException();     // NullArgument
            }
            if (task instanceof SimpleTask) {
                updateSimpleTask((SimpleTask) task);
            }
            if (task instanceof EpicTask) {
                updateEpicTask((EpicTask) task);
            }
            if (task instanceof Subtask) {
                updateSubtask((Subtask) task);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTask(Task task) {
        try {
            if (task == null) {
                throw new NullPointerException();     // NullArgument
            }
            int taskId = task.getId();

            if (task instanceof SimpleTask) {
                removeSimpleTask(taskId);
            }
            if (task instanceof EpicTask) {
                removeEpicTask(taskId);
            }
            if (task instanceof Subtask) {
                removeSubtask(taskId);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Task getTask(int id) {
        return getTaskInMemory(id);
    }

    protected Task getTaskInMemory(int id) {
        Task task;
        if ((task = getSimpleTask(id)) != null) {
            return task;
        } else if ((task = getEpicTask(id)) != null) {
            return task;
        } else {
            return getSubtask(id);
        }
    }

// **********************************************************  Методы для работы с задачами класса task. SimpleTask.
    @Override
    public SimpleTask getSimpleTask(int id) {
        SimpleTask task = simpleTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<SimpleTask> getAllSimpleTasks() {
        return List.copyOf(simpleTasks.values());
    }

    @Override
    public void removeSimpleTask(int id) {
        try {
            if (simpleTasks.remove(id) == null)
                throw new ManagerException(
                        "Задача с id=" + id + " в менеджере отсутствует. Удаление не выполнено.");
            historyManager.remove(id);
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void removeAllSimpleTasks() {
        for (Integer id : simpleTasks.keySet()) {
            simpleTasks.remove(id);
            historyManager.remove(id);
        }
    }

    protected int addSimpleTask(SimpleTask task) {
        int taskId = task.getId();
        if (taskId == 0) {
            taskId = TaskId.generate();
            task.setId(taskId);
        }
        simpleTasks.put(taskId, task);
        return taskId;
    }

    protected void updateSimpleTask(SimpleTask task) {
        int taskId = task.getId();
        try {
            if (simpleTasks.get(taskId) == null) {
                throw new ManagerException(     // SimpleTaskDoesNotExist
                        "Задача с id=" + taskId + " в менеджере отсутствует. Обновление не выполнено.");
            } else {
                simpleTasks.put(taskId, task);
            }
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


// ********************************************************** Методы для работы с задачами класса task. EpicTask.
    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask task = epicTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return List.copyOf(epicTasks.values());
    }

    @Override
    public void removeEpicTask(int id) {
        EpicTask epic = epicTasks.get(id);
        try {
            if (epic == null) {
                throw new ManagerException(
                        "Эпик с id=" + id + " в менеджере отсутствует. Удаление не выполнено.");
            }
            for (int subtaskId : epic.getSubtasks()) {
                removeSubtask(subtaskId);
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        removeAllSubtasks();
        for (int id : epicTasks.keySet()) {
            removeEpicTask(id);
        }
    }

    protected int addEpicTask(EpicTask task) {
        int taskId = task.getId();
        try {
            // Чтобы создался новый эпик, необходимо чтобы его список подзадач был пустым
            if (task.getSubtasks().size() != 0) {
                throw new ManagerException(
                        "Список подзадач эпика при добавлении в менеджер должен быть пустым!");
            }
            if (taskId == 0) {
                taskId = TaskId.generate();
                task.setId(taskId);     // Присваиваем эпику вновь сгенерированный id
            }
            epicTasks.put(taskId, task);
            return taskId;
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return taskId;
    }

    protected void updateEpicTask(EpicTask task) {
        int taskId = task.getId();
        EpicTask taskInMap = epicTasks.get(taskId);
        try {
            if (taskInMap == null) {
                throw new ManagerException(     // EpicTaskDoesNotExist
                        "Эпик с id=" + taskId + " в менеджере отсутствует. Обновление не выполнено.");
            } else if (!task.getSubtasks().equals(taskInMap.getSubtasks())) {
                throw new ManagerException(     // BadSubtasksData
                        "Списки подзадач у эпика при обновлении должны совпадать!");
            } else {
                epicTasks.put(taskId, task);
            }
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void updateEpicStatus(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        if (epic != null) {
            if (epic.getSubtasks().size() == 0) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(calculateStatus(getSubtaskStatuses(epicId)));
            }
        }
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

    private List<TaskStatus> getSubtaskStatuses(int epicId) {
        List<TaskStatus> result = new ArrayList<>();
        for (int id: epicTasks.get(epicId).getSubtasks()) {
            TaskStatus subtaskStatus = subtasks.get(id).getStatus();
            result.add(subtaskStatus);
        }
        return result;
    }

    // ********************************************************** Методы для работы с задачами класса task. Subtask.
    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        EpicTask epic;
        try {
            if (subtask == null) throw new ManagerException(
                    "Подзадача с id=" + id + " в менеджере отсутствует. Удаление не выполнено.");

            int epicId = subtask.getParentEpicId();
            subtasks.remove(id);
            historyManager.remove(id);

            epic = epicTasks.get(epicId);
            if (epic == null) throw new ManagerException(
                    "Эпик для подзадачи с id=" + id + " в менеджере отсутствует.");
            epic.unbindSubtask(id);
            updateEpicStatus(epicId);

        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId: subtasks.keySet()) {
            removeSubtask(subtaskId);
        }
        for (int epicId: epicTasks.keySet()) {
            updateEpicStatus(epicId);
        }
    }

    protected int addSubtask(Subtask task) {
        int taskId = task.getId();

        int parentEpicId = task.getParentEpicId();
        EpicTask parentEpic = epicTasks.get(parentEpicId);
        try {
            if (parentEpic == null) {       // BadParentEpic
                throw new ManagerException("При добавлении подзадачи определите для нее эпик!");
            } else {
                if(taskId == 0) {
                    taskId = TaskId.generate();
                    task.setId(taskId);
                }
                subtasks.put(taskId, task);
                parentEpic.bindSubtask(taskId);
                updateEpicStatus(parentEpicId);
                return taskId;
            }
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return taskId;
    }

    protected void updateSubtask(Subtask task) {
        int subtaskId = task.getId();
        Subtask subtaskInMap = subtasks.get(subtaskId);
        int parentEpicId = task.getParentEpicId();
        try {
            if (subtaskInMap == null) {         // SubtaskDoesNotExist
                throw new ManagerException(
                        "Подзадача id=" + subtaskId + " в менеджере отсутствует. Обновление не выполнено.");
            } else if (parentEpicId != subtaskInMap.getParentEpicId()) { // SubtaskHasWrongEpic
                throw new ManagerException(
                        "При обновлении подзадачи id эпика подзадачи должен оставаться прежним.");
            } else {
                subtasks.put(subtaskId, task);
                updateEpicStatus(parentEpicId);
            }
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        InMemoryTaskManager anotherManager = (InMemoryTaskManager) obj;
        return simpleTasks.equals(anotherManager.simpleTasks)
                && epicTasks.equals(anotherManager.epicTasks)
                && subtasks.equals(anotherManager.subtasks)
                && historyManager.getHistory().equals(anotherManager.historyManager.getHistory());
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Список задач менеджера\n");

        buffer.append("Список обычных задач:\n");
        if (simpleTasks.size() == 0) {
            buffer.append("\tСписок пуст..\n");
        } else {
            for (SimpleTask simple : simpleTasks.values()) {
                buffer.append("\t").append(simple.toString()).append("\n");
            }
        }

        buffer.append("Список эпиков:\n");
        if (epicTasks.size() == 0) {
            buffer.append("\tСписок пуст..\n");
        } else {
            for (EpicTask epic : epicTasks.values()) {
                buffer.append("\t").append(epic.toString()).append("\n");
            }
        }

        buffer.append("Список подзадач:\n");
        if (subtasks.size() == 0) {
            buffer.append("\tСписок пуст..\n");
        } else {
            for (Subtask subtask : subtasks.values()) {
                buffer.append("\t").append(subtask.toString()).append("\n");
            }
        }

        return buffer.toString();
    }
}
