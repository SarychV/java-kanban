import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import task.SimpleTask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {
    @Test
    void checkEmptyHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void checkDuplication() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        SimpleTask task = new SimpleTask("Simple task", "Description", 1, TaskStatus.DONE);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void checkRemoving() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> tasks;
        SimpleTask task1 = new SimpleTask("Simple task1", "Description1", 1,TaskStatus.DONE);
        SimpleTask task2 = new SimpleTask("Simple task2", "Description2", 2,TaskStatus.NEW);
        SimpleTask task3 = new SimpleTask("Simple task3", "Description3", 3,TaskStatus.IN_PROGRESS);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);
        tasks = List.of(task2, task3, task1);
        assertEquals(tasks, historyManager.getHistory());

        historyManager.remove(task2.getId());
        tasks = List.of(task3, task1);
        assertEquals(tasks, historyManager.getHistory());

        historyManager.remove(task1.getId());
        tasks = List.of(task3);
        assertEquals(tasks, historyManager.getHistory());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        tasks = List.of(task3, task2);
        assertEquals(tasks, historyManager.getHistory());
    }
}
