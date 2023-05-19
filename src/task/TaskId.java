package task;

public class TaskId {
    private static int lastId = 0;
    public static int generate() { return ++lastId; }
}

