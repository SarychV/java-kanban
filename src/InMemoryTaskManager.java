import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, SimpleTask> simpleTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager history) {
        simpleTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = history;
    }

    @Override
    public int addTask(Task task) throws ManagerException {
        int taskId = 0;
        if (task == null) {
            throw new ManagerException();     // NullArgument
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
        return taskId;
    }

    @Override
    public void updateTask(Task task) throws ManagerException {
        if (task == null) {
            throw new ManagerException();     // NullArgument
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
    }

// **********************************************************  Методы для работы с задачами класса SimpleTask.
    @Override
    public SimpleTask getSimpleTask(int id) {
        SimpleTask task = simpleTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Integer> getAllSimpleTaskIds() {
        return List.copyOf(simpleTasks.keySet());
    }

    @Override
    public void removeSimpleTask(int id) {
        simpleTasks.remove(id);
    }

    @Override
    public void removeAllSimpleTasks() {
        simpleTasks.clear();
    }

    private int addSimpleTask(SimpleTask task) {
        int taskId = TaskId.generate();
        task.setId(taskId);
        simpleTasks.put(taskId, task);
        return taskId;
    }

    private void updateSimpleTask(SimpleTask task) throws ManagerException {
        int taskId = task.getId();
        if (simpleTasks.get(taskId) == null) {
            throw new ManagerException();  // SimpleTaskDoesNotExist
        } else {
            simpleTasks.put(taskId, task);
        }
    }


// ********************************************************** Методы для работы с задачами класса EpicTask.
    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask task = epicTasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Integer> getAllEpicTasksIds() {
        return List.copyOf(epicTasks.keySet());
    }

    @Override
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

    @Override
    public void removeAllEpicTasks() {
        subtasks.clear();
        epicTasks.clear();
    }

    private int addEpicTask(EpicTask task) throws ManagerException {
        int taskId = TaskId.generate();

        // Чтобы создался новый эпик, необходимо чтобы его список подзадач был пустым,
        // либо чтобы подзадачи из его списка уже существовали в менеджере.
        // Проверим это!
        if (task.getSubtasks().size() != 0) {
            for (int subtaskId: task.getSubtasks()) {
                if (!this.hasSubtask(subtaskId)) {
                    throw new ManagerException();  // BadSubtasksData
                }
            }
        }
        task.setId(taskId);     // Присваиваем эпику вновь сгенерированный id
        epicTasks.put(taskId, task);
        return taskId;
    }

    private void updateEpicTask(EpicTask task) throws ManagerException {
        int taskId = task.getId();
        EpicTask taskInMap = epicTasks.get(taskId);

        if (taskInMap == null) {
            throw new ManagerException();   // EpicTaskDoesNotExist
        } else if (!task.getSubtasks().equals(taskInMap.getSubtasks())) {
            throw new ManagerException();   // BadSubtasksData
        } else {
            epicTasks.put(taskId, task);
        }
    }

    private void updateEpicStatus(int epicId) {
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

    private boolean hasSubtask(int id) {
        return subtasks.containsKey(id);
    }

    // ********************************************************** Методы для работы с задачами класса Subtask.
    @Override
    public Subtask getSubtask(int id) {
        Subtask task = subtasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Integer> getAllSubtaskIds() {
        return List.copyOf(subtasks.keySet());
    }

    @Override
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
            updateEpicStatus(epicId);
        } catch (ManagerException e) {
            System.err.println("Эпик для подзадачи с id=" + id + " не существует.");
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId: getAllSubtaskIds()) {
            removeSubtask(subtaskId);
        }
        for (int epicId: getAllEpicTasksIds()) {
            updateEpicStatus(epicId);
        }
    }

    private int addSubtask(Subtask task) throws ManagerException {
        int taskId = TaskId.generate();
        int parentEpicId = task.getParentEpicId();
        EpicTask parentEpic = epicTasks.get(parentEpicId);

        if (parentEpic == null) {
            throw new ManagerException();  // BadParentEpic
        } else {
            task.setId(taskId);
            subtasks.put(taskId, task);
            parentEpic.bindSubtask(taskId);
            updateEpicStatus(parentEpicId);
            return taskId;
        }
    }

    private void updateSubtask(Subtask task) throws ManagerException {
        int subtaskId = task.getId();
        Subtask subtaskInMap = subtasks.get(subtaskId);
        int parentEpicId = task.getParentEpicId();

        if (subtaskInMap == null) {
            throw new ManagerException();  // SubtaskDoesNotExist
        } else if (parentEpicId != subtaskInMap.getParentEpicId()) {
            throw new ManagerException();  // SubtaskHasWrongEpic
        } else {
            subtasks.put(subtaskId, task);
            updateEpicStatus(parentEpicId);
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
