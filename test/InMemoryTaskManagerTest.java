import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import task.Subtask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    { super.mgr = new InMemoryTaskManager(); }

    @Test
    void TasksWithTimesTest() {
        assertNull(et1.getStartTime());
        mgr.addTask(sub2);
        assertEquals(et1.getStartTime(), sub2.getStartTime());
        mgr.addTask(sub1);
        assertEquals(et1.getStartTime(), sub1.getStartTime());
    }

    @Test
    void shouldMakePrioritizedTasks() {
        mgr.addTask(sub2);
        mgr.addTask(sub1);
        mgr.addTask(sub3);
        sub3 = new Subtask(et1.getId(), "e1s4", "Description e1s4", null, 15);
        mgr.addTask(sub3);
        sub3 = new Subtask(et1.getId(), "e1s5", "Description e1s5", null, 15);
        mgr.addTask(sub3);
        sub3 = new Subtask(et1.getId(), "e1s6", "Description e1s6", LocalDateTime.of(2023, 6, 21, 4, 51), 15);
        mgr.addTask(sub3);

        assertEquals(6, mgr.getPrioritizedTasks().size());
    }
}
