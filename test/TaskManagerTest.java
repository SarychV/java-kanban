import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    T mgr;

    SimpleTask st1;
    EpicTask et1;
    Subtask sub1;
    Subtask sub2;
    Subtask sub3;

    @BeforeEach
    void initTasks() {
        st1 = new SimpleTask("st1", "Description st1", LocalDateTime.of(2023, 6, 18, 15, 26), 3);
        et1 = new EpicTask("e1", "Description epic1");
        mgr.addTask(et1);
        sub1 = new Subtask(et1.getId(), "e1s1", "Description e1s1", LocalDateTime.of(2023, 6, 18, 15, 20), 3);
        sub2 = new Subtask(et1.getId(), "e1s2", "Description e1s2", LocalDateTime.of(2023, 6, 18, 15, 30), 5);
        sub3 = new Subtask(et1.getId(), "e1s3", "Description e1s2", LocalDateTime.of(2023, 6, 20, 0, 51), 15);
    }

    @Test
    void shouldAddTask() {
        assertEquals(-1, mgr.addTask(null));

        int simpleId = mgr.addTask(st1);
        assertNotEquals(-1, simpleId);
        // Проверка повторной передачи задачи в менеджер (id задачи уже есть в менеджере).
        simpleId = mgr.addTask(st1);
        assertEquals(st1.getId(), simpleId);

        EpicTask epic = new EpicTask("e2", "Description epic2");
        int epicId = mgr.addTask(epic);
        assertNotEquals(-1, epicId);
        // Проверка повторной передачи задачи в менеджер (id задачи уже есть в менеджере).
        epicId = mgr.addTask(epic);
        assertEquals(epic.getId(), epicId);

        int subId = mgr.addTask(sub1);
        assertNotEquals(-1, subId);
        // Проверка повторной передачи задачи в менеджер (id задачи уже есть в менеджере).
        subId = mgr.addTask(sub1);
        assertEquals(sub1.getId(), subId);

        // Создание подзадачи для несуществующего эпика
        sub2 = new Subtask(100, "sub2", "Description of sub2");
        subId = mgr.addTask(sub2);
        assertEquals(-1, subId);

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    public void shouldGetTask() {
        Task task;

        int simpleId = mgr.addTask(st1);
        SimpleTask simpleFromMgr = mgr.getSimpleTask(simpleId);
        // Проверка getSimpleTask().
        assertEquals(st1, simpleFromMgr, "Получение задачи из менеджера. Обычные задачи не совпадают.");
        // Получение несуществующей задачи.
        simpleFromMgr = mgr.getSimpleTask(1000);
        assertNull(simpleFromMgr);

        // Проверка getTask().
        task = mgr.getTask(simpleId);
        assertEquals(st1, task);

        int epicId = et1.getId();
        EpicTask epicFromMgr = mgr.getEpicTask(epicId);
        // Проверка getEpicTask().
        assertEquals(et1, epicFromMgr, "Получение задачи из менеджера. Эпики не совпадают.");
        // Получение несуществующего эпика.
        epicFromMgr = mgr.getEpicTask(1000);
        assertNull(epicFromMgr);

        // Проверка getTask() на эпике.
        task = mgr.getTask(epicId);
        assertEquals(et1, task);

        int sub1Id = mgr.addTask(sub1);
        Subtask sub1FromMgr = mgr.getSubtask(sub1Id);
        // Проверка getSubtask().
        assertEquals(sub1, sub1FromMgr, "Получение задачи из менеджера. Подзадачи не совпадают.");
        // Получение несуществующей подзадачи.
        sub1FromMgr = mgr.getSubtask(1000);
        assertNull(sub1FromMgr);

        // Проверка getTask() на подзадаче.
        task = mgr.getTask(sub1Id);
        assertEquals(sub1, task);
        assertEquals(3, mgr.getHistory().size());

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    void shouldUpdateTask() {
        int simpleId = mgr.addTask(st1);
        int epicId = et1.getId();
        int sub1Id = mgr.addTask(sub1);
        int sub2Id = mgr.addTask(sub2);

        mgr.updateTask(null);
        SimpleTask simpleFromMgr = mgr.getSimpleTask(simpleId);
        simpleFromMgr.setStatus(TaskStatus.DONE);
        simpleFromMgr.setDescription("Другое описание обычной задачи.");
        mgr.updateTask(simpleFromMgr);
        assertEquals(simpleFromMgr, mgr.getSimpleTask(simpleId));
        // Обновление несуществующей обычной задачи
        SimpleTask nonExistingSimple = new SimpleTask("simple", "Very simple task",
                100, TaskStatus.IN_PROGRESS);
        mgr.updateTask(nonExistingSimple);


        EpicTask epicFromMgr = mgr.getEpicTask(epicId);
        epicFromMgr.setDescription("Другое описание эпика.");
        epicFromMgr.setStatus(TaskStatus.DONE);
        mgr.updateTask(epicFromMgr);
        assertEquals(epicFromMgr, mgr.getEpicTask(epicId));
        // Обновление несуществующего эпика
        EpicTask nonExistingEpic = new EpicTask("nonExistingEpic",
                "Description of nonExistingEpic", 100, TaskStatus.IN_PROGRESS);
        mgr.updateTask(nonExistingEpic);


        Subtask subFromMgr = mgr.getSubtask(sub1Id);
        assertNotNull(mgr.getEpicTask(subFromMgr.getParentEpicId()));
        subFromMgr.setStatus(TaskStatus.DONE);
        mgr.updateTask(subFromMgr);
        assertEquals(subFromMgr, mgr.getSubtask(sub1Id));
        // Проверка статуса эпика после изменения статуса подзадачи
        assertEquals(TaskStatus.IN_PROGRESS,
                mgr.getEpicTask(subFromMgr.getParentEpicId()).getStatus());

        // Обновление несуществующей подзадачи
        Subtask nonExistingSubtask = new Subtask(100, "nonExistingSub",
                "Description of nonExistingSub", 102, TaskStatus.DONE);
        mgr.updateTask(nonExistingSubtask);

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    void reviewContentOfManager() {
        int simpleId = mgr.addTask(st1);
        int sub1Id = mgr.addTask(sub1);
        int sub2Id = mgr.addTask(sub2);

        List<SimpleTask> simples = mgr.getAllSimpleTasks();
        assertEquals(1,simples.size(),"Количество обычных задач в менеджере неверное.");
        List<EpicTask> epics = mgr.getAllEpicTasks();
        assertEquals(1, epics.size(), "Количество эпиков в менеджере задач неверное.");
        List<Subtask> subtasks = mgr.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Количество подзадач в менеджере неверное.");

        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.IN_PROGRESS);
        mgr.updateTask(sub1);
        mgr.updateTask(sub2);
        int subParentId = sub1.getParentEpicId();
        assertEquals(et1.getId(), subParentId, "Подзадача1 имеет неверный родительский эпик.");
        subParentId = sub2.getParentEpicId();
        assertEquals(et1.getId(), subParentId, "Подзадача2 имеет неверный родительский эпик.");
        assertEquals(TaskStatus.IN_PROGRESS, mgr.getEpicTask(subParentId).getStatus(), "Неверный статус эпика.");

        mgr.removeAllSimpleTasks();
        simples = mgr.getAllSimpleTasks();
        assertEquals(0,simples.size(),"Количество обычных задач после удаления в менеджере неверное.");

        mgr.removeAllSubtasks();
        subtasks = mgr.getAllSubtasks();
        assertEquals(0, subtasks.size(), "Количество подзадач после удаления в менеджере неверное.");

        mgr.removeAllEpicTasks();
        epics = mgr.getAllEpicTasks();
        assertEquals(0, epics.size(), "Количество эпиков после удаления в менеджере задач неверное.");

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    public void shouldRemoveTask() {
        int simpleId = mgr.addTask(st1);
        int epicId = et1.getId();
        int sub1Id = mgr.addTask(sub1);
        int sub2Id = mgr.addTask(sub2);

        mgr.removeTask(null);
        mgr.removeTask(st1);
        assertEquals(0, mgr.getAllSimpleTasks().size());
        simpleId = mgr.addTask(st1);
        mgr.removeSimpleTask(simpleId);
        assertEquals(0, mgr.getAllSimpleTasks().size());
        SimpleTask nonExistingSimple = new SimpleTask("simple", "Very simple task",
                100, TaskStatus.IN_PROGRESS);
        mgr.removeTask(nonExistingSimple);

        assertEquals(2, mgr.getAllSubtasks().size());
        mgr.removeTask(et1);
        assertEquals(0, mgr.getAllEpicTasks().size());
        assertEquals(0, mgr.getAllSubtasks().size());
        epicId = mgr.addTask(et1);
        mgr.removeEpicTask(epicId);
        assertEquals(0, mgr.getAllEpicTasks().size());
        EpicTask nonExistingEpic = new EpicTask("epic", "Description of epic task",
                99, TaskStatus.IN_PROGRESS);
        mgr.removeTask(nonExistingEpic);

        epicId = mgr.addTask(et1);
        sub1 = new Subtask(et1.getId(), "Sub1", "Simple subtask1 description.");
        sub2 = new Subtask(et1.getId(), "Sub2", "Simple subtask2 description.");
        sub1Id = mgr.addTask(sub1);
        sub2Id = mgr.addTask(sub2);

        mgr.removeTask(sub1);
        assertEquals(1, mgr.getAllSubtasks().size());
        mgr.removeSubtask(sub2Id);
        assertEquals(0, mgr.getAllSubtasks().size());
        Subtask nonExistingSub = new Subtask(32, "Some sub", "Description of subtask",
                77, TaskStatus.DONE);
        mgr.removeTask(nonExistingSub);

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    void shouldRemoveAllTasks() {
        mgr.addTask(st1);
        mgr.addTask(new SimpleTask("simple2", "Description of second simple task."));
        mgr.addTask(new EpicTask("epic2", "Description of epic 2."));
        mgr.addTask(sub1);
        mgr.addTask(sub2);
        assertEquals(2, mgr.getAllSimpleTasks().size());
        assertEquals(2, mgr.getAllEpicTasks().size());
        assertEquals(2, mgr.getAllSubtasks().size());
        mgr.removeAllSimpleTasks();
        assertEquals(0, mgr.getAllSimpleTasks().size());
        mgr.removeAllEpicTasks();
        assertEquals(0, mgr.getAllEpicTasks().size());
        assertEquals(0, mgr.getAllSubtasks().size());

        mgr.addTask(et1);
        mgr.addTask(sub1);
        mgr.addTask(sub2);
        mgr.removeAllSubtasks();
        assertEquals(0, mgr.getAllSubtasks().size());

        mgr.removeAllSimpleTasks();
        mgr.removeAllEpicTasks();
    }

    @Test
    void shouldGetHistory() {
        int simpleId = mgr.addTask(st1);
        int epicId = et1.getId();
        int sub1Id = mgr.addTask(sub1);
        int sub2Id = mgr.addTask(sub2);

        List<Task> history =  mgr.getHistory();
        assertEquals(0, history.size());

        mgr.getTask(simpleId);
        mgr.getSubtask(sub1Id);
        mgr.getEpicTask(epicId);
        mgr.getSubtask(sub2Id);
        history = mgr.getHistory();
        assertEquals(4, history.size());

        mgr.getSubtask(sub1Id);
        mgr.removeSubtask(sub2Id);
        history = mgr.getHistory();
        assertEquals(3, history.size());
    }

}
