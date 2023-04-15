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
    // Возвращает null, если задача добавлена или аргументом метода является null.
    // Возвращает SimpleTask-объект, если задача с таким id уже имеется.
    public SimpleTask addSimpleTask(SimpleTask task) {
        int taskId;
        SimpleTask taskInMap;

        if (task != null) {
            taskId = task.getId();
            taskInMap = simpleTasks.get(taskId);
            if (taskInMap != null) {
                return taskInMap;  // Потенциальное TaskExists();
            } else {
                return simpleTasks.put(taskId, task);
            }
        }
        return null;  // Потенциальное throw new NullArgument();
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

    // Возвращает задачу, которая была в списке и была заменена.
    // Возвращает null, если задачи нет в списке (новая версия задачи при этом не добавляется)
    // или если в качестве аргумента передана пустая ссылка
    public SimpleTask updateSimpleTask(SimpleTask task) {
        int taskId;
        SimpleTask taskInMap;

        if (task != null) {
            taskId = task.getId();
            taskInMap = simpleTasks.get(taskId);
            if (taskInMap == null) {
                return null;
            } else {
                simpleTasks.put(taskId, task);
                return taskInMap;
            }
        }
        return null;
    }


// ********************************************************** Методы для работы с задачами класса EpicTask.
    // Возвращает null, если эпик добавлен или аргументом метода является null.
    // Возвращает ссылку эпика из списка, если эпик с таким id уже имеется.
    // Возвращает эпик-аргумент, если он содержит подзадачи (эпик при добавлении должен быть пустым).
    public EpicTask addEpicTask(EpicTask task) {
        int taskId;
        EpicTask taskInMap;

        if (task != null) {
            taskId = task.getId();
            taskInMap = epicTasks.get(taskId);

            if (taskInMap != null) {
                return taskInMap;  // потенциальное throw new TaskExists();
            } else {
                // Список подзадач нового эпика должен быть пустым
                if (task.getSubtasks().size() == 0) {
                    return epicTasks.put(taskId, task); // Возвращает null
                } else {
                    return task;  // потенциальное throw new BadTaskData();
                }
            }
        }
        return null;    // потенциальное throw new NullArgument();
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.get(id);
    }

    public List<Integer> getAllEpicTasksIds() {
        return List.copyOf(epicTasks.keySet());
    }

    public EpicTask removeEpicTask(int id) {
        EpicTask epic = epicTasks.get(id);
        if (epic != null) {
            for (int subtaskId: epic.getSubtasks()) {
                removeSubtask(subtaskId);
            }
            return epicTasks.remove(id);
        }
        return null;
    }

    public void removeAllEpicTasks() {
        subtasks.clear();
        epicTasks.clear();
    }

    // Возвращает ссылку на задачу, которая была в списке и была заменена, или
    // ссылку на саму задачу, если ее подзадачи не совпадают с подзадачами задачи из списка.
    // Возвращает null, если задача отсутствует в списке (обновление задачи при этом не выполняется)
    // или если в качестве аргумента передана пустая ссылка
    public EpicTask updateEpicTask(EpicTask task) {
        int taskId;
        EpicTask taskInMap;

        if (task != null) {
            taskId = task.getId();
            taskInMap = epicTasks.get(taskId);

            if (taskInMap == null) {
                return null;
            } else {
                if (task.getSubtasks().equals(taskInMap.getSubtasks())) {
                    return epicTasks.put(taskId, task);
                } else {
                    return task;
                }
            }
        }
        return null;
    }

// ********************************************************** Методы для работы с задачами класса Subtask.
    // Возвращает null, если подзадача добавлена в список или аргументом метода является null.
    // Возвращает ссылку подзадачи из списка, если подзадача с таким id уже имеется.
    // Возвращает подзадачу-аргумент, если используется некорректный id родительского эпика.
    public Subtask addSubtask(Subtask task) {
        Subtask taskInMap;
        EpicTask parentEpic;
        if (task != null) {
            int taskId = task.getId();
            taskInMap = subtasks.get(taskId);

            int epicId = task.getParentEpicId();
            parentEpic = epicTasks.get(epicId);

            if (taskInMap != null) {
                return taskInMap;   // Потенциальный throw new TaskExists();
            } else {
                if (parentEpic == null) {
                    return task;   // Потенциальный throw new BadTaskData();
                } else {
                    parentEpic.bindSubtask(taskId);
                    subtasks.put(taskId, task);
                    parentEpic.updateStatus();
                    return null;
                }
            }
        }
        return null;  // Потенциальный throw new NullArgument();
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public List<Integer> getAllSubtaskIds() {
        return List.copyOf(subtasks.keySet());
    }

    public Subtask removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        EpicTask epic;

        if (subtask != null) {
            epic = epicTasks.get(subtask.getParentEpicId());
            epic.unbindSubtask(id);
            subtask = subtasks.remove(id);
            epic.updateStatus();
            return subtask;
        }
        return null;
    }

    public void removeAllSubtasks() {
        for (int subtaskId: getAllSubtaskIds()) {
            removeSubtask(subtaskId);
        }
        for (int epicId: getAllEpicTasksIds()) {
            epicTasks.get(epicId).updateStatus();
        }
    }


    // Возвращает ссылку на подзадачу, которая была в списке и была заменена.
    // Возвращает ссылку на саму подзадачу, если ее родительский эпик отличается от родительского эпика подзадачи
    // из списка (обновление подзадачи в этом случае не выполняется).
    // Возвращает null, если подзадачи нет в списке (обновление подзадачи при этом не выполняется)
    // или если в качестве аргумента передана пустая ссылка.
    public Subtask updateSubtask(Subtask subtask) {
        int subtaskId;
        Subtask subtaskInMap;
        Subtask result;

        if (subtask != null) {
            subtaskId = subtask.getId();
            subtaskInMap = subtasks.get(subtaskId);

            if (subtaskInMap == null) {
                return null;
            } else {
                if (subtask.getParentEpicId() == subtaskInMap.getParentEpicId()) {
                    result = subtasks.put(subtaskId, subtask);
                    epicTasks.get(subtask.getParentEpicId()).updateStatus();
                    return result;
                } else {
                    return subtask;
                }
            }
        }
        return null;
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
