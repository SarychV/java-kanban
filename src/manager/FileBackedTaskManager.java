package manager;

import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    private void loadTask(String lineFromFile) {
        String[] fields = lineFromFile.split(",");
        Task task;
        try {
            if (fields.length > countFieldsInFile()) {
                throw new ManagerException("Количество полей задачи в файле превышает допустимое!");
            } else {
                task = newTask(fields);
                addTaskInMemory(task);
            }
        } catch (ManagerException e) {
            System.err.println(e.getMessage());
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

            switch (type) {
                case TASK:
                    task = new SimpleTask(params[2], params[4], Integer.parseInt(params[0]), status);
                    break;
                case EPIC:
                    task = new EpicTask(params[2], params[4], Integer.parseInt(params[0]), status);
                    break;
                case SUBTASK:
                    task = new Subtask(Integer.parseInt(params[5]), params[2],
                                        params[4], Integer.parseInt(params[0]), status);
                    break;
                default:
                    task = null;
            }
        } catch (IllegalArgumentException e) {
            throw new ManagerException("В файле неверный тип или статус задачи!");
        }
        return task;
    }

    private void loadHistory(String line) {
        List<Integer> taskIds = HistoryManager.historyFromString(line);
        for (int id : taskIds) {
            getTaskInMemory(id);
        }
    }

    public void save() {
        try (Writer file = new FileWriter(filename)) {
            file.write(this.header());
            file.write(this.tasks());
            file.write(this.blank());
            file.write(HistoryManager.historyToString(this.historyManager));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String header() {
        return "id,type,name,status,description,epic\n";
    }

    private String tasks() {
        StringBuilder result = new StringBuilder();
        List<Task> tasks = new LinkedList<>();

        tasks.addAll(getAllSimpleTasks());
        tasks.addAll(getAllEpicTasks());
        tasks.addAll(getAllSubtasks());

        for (Task task : tasks) {
            result.append(toString(task));
            result.append("\n");
        }
        return result.toString();
    }

    private String toString(Task task) {
        return String.join(",", parseTaskToFields(task));
    }

    private String[] parseTaskToFields(Task task) {
        int i = 0;
        List<String> fields = new ArrayList<>();
        fields.add(i++, "" + task.getId());
        fields.add(i++, formTypeField(task));
        fields.add(i++, task.getTitle());
        fields.add(i++, task.getStatus().name());
        fields.add(i, task.getDescription());
        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getParentEpicId();
            fields.add("" + epicId);
        }
        return fields.toArray(new String[0]);
    }

    private String formTypeField(Task task) {
        if (task instanceof SimpleTask) {
            return TaskType.TASK.name();
        } else if (task instanceof EpicTask) {
            return TaskType.EPIC.name();
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK.name();
        } else {
            return "UNDEFINED";
        }
    }

    private String blank() {
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
        for (int id : epicTasks.keySet()) {
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
        for (int subtaskId: subtasks.keySet()) {
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

    public static void main(String... unused) {
        String filename = "tasklist.csv";
        FileBackedTaskManager mgrA = new FileBackedTaskManager(filename);
        SimpleTask st1 = new SimpleTask("T1", "Simple task 1");
        SimpleTask st2 = new SimpleTask("T2", "Simple task 2");

        st1.setId(mgrA.addTask(st1));
        st2.setId(mgrA.addTask(st2));
        mgrA.getSimpleTask(st1.getId());
        mgrA.getTask(st2.getId());
        mgrA.getTask(st1.getId());
        st2.setStatus(TaskStatus.DONE);
        mgrA.updateTask(st2);
        System.out.println(mgrA);
        System.out.println(mgrA.getHistory());

        //,EPIC,Epic2,DONE,Description epic2,
        //,SUBTASK,Sub Task2,DONE,Description sub task3,2
        EpicTask et1 = new EpicTask("Epic2", "Description epic2");
        et1.setId(mgrA.addTask(et1));
        Subtask sub1 = new Subtask(et1.getId(), "Sub Task2", "Description sub task3");
        sub1.setId(mgrA.addTask(sub1));
        System.out.println(mgrA.getHistory());
        sub1.setStatus(TaskStatus.DONE);
        mgrA.updateTask(sub1);
        mgrA.getSubtask(sub1.getId());
        System.out.println(mgrA);
        System.out.println(mgrA.getHistory());

        File file = new File(filename);
        FileBackedTaskManager mgrB = loadFromFile(file);
        System.out.println("Manager B______\n" + mgrB);
        System.out.println(mgrB.getHistory());
    }
}