public class Managers {
    TaskManager defaultTaskManager;
    HistoryManager defaultHistoryManager;

    public Managers() {
        defaultHistoryManager = new InMemoryHistoryManager(10);
        defaultTaskManager = new InMemoryTaskManager(defaultHistoryManager);
    }
    public TaskManager getDefault() {
        return defaultTaskManager;
    }

    public HistoryManager getDefaultHistory() {
        return defaultHistoryManager;
    }

}
