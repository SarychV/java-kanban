package manager;

import server.KVServer;
import server.KVTaskClient;
import task.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvsClient;
    private final String serverUrl;

    public HttpTaskManager(String serverUrl) {
        super(serverUrl);
        this.serverUrl = serverUrl;
        kvsClient = new KVTaskClient(this.serverUrl);
    }

    static public HttpTaskManager loadFromHttpServer(String serverUrl) {
        HttpTaskManager manager = new HttpTaskManager(serverUrl);
        boolean whileTaskRecords = true;

        List<String> lines;
        try {
            String content = manager.kvsClient.load("content");
            lines = Arrays.stream(content.split("\n"))
                    .collect(Collectors.toList());
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public void save() {
        try {
            String saveContent = this.header() + this.tasks() + this.blank()
                    + Formatter.historyToString(this.historyManager);
            kvsClient.put("content", saveContent);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public static void main(String... unused) throws IOException {
        KVServer kvs = new KVServer();
        kvs.start();

        String filename = "http://localhost:8078/";
        HttpTaskManager mgrA = new HttpTaskManager(filename);
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

        HttpTaskManager mgrB = loadFromHttpServer(filename);

        SimpleTask st1Cp1 = new SimpleTask("T1", "Simple task 1", st1.getId(), st1.getStatus());
        SimpleTask st1Cp2 = new SimpleTask("T1", "Simple task 1", st1.getId(), st1.getStatus());
        System.out.println("st1 == st1: " + (st1 == st1));
        System.out.println("st1 == st2: " + (st1 == st2));
        System.out.println("st1 == st1Cp1: " + (st1 == st1Cp1));
        System.out.println("st1 == st1Cp2: " + (st1 == st1Cp2));
        System.out.println("st1Cp1 == st1Cp2: " + (st1Cp1 == st1Cp2));
        System.out.println("st1Cp2 == st1Cp1: " + (st1Cp2 == st1Cp1));
        System.out.println();
        System.out.println("st1.equals(st1): " + (st1.equals(st1)));
        System.out.println("st1.equals(st2): " + (st1.equals(st2)));
        System.out.println("st1.equals(st1Cp1): " + (st1.equals(st1Cp1)));
        System.out.println("st1.equals(st1Cp2): " + (st1.equals(st1Cp2)));
        System.out.println("st1Cp1.equals(st1Cp2): " + (st1Cp1.equals(st1Cp2)));
        System.out.println("st1Cp2.equals(st1Cp1): " + (st1Cp2.equals(st1Cp1)));
        System.out.println("\n");

        // System.out.println(et1);
        // et1.unbindSubtask(sub1.getId());
        // System.out.println(et1);
        // Эпик et1 содержит подзадачу, поэтому он не равен эпикам et1Cp1 и et1Cp2, которые равны между собой.
        // Если удалить первые три закомментированные строки, то все эпики станут равны.
        EpicTask et1Cp1 = new EpicTask("Epic2", "Description epic2", et1.getId(), et1.getStatus());
        EpicTask et1Cp2 = new EpicTask("Epic2", "Description epic2", et1.getId(), et1.getStatus());
        System.out.println("et1 == et1: " + (et1 == et1));
        System.out.println("et1 == et1Cp1: " + (et1 == et1Cp1));
        System.out.println("et1 == et1Cp2: " + (et1 == et1Cp2));
        System.out.println("et1Cp1 == et1Cp2: " + (et1Cp1 == et1Cp2));
        System.out.println("et1Cp2 == et1Cp1: " + (et1Cp2 == et1Cp1));
        System.out.println();
        System.out.println("et1.equals(et1): " + (et1.equals(et1)));
        System.out.println("et1.equals(et1Cp1): " + (et1.equals(et1Cp1)));
        System.out.println("et1.equals(et1Cp2): " + (et1.equals(et1Cp2)));
        System.out.println("et1Cp1.equals(et1Cp2): " + (et1Cp1.equals(et1Cp2)));
        System.out.println("et1Cp2.equals(et1Cp10: " + (et1Cp2.equals(et1Cp1)));
        System.out.println("\n");

        Subtask sub1Cp1 = new Subtask(et1.getId(), "Sub Task2", "Description sub task3",
                sub1.getId(), sub1.getStatus());
        Subtask sub1Cp2 = new Subtask(et1.getId(), "Sub Task2", "Description sub task3",
                sub1.getId(), sub1.getStatus());

        System.out.println("sub1 == sub1: " + (sub1 == sub1));
        System.out.println("sub1 == sub1Cp1: " + (sub1 == sub1Cp1));
        System.out.println("sub1 == sub1Cp2: " + (sub1 == sub1Cp2));
        System.out.println("sub1Cp1 == sub1Cp2: " + (sub1Cp1 == sub1Cp2));
        System.out.println("sub1Cp2 == sub1Cp1: " + (sub1Cp2 == sub1Cp1));
        System.out.println();
        System.out.println("sub1.equals(sub1): " + (sub1.equals(sub1)));
        System.out.println("sub1.equals(sub1Cp1): " + (sub1.equals(sub1Cp1)));
        System.out.println("sub1.equals(sub1Cp2): " + (sub1.equals(sub1Cp2)));
        System.out.println("sub1Cp1.equals(sub1Cp2): " + (sub1Cp1.equals(sub1Cp2)));
        System.out.println("sub1Cp2.equals(sub1Cp10: " + (sub1Cp2.equals(sub1Cp1)));
        System.out.println("\n");

        System.out.println("Manager B*********************\n" + mgrB);
        System.out.println("Менеджер прочитался правильно: " + mgrA.equals(mgrB));
        System.out.println("История прочиталась правильно: " + mgrB.getHistory().equals(mgrA.getHistory()));

        kvs.stop();
    }
}

