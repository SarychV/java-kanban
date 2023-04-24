public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Managers manager = new Managers();

        makeTasks(manager);
        testHistory(manager);
        System.out.println("\nПервая серия обращений к менеджеру....");
        System.out.println(manager.getDefaultHistory().getHistory());
        testHistory(manager);
        System.out.println("\nВторая серия обращений к менеджеру....");
        System.out.println(manager.getDefaultHistory().getHistory());


        System.out.println();
        TaskManager taskManager= manager.getDefault();

        taskManager.removeAllSubtasks();
        taskManager.removeAllSimpleTasks();
        System.out.println(taskManager);

        taskManager.removeAllEpicTasks();
        System.out.println(taskManager);
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
            simple1.setId(taskManager.addTask(simple1));
            simple2.setId(taskManager.addTask(simple2));
            System.out.println(taskManager);

            SimpleTask simple3 = new SimpleTask(
                    "Замена задачи №2",
                    simple2.getDescription(),
                    simple2.getId(),
                    TaskStatus.IN_PROGRESS);

            taskManager.updateTask(simple3);
            System.out.println(taskManager);

            epic1.setId(taskManager.addTask(epic1));
            epic2.setId(taskManager.addTask(epic2));
            System.out.println(taskManager);

            Subtask sub1 = new Subtask(epic1.getId(), "Первая подзадача эпика №1",
                    "Детальное описание первой подзадачи эпика №1.");
            Subtask sub2 = new Subtask(epic1.getId(), "Вторая подзадача эпика №1",
                    "Детальное описание второй подзадачи эпика №1");
            Subtask sub3 = new Subtask(epic2.getId(), "Единственная подзадача эпика №2",
                    "Очень детальное описание единственной подзадачи эпика №2");

            sub1.setId(taskManager.addTask(sub1));
            sub2.setId(taskManager.addTask(sub2));
            sub3.setId(taskManager.addTask(sub3));
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
