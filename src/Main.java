public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();

        SimpleTask simple1 = new SimpleTask(
                "Обычная задача №1",
                "Более детальное описание обычной задачи №1");
        SimpleTask simple2 = new SimpleTask(
                "Обычная задача №2",
                "Более детальное описание обычной задачи №2");
        SimpleTask simple3 = new SimpleTask(
                "Замена задачи №2",
                simple2.getDescription(),
                simple2.getId(),
                TaskStatus.IN_PROGRESS);

        manager.addSimpleTask(simple1);
        manager.addSimpleTask(simple2);
        System.out.println(manager);

        manager.updateSimpleTask(simple3);
        System.out.println(manager);

        EpicTask epic1 = new EpicTask(manager, "Эпик №1", "Должен включать две подзадачи.");
        EpicTask epic2 = new EpicTask(manager, "Эпик №2", "Должен включать одну подзадачу.");
        manager.addEpicTask(epic1);
        manager.addEpicTask(epic2);
        System.out.println(manager);

        Subtask sub1 = new Subtask(epic1.getId(), "Первая подзадача эпика №1",
                "Детальное описание первой подзадачи эпика №1.");
        Subtask sub2 = new Subtask(epic1.getId(), "Вторая подзадача эпика №1",
                "Детальное описание второй подзадачи эпика №1");
        Subtask sub3 = new Subtask(epic2.getId(), "Единственная подзадача эпика №2",
                "Очень детальное описание единственной подзадачи эпика №2");
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);
        manager.addSubtask(sub3);
        System.out.println(manager);

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.DONE);
        sub3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        manager.updateSubtask(sub3);
        System.out.println(manager);

        manager.removeSimpleTask(simple2.getId());
        manager.removeEpicTask(epic1.getId());
        System.out.println(manager);

        manager.removeAllSubtasks();
        manager.removeAllSimpleTasks();
        System.out.println(manager);

        manager.removeAllEpicTasks();
        System.out.println(manager);
    }
}
