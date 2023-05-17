public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Managers manager = new Managers();
        testFinalSprint5(manager);
    }

    public static void testFinalSprint5(Managers manager) {
        TaskManager taskManager = manager.getDefault();
        HistoryManager history = manager.getDefaultHistory();

        EpicTask epic1 = new EpicTask("Эпик №1", "Будет включать три подзадачи.");
        EpicTask epic2 = new EpicTask("Эпик №2", "Подзадачи отсутствуют.");

        try {
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
            System.out.println(history.getHistory());

            taskManager.getSubtask(sub2Id);
            taskManager.getSubtask(sub1Id);
            taskManager.getEpicTask(epic2Id);
            System.out.println(history.getHistory());

            taskManager.getSubtask(sub3Id);
            taskManager.getEpicTask(epic1Id);
            taskManager.removeSubtask(sub2Id);
            System.out.println(history.getHistory());

            taskManager.removeEpicTask(epic1Id);
            System.out.println(history.getHistory());

        } catch (Exception e) {
            System.err.println("Перехват исключения в методе testFinalSprint5().");
        }
    }

    public static void makeTasks(Managers manager) {
        TaskManager taskManager = manager.getDefault();

        SimpleTask simple1 = new SimpleTask(
                "Обычная задача №1",
                "Более детальное описание обычной задачи №1");
        SimpleTask simple2 = new SimpleTask(
                "Обычная задача №2",
                "Более детальное описание обычной задачи №2");

        EpicTask epic1 = new EpicTask("Эпик №1", "Должен включать две подзадачи.");
        EpicTask epic2 = new EpicTask("Эпик №2", "Должен включать одну подзадачу.");

        try {
            taskManager.addTask(simple1);
            taskManager.addTask(simple2);
            System.out.println(taskManager);

            SimpleTask simple3 = new SimpleTask(
                    "Замена задачи №2",
                    simple2.getDescription(),
                    simple2.getId(),
                    TaskStatus.IN_PROGRESS);

            taskManager.updateTask(simple3);
            System.out.println(taskManager);

            taskManager.addTask(epic1);
            taskManager.addTask(epic2);
            System.out.println(taskManager);

            Subtask sub1 = new Subtask(epic1.getId(), "Первая подзадача эпика №1",
                    "Детальное описание первой подзадачи эпика №1.");
            Subtask sub2 = new Subtask(epic1.getId(), "Вторая подзадача эпика №1",
                    "Детальное описание второй подзадачи эпика №1");
            Subtask sub3 = new Subtask(epic2.getId(), "Единственная подзадача эпика №2",
                    "Очень детальное описание единственной подзадачи эпика №2");

            taskManager.addTask(sub1);
            taskManager.addTask(sub2);
            taskManager.addTask(sub3);
            System.out.println(taskManager);

            sub1.setStatus(TaskStatus.IN_PROGRESS);
            sub2.setStatus(TaskStatus.DONE);
            sub3.setStatus(TaskStatus.DONE);
            taskManager.updateTask(sub1);
            taskManager.updateTask(sub2);
            taskManager.updateTask(sub3);
            System.out.println(taskManager);
        } catch (ManagerException e) {
            System.err.println("Необходимо ловить сообщения с определением метода-генератора исключения.");
        }
    }

    public static void testHistory(Managers manager) {
        TaskManager taskManager = manager.getDefault();
        for (int taskId: taskManager.getAllSimpleTaskIds()) {
            taskManager.getSimpleTask(taskId);
        }
        for (int taskId: taskManager.getAllEpicTasksIds()) {
            taskManager.getEpicTask(taskId);
        }
        for (int taskId: taskManager.getAllSubtaskIds()) {
            taskManager.getSubtask(taskId);
        }
    }
}

