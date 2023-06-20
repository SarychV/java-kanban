import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.Subtask;
import task.TaskStatus;


public class EpicStatusTest {
    InMemoryTaskManager mgr = new InMemoryTaskManager();
    EpicTask et1 = new EpicTask("e1", "Description epic1");
    Subtask sub1;
    Subtask sub2;
    Subtask sub3;
    {
        mgr.addTask(et1);
        sub1 = new Subtask(et1.getId(), "e1s1", "Description e1s1");
        sub2 = new Subtask(et1.getId(), "e1s2", "Description e1s2");
        sub3 = new Subtask(et1.getId(), "e1s3", "Description e1s3");
    }

    @Test
    public void subtaskStatusTests() {
        // a. Пустой список подзадач.
        Assertions.assertEquals(TaskStatus.NEW, mgr.getEpicTask(et1.getId()).getStatus());

        mgr.addTask(sub1);
        mgr.addTask(sub2);
        mgr.addTask(sub3);

        // b. Все подзадачи со статусом NEW.
        sub1.setStatus(TaskStatus.NEW);
        sub2.setStatus(TaskStatus.NEW);
        sub3.setStatus(TaskStatus.NEW);
        updateSubtasksInManager();
        Assertions.assertEquals(TaskStatus.NEW, mgr.getEpicTask(et1.getId()).getStatus());

        // c. Все подзадачи со статусом DONE.
        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        sub3.setStatus(TaskStatus.DONE);
        updateSubtasksInManager();
        Assertions.assertEquals(TaskStatus.DONE, mgr.getEpicTask(et1.getId()).getStatus());

        // d. Подзадачи со статусами NEW и DONE.
        sub1.setStatus(TaskStatus.DONE);
        sub2.setStatus(TaskStatus.DONE);
        sub3.setStatus(TaskStatus.NEW);
        updateSubtasksInManager();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, mgr.getEpicTask(et1.getId()).getStatus());

        // e. Подзадачи со статусом IN_PROGRESS.
        sub1.setStatus(TaskStatus.IN_PROGRESS);
        sub2.setStatus(TaskStatus.IN_PROGRESS);
        sub3.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtasksInManager();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, mgr.getEpicTask(et1.getId()).getStatus());

    }

    protected void updateSubtasksInManager() {
        mgr.updateTask(sub1);
        mgr.updateTask(sub2);
        mgr.updateTask(sub3);
    }
}