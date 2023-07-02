import manager.Managers;
import manager.TaskManager;
import server.KVServer;
import task.EpicTask;
import task.Subtask;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        KVServer kvs = new KVServer();
        kvs.start();
        testFinalSprint5();
        kvs.stop();
    }

    public static void testFinalSprint5() {
        TaskManager taskManager = Managers.getDefault();

        EpicTask epic1 = new EpicTask("Эпик №1", "Будет включать три подзадачи.");
        EpicTask epic2 = new EpicTask("Эпик №2", "Подзадачи отсутствуют.");


        int epic1Id = taskManager.addTask(epic1);
        int epic2Id = taskManager.addTask(epic2);

        Subtask sub1 = new Subtask(epic1Id, "Первая подзадача эпика №1",
                "Детальное описание первой подзадачи эпика №1.");
        Subtask sub2 = new Subtask(epic1Id, "Вторая подзадача эпика №1",
                "Детальное описание второй подзадачи эпика №1");
        Subtask sub3 = new Subtask(epic1Id, "Третья подзадача эпика №1",
                "Очень детальное описание третьей подзадачи эпика №1");
        int sub1Id = taskManager.addTask(sub1);
        int sub2Id = taskManager.addTask(sub2);
        int sub3Id = taskManager.addTask(sub3);

        taskManager.getEpicTask(epic2Id);
        taskManager.getSubtask(sub2Id);
        taskManager.getSubtask(sub1Id);
        taskManager.getEpicTask(epic1Id);
        taskManager.getSubtask(sub3Id);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtask(sub2Id);
        taskManager.getSubtask(sub1Id);
        taskManager.getEpicTask(epic2Id);
        System.out.println(taskManager.getHistory());

        taskManager.getSubtask(sub3Id);
        taskManager.getEpicTask(epic1Id);
        taskManager.removeTask(sub2);
        System.out.println(taskManager.getHistory());

        taskManager.removeTask(epic1);
        System.out.println(taskManager.getHistory());

    }
}

