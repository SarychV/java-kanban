import manager.HttpTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.KVServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    { super.mgr = HttpTaskManager.loadFromHttpServer("http://localhost:8078/");}


    static KVServer kvs;
    /*@BeforeAll @AfterAll требуют статических методов (без static функции с этими аннотациями не запускаются),
     а поскольку методы, где используется kvs, должны быть статическими, то и KVServer kvs
     тоже должен быть статическим:((
    */

    @BeforeAll
    static void start() throws IOException {
        kvs = new KVServer();
        kvs.start();
    }

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

    @AfterAll
    static void stop() {
        kvs.stop();
    }
}
