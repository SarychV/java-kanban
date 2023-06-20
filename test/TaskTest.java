import org.junit.jupiter.api.Test;
import task.SimpleTask;
import task.Subtask;
import task.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    void shouldCreateSimpleTask() {
        SimpleTask obj1 = new SimpleTask("obj", "Description of obj");
        System.out.println(obj1);
        LocalDateTime objTime = obj1.getStartTime();

        SimpleTask obj2 = new SimpleTask("obj", "Description of obj", 0, TaskStatus.NEW);
        obj2.setStartTime(objTime);
        assertEquals(obj1, obj2);

        SimpleTask obj3 = new SimpleTask("obj", "Description of obj", objTime, 0);
        assertEquals(obj1, obj2);
        assertEquals(obj2, obj3);
        assertEquals(obj1, obj3);

        SimpleTask obj4 = new SimpleTask("obj", "Description of obj", 0, TaskStatus.NEW, objTime, 0);
        assertEquals(obj1, obj4);

        SimpleTask obj6 = new SimpleTask("obj", "Description");
        assertNotEquals(obj1, obj6);
    }
    
    @Test
    void shouldCreateSubtask() {
        Subtask obj1 = new Subtask(1, "obj", "Description of obj");
        System.out.println(obj1);
        LocalDateTime objTime = obj1.getStartTime();

        Subtask obj2 = new Subtask(1, "obj", "Description of obj", 0, TaskStatus.NEW);
        obj2.setStartTime(objTime);
        assertEquals(obj1, obj2);

        Subtask obj3 = new Subtask(1, "obj", "Description of obj", objTime, 0);
        assertEquals(obj1, obj2);
        assertEquals(obj2, obj3);
        assertEquals(obj1, obj3);

        Subtask obj4 = new Subtask(1, "obj", "Description of obj", 0, TaskStatus.NEW, objTime, 0);
        assertEquals(obj1, obj4);

        Subtask obj6 = new Subtask(1, "obj", "Description");
        assertNotEquals(obj1, obj6);
    }

    @Test
    void checkTimeIntersection() {
        SimpleTask t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,00), 8);
        SimpleTask t2 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,10), 10);
        assertFalse(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,02), 8);
        assertFalse(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,05), 8);
        assertTrue(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,15), 8);
        assertTrue(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,20), 8);
        assertFalse(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", LocalDateTime.of(2023,6,21,23,21), 8);
        assertFalse(t1.hasTimeIntersection(t2));
        t1 = new SimpleTask("t1", "descr t1", null, 8);
        assertFalse(t1.hasTimeIntersection(t2));
        t2 = new SimpleTask("t1", "descr t1", null, 10);
        assertFalse(t1.hasTimeIntersection(t2));
    }
}
