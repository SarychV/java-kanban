import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import task.Subtask;

import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    { super.mgr = new InMemoryTaskManager(); }

    @Test
    void TasksWithTimesTest() {
        System.out.println(mgr);
        mgr.addTask(sub2);
        System.out.println(mgr);
        mgr.addTask(sub1);
        System.out.println(mgr);
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

        System.out.println(mgr);
        System.out.println(mgr.getPrioritizedTasks());
    }
}
