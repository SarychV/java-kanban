package manager;

import task.*;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.summingInt;

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
        int taskId = -1;
        try {
            if (task == null) {
                throw new NullPointerException("В метод addTaskInMemory() передана null-ссылка.");     // NullArgument
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
                throw new NullPointerException("В метод updateTask() передана null-ссылка.");     // NullArgument
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
                throw new NullPointerException("В метод removeTask() передана null-ссылка.");     // NullArgument
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
            e.printStackTrace();
        }

    }

    @Override
    public void removeAllSimpleTasks() {
        for (Integer id : Set.copyOf(simpleTasks.keySet())) {
            simpleTasks.remove(id);
            historyManager.remove(id);
        }
    }

    protected int addSimpleTask(SimpleTask task) {
        int taskId = task.getId();
        try {
            if (taskOverlapInTime(task)) {
                throw new ManagerException("Подзадача " + task + "имеет пересечение по времени.");
            } else {
                if (taskId == 0) {
                    taskId = TaskId.generate();
                    task.setId(taskId);
                }
                simpleTasks.put(taskId, task);
            }
        } catch (ManagerException e) {
            e.printStackTrace();
            taskId = -1;
        }
        return taskId;
    }

    protected void updateSimpleTask(SimpleTask task) {
        int taskId = task.getId();
        try {
            if (simpleTasks.get(taskId) == null) {
                throw new ManagerException(     // SimpleTaskDoesNotExist
                        "Задача с id=" + taskId + " в менеджере отсутствует. Обновление не выполнено.");
            } else if (taskOverlapInTime(task)) {
                throw new ManagerException("Подзадача " + task + "имеет пересечение по времени.");
            } else {
                simpleTasks.put(taskId, task);
            }
        } catch (ManagerException e) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllEpicTasks() {
        removeAllSubtasks();
        for (int id : Set.copyOf(epicTasks.keySet())) {
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
                updateEpicStatus(taskId);
                updateEpicTimes(taskId);
                //task.setStatus(taskInMap.getStatus());
            }
        } catch (ManagerException e) {
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

    protected void updateEpicTimes(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        if (epic != null) {
            if (epic.getSubtasks().size() == 0) {
                epic.setStartTime(null);
                epic.setDuration(0);
                epic.setEndTime(null);
            } else {
                setEpicTimesBySubtasks(epicId);
            }
        }
    }

    private void setEpicTimesBySubtasks(int epicId) {
        EpicTask epic = epicTasks.get(epicId);
        List<Integer> subs = epic.getSubtasks();

        // найти подзадачу с наименьшим начальным временем, установить startTime эпика
        Optional<LocalDateTime> earliestTime = subs.stream()
                .map(id -> subtasks.get(id).getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (earliestTime.isPresent()) epic.setStartTime(earliestTime.get());
            else epic.setStartTime(null);

        // определить duration эпика
        int duration = subs.stream()
                .map(id -> subtasks.get(id))
                .collect(summingInt(Task::getDuration));
        epic.setDuration(duration);

        // найти подзадачу с наибольшим конечным временем, установить endTime эпика
        Optional<LocalDateTime> lastTime = subs.stream()
                .map(id -> subtasks.get(id))
                .map(subtask -> {
                    LocalDateTime startTime = subtask.getStartTime();
                    if (startTime != null) {
                        return startTime.plusMinutes(subtask.getDuration());
                    } else
                        return null;
                })
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);
        if (lastTime.isPresent()) epic.setEndTime(lastTime.get());
            else epic.setEndTime(null);
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
            updateEpicTimes(epicId);

        } catch (ManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (int subtaskId: Set.copyOf(subtasks.keySet())) {
            removeSubtask(subtaskId);
        }
        for (int epicId: epicTasks.keySet()) {
            updateEpicStatus(epicId);
            updateEpicTimes(epicId);
        }
    }

    protected int addSubtask(Subtask task) {
        int taskId = task.getId();

        int parentEpicId = task.getParentEpicId();
        EpicTask parentEpic = epicTasks.get(parentEpicId);
        try {
            if (parentEpic == null) {       // BadParentEpic
                throw new ManagerException("При добавлении подзадачи определите для нее существующий эпик!");
            } else if (taskOverlapInTime(task)) {
                throw new ManagerException("Подзадача " + task + "имеет пересечение по времени.");
            } else {
                if(taskId == 0) {
                    taskId = TaskId.generate();
                    task.setId(taskId);
                }
                if (subtasks.put(taskId, task) == null) {
                    parentEpic.bindSubtask(taskId);
                    updateEpicStatus(parentEpicId);
                    updateEpicTimes(parentEpicId);
                }
                return taskId;
            }
        } catch (ManagerException e) {
            e.printStackTrace();
            taskId = -1;
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
            } else if (taskOverlapInTime(task)) {
                throw new ManagerException("Подзадача " + task + "имеет пересечение по времени.");
            } else {
                subtasks.put(subtaskId, task);
                updateEpicStatus(parentEpicId);
                updateEpicTimes(parentEpicId);
            }
        } catch (ManagerException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> result = new TreeSet<>(new StartTimeComparator());
        result.addAll(getAllSimpleTasks());
        result.addAll(getAllSubtasks());
        return result;
    }

    private boolean taskOverlapInTime(Task task) {
        if (task == null) return false;
        for (Task taskInManager: getPrioritizedTasks()) {
            if(task.getId() != taskInManager.getId()) {
                if (task.hasTimeIntersection(taskInManager))
                    return true;
            }
        }
        return false;
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

class StartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return Integer.compare(t1.getId(), t2.getId());
        }
        if (t1.getStartTime() == null && t2.getStartTime() != null) return 1;
        if (t1.getStartTime() != null && t2.getStartTime() == null) return -1;
        if (t1.getStartTime().isBefore(t2.getStartTime())) return -1;
        if (t1.getStartTime().isAfter(t2.getStartTime())) return 1;
        return 0;
    }
}
