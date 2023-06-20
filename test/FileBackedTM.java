import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTM {
    TaskManager mgrA = new FileBackedTaskManager("tasks.csv");
    TaskManager mgrB;

    @Test
    void recordingTest() {
        SimpleTask simple = new SimpleTask("simple title","simple description", LocalDateTime.of(2023,6,18,20,02), 10);
        mgrA.addTask(simple);
        EpicTask epic = new EpicTask("epic title", "epic description");
        mgrA.addTask(epic);
        Subtask suba = new Subtask(epic.getId(), "suba", "suba description", LocalDateTime.of(2023,06,18,21,41), 15);
        Subtask sube = new Subtask(epic.getId(), "sube", "sube description", LocalDateTime.of(2023,06,17,21,00), 8);
        mgrA.addTask(suba);
        mgrA.addTask(sube);
    }

    @Test
    void readingTest() {
        recordingTest();
        mgrB = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        assertEquals(mgrA, mgrB);
    }
}
