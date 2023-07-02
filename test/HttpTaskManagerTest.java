import manager.HttpTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    { super.mgr = HttpTaskManager.loadFromHttpServer("http://localhost:8078/");}

    @Test
    void checkEmptyManager() {
        mgr.addTask(st1);
        mgr.addTask(sub1);
        mgr.addTask(sub2);
        mgr.removeAllEpicTasks();
        mgr.removeAllSubtasks();
        mgr.removeAllSimpleTasks();

        TaskManager emptyMgr = HttpTaskManager.loadFromHttpServer("http://localhost:8078/");
        assertEquals(0, emptyMgr.getAllSimpleTasks().size());
        assertEquals(0, emptyMgr.getAllEpicTasks().size());
        assertEquals(0, emptyMgr.getAllSubtasks().size());

    }

    @Test
    void checkEpicWithoutSubtasks() {
        mgr.removeAllEpicTasks();
        mgr.addTask(et1);
        TaskManager oneEpicMgr = HttpTaskManager.loadFromHttpServer("http://localhost:8078/");
        assertEquals(0, oneEpicMgr.getAllSimpleTasks().size());
        assertEquals(1, oneEpicMgr.getAllEpicTasks().size());
        assertEquals(0, oneEpicMgr.getAllSubtasks().size());
        mgr.removeAllEpicTasks();
    }

    @Test
    void checkEmptyHistory() {
        TaskManager emptyHistoryMgr = HttpTaskManager.loadFromHttpServer("http://localhost:8078/");
        assertEquals(0, emptyHistoryMgr.getHistory().size());
    }
}
