package manager;

import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;

    public FileBackedTaskManager(String filename) {
        this.filename = filename;
    }

    static public FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toString());
        boolean whileTaskRecords = true;

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            lines.remove(0);  // Remove header.

            for (String line : lines) {
                if (whileTaskRecords) {
                    if (!line.isBlank()) {
                        manager.loadTask(line);
                    } else {
                        whileTaskRecords = false;
                    }
                } else {
                    manager.loadHistory(line);
                    break;
                }
            }
            manager.prioritizedTasks.addAll(manager.getAllSimpleTasks());
            manager.prioritizedTasks.addAll(manager.getAllSubtasks());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    void loadTask(String lineFromFile) {
        String[] fields = lineFromFile.split(",");
        Task task;
        try {
            if (fields.length > countFieldsInFile()) {
                throw new ManagerException("Количество сохраненных полей задачи  превышает допустимое!");
            } else {
                task = newTask(fields);
                addTaskInMemory(task);
            }
        } catch (ManagerException e) {
            e.printStackTrace();
        }
    }

    private int countFieldsInFile() {
        return header().strip().split(",").length;
    }

    private Task newTask(String[] params) throws ManagerException {
        Task task;
        try {
            TaskType type = TaskType.valueOf(params[1]);
            TaskStatus status = TaskStatus.valueOf(params[3]);
            LocalDateTime start;
            if (params[6].isEmpty()) start = null;
                else start = LocalDateTime.parse(params[6]);

            switch (type) {
                case TASK:
                    task = new SimpleTask(params[2], params[4], Integer.parseInt(params[0]), status,
                            start, Integer.parseInt(params[7]));
                    break;

                case EPIC:
                    LocalDateTime end;
                    if (params.length == countFieldsInFile()) {
                        end = LocalDateTime.parse(params[8]);
                    } else {
                        end = null;
                    }
                    task = new EpicTask(params[2], params[4], Integer.parseInt(params[0]), status,
                            start, Integer.parseInt(params[7]), end);
                    break;

                case SUBTASK:
                    task = new Subtask(Integer.parseInt(params[5]), params[2],
                            params[4], Integer.parseInt(params[0]), status, start, Integer.parseInt(params[7]));
                    break;

                default:
                    task = null;
            }
        } catch (IllegalArgumentException e) {
            throw new ManagerException("В сохраненных данных неверный тип или статус задачи!");
        }
        return task;
    }

    void loadHistory(String line) {
        List<Integer> taskIds = Formatter.historyFromString(line);
        for (int id : taskIds) {
            getTaskInMemory(id);
        }
    }

    public void save() {
        try (Writer file = new FileWriter(filename)) {
            file.write(this.header());
            file.write(this.tasks());
            file.write(this.blank());
            file.write(Formatter.historyToString(this.historyManager));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String header() {
        return "id,type,name,status,description,epic,start,duration,end\n";
    }

    String tasks() {
        StringBuilder result = new StringBuilder();
        List<Task> tasks = new LinkedList<>();

        tasks.addAll(getAllSimpleTasks());
        tasks.addAll(getAllEpicTasks());
        tasks.addAll(getAllSubtasks());

        for (Task task : tasks) {
            result.append(Formatter.toString(task));
            result.append("\n");
        }
        return result.toString();
    }

    String blank() {
        return "\n";
    }


    @Override
    public int addTask(Task task) {
        int taskId = super.addTask(task);
        save();
        return taskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(Task task) {
        super.removeTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SimpleTask getSimpleTask(int id) {
        SimpleTask temp = super.getSimpleTask(id);
        save();
        return temp;
    }

    @Override
    public void removeSimpleTask(int id) {
        super.removeSimpleTask(id);
        save();
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
        save();
    }

    @Override
    protected int addSimpleTask(SimpleTask task) {
        int taskId = super.addSimpleTask(task);
        save();
        return taskId;
    }

    @Override
    protected void updateSimpleTask (SimpleTask task) {
        super.updateSimpleTask(task);
        save();
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask temp = super.getEpicTask(id);
        save();
        return temp;
    }

    @Override
    public void removeEpicTask(int id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        /* Реализовано через вызовы методов родительского класса, чтобы не сохранять в файл
         * состояние менеджера до окончания удаления всех требуемых задач (подзадач).
         */
        super.removeAllSubtasks();
        for (int id : Set.copyOf(epicTasks.keySet())) {
            super.removeEpicTask(id);
        }
        save();
    }

    @Override
    protected int addEpicTask(EpicTask task) {
        int taskId = super.addEpicTask(task);
        save();
        return taskId;
    }

    @Override
    protected void updateEpicTask(EpicTask task) {
        super.updateEpicTask(task);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask temp = super.getSubtask(id);
        save();
        return temp;
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        /* Реализовано через вызовы методов родительского класса, чтобы не сохранять в файл
         * состояние менеджера до окончания удаления всех требуемых задач (подзадач).
         */
        for (int subtaskId: Set.copyOf(subtasks.keySet())) {
            super.removeSubtask(subtaskId);
        }
        for (int epicId: epicTasks.keySet()) {
            super.updateEpicStatus(epicId);
        }
        save();
    }

    @Override
    protected int addSubtask(Subtask task) {
        int taskId = super.addSubtask(task);
        save();
        return taskId;
    }

    @Override
    protected void updateSubtask(Subtask task) {
        super.updateSubtask(task);
        save();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}