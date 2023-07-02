package manager;

public class Managers {
    public static TaskManager getDefault() {
        return HttpTaskManager.loadFromHttpServer("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
