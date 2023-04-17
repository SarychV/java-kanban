import java.util.HashMap;
import java.util.List;

public class Manager {
    private final HashMap<Integer, SimpleTask> simpleTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;

    public Manager() {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

// **********************************************************  Методы для работы с задачами класса SimpleTask.
    public void addSimpleTask(SimpleTask task) throws ManagerException {
        int taskId;

        if (task == null) {
            throw new ManagerException();  // NullArgument
        }
        taskId = task.getId();
        if (simpleTasks.get(taskId) != null) {
            throw new ManagerException();  // TaskExists
        }
        simpleTasks.put(taskId, task);
    }

    public SimpleTask getSimpleTask(int id) {
        return simpleTasks.get(id);
    }

    public List<Integer> getAllSimpleTaskIds() {
        return List.copyOf(simpleTasks.keySet());
    }

    public void removeSimpleTask(int id) {
        simpleTasks.remove(id);
    }

    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    public void updateSimpleTask(SimpleTask task) throws ManagerException {
        int taskId;

        if (task == null) {
            throw new ManagerException();  // NullArgument
        }
        taskId = task.getId();
        if (simpleTasks.get(taskId) == null) {
            throw new ManagerException();  // SimpleTaskDoesNotExist
        }
        simpleTasks.put(taskId, task);
    }


// ********************************************************** Методы для работы с задачами класса EpicTask.
    public void addEpicTask(EpicTask task) throws ManagerException {
        int taskId;

        if (task == null) {
            throw new ManagerException(); // NullArgument
        }
        taskId = task.getId();

        if (epicTasks.get(taskId) != null) {
            throw new ManagerException();  // TaskExists
        }
        // Список подзадач нового эпика должен быть пустым
        if (task.getSubtasks().size() != 0) {
            throw new ManagerException();  // BadTaskData
        }
        epicTasks.put(taskId, task); // Возвращает null
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.get(id);
    }

    public List<Integer> getAllEpicTasksIds() {
        return List.copyOf(epicTasks.keySet());
    }

    public void removeEpicTask(int id) {
        try {
            EpicTask epic = epicTasks.get(id);
            if (epic == null) {
                throw new ManagerException();  // EpicDoesNotExist
            }
            for (int subtaskId : epic.getSubtasks()) {
                removeSubtask(subtaskId);
            }
            epicTasks.remove(id);
        } catch (ManagerException e) {
            System.err.println("Эпик с id=" + id + " не существует. Отсутствует объект для удаления.");
        }
    }

    public void removeAllEpicTasks() {
        subtasks.clear();
        epicTasks.clear();
    }

    public void updateEpicTask(EpicTask task) throws ManagerException {
        int taskId;
        EpicTask taskInMap;

        if (task == null) {
            throw new ManagerException();  // NullArgument
        }
        taskId = task.getId();
        taskInMap = epicTasks.get(taskId);
        if (taskInMap == null) {
            throw new ManagerException();  // EpicTaskDoesNotExist
        }
        if (!task.getSubtasks().equals(taskInMap.getSubtasks())) {
            throw new ManagerException();  // EpicsDoNotMatch
        }
        epicTasks.put(taskId, task);
    }

// ********************************************************** Методы для работы с задачами класса Subtask.
    public void addSubtask(Subtask task) throws ManagerException {
        Subtask taskInMap;
        EpicTask parentEpic;

        if (task == null) {
            throw new ManagerException();  // NullArgument
        }

        int taskId = task.getId();
        taskInMap = subtasks.get(taskId);
        if (taskInMap != null) {
            throw new ManagerException();  // TaskExists
        }

        int epicId = task.getParentEpicId();
        parentEpic = epicTasks.get(epicId);
        if (parentEpic == null) {
            throw new ManagerException();  // BadParentEpic
        }

        subtasks.put(taskId, task);
        parentEpic.bindSubtask(taskId);
        parentEpic.updateStatus();
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public List<Integer> getAllSubtaskIds() {
        return List.copyOf(subtasks.keySet());
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        EpicTask epic;
        int epicId = 0;

        try {
            if (subtask == null) {
                throw new ManagerException();  // SubtaskDoesNotExist
            }
            epicId = subtask.getParentEpicId();
            subtasks.remove(id);
        } catch (ManagerException e) {
            System.err.println("Подзадача с id=" + id + " не существует, чтобы ее удалить.");
        }

        try {
            epic = epicTasks.get(epicId);
            if (epic == null) {
                throw new ManagerException();  // EpicTaskDoesNotExist
            }
            epic.unbindSubtask(id);
            epic.updateStatus();
        } catch (ManagerException e) {
            System.err.println("Эпик для подзадачи с id=" + id + " не существует.");
        }
    }

    public void removeAllSubtasks() {
        for (int subtaskId: getAllSubtaskIds()) {
            removeSubtask(subtaskId);
        }
        for (int epicId: getAllEpicTasksIds()) {
            epicTasks.get(epicId).updateStatus();
        }
    }

    public void updateSubtask(Subtask subtask) throws ManagerException {
        int subtaskId;
        Subtask subtaskInMap;

        if (subtask == null) {
            throw new ManagerException();  // NullArgument
        }

        subtaskId = subtask.getId();
        subtaskInMap = subtasks.get(subtaskId);

        if (subtaskInMap == null) {
            throw new ManagerException();  // SubtaskDoesNotExist
        }

        if (subtask.getParentEpicId() == subtaskInMap.getParentEpicId()) {
            subtasks.put(subtaskId, subtask);
            epicTasks.get(subtask.getParentEpicId()).updateStatus();
        } else {
            throw new ManagerException();  // SubtaskHasWrongEpic
        }
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
                buffer.append("\t" + simple.toString() + "\n");
            }
        }

        buffer.append("Список эпиков:\n");
        if (epicTasks.size() == 0) {
            buffer.append("\tСписок пуст..\n");
        } else {
            for (EpicTask epic : epicTasks.values()) {
                buffer.append("\t" + epic.toString() + "\n");
            }
        }

        buffer.append("Список подзадач:\n");
        if (subtasks.size() == 0) {
            buffer.append("\tСписок пуст..\n");
        } else {
            for (Subtask subtask : subtasks.values()) {
                buffer.append("\t" + subtask.toString() + "\n");
            }
        }

        return buffer.toString();
    }
}
