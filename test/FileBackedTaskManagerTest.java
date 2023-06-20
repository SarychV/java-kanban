import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    { super.mgr = FileBackedTaskManager.loadFromFile(new File("testTaskList.csv"));}


    @Test
    void checkEmptyManager() {
        mgr.addTask(st1);
        mgr.addTask(sub1);
        mgr.addTask(sub2);
        mgr.removeAllEpicTasks();
        mgr.removeAllSubtasks();
        mgr.removeAllSimpleTasks();

        TaskManager emptyMgr = FileBackedTaskManager.loadFromFile(new File("testTaskList.csv"));
        assertEquals(0, emptyMgr.getAllSimpleTasks().size());
        assertEquals(0, emptyMgr.getAllEpicTasks().size());
        assertEquals(0, emptyMgr.getAllSubtasks().size());

    }

    @Test
    void checkEpicWithoutSubtasks() {
        mgr.removeAllEpicTasks();
        mgr.addTask(et1);
        TaskManager oneEpicMgr = FileBackedTaskManager.loadFromFile(new File("testTaskList.csv"));
        assertEquals(0, oneEpicMgr.getAllSimpleTasks().size());
        assertEquals(1, oneEpicMgr.getAllEpicTasks().size());
        assertEquals(0, oneEpicMgr.getAllSubtasks().size());
        mgr.removeAllEpicTasks();
    }

    @Test
    void checkEmptyHistory() {
        TaskManager emptyHistoryMgr = FileBackedTaskManager.loadFromFile(new File("testTaskList.csv"));
        assertEquals(0, emptyHistoryMgr.getHistory().size());
    }


}
