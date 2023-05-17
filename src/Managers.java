public class Managers {
    TaskManager defaultTaskManager;
    HistoryManager defaultHistoryManager;

    public Managers() {
        defaultHistoryManager = new InMemoryHistoryManager();
        defaultTaskManager = new InMemoryTaskManager(defaultHistoryManager);
    }
    public TaskManager getDefault() {
        return defaultTaskManager;
    }

    public HistoryManager getDefaultHistory() {
        return defaultHistoryManager;
    }

}
