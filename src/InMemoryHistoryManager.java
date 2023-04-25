import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    private final int sizeOfHistory;  // Заданный размер истории задач

    public InMemoryHistoryManager(int size) {
        history = new LinkedList<>();
        this.sizeOfHistory = size;
    }

    @Override
    public void add(Task task) {
        while (history.size() >= sizeOfHistory) {
            history.remove(0);
        }
        history.add(task);
    }
    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

    public static void main(String[] args) {
        InMemoryHistoryManager mgr = new InMemoryHistoryManager(3);
        SimpleTask task1 = new SimpleTask("Simple", "Testing string simple", 1, TaskStatus.NEW);
        mgr.add(task1);
        System.out.println(mgr.getHistory());

        EpicTask task2 = new EpicTask("Epic", "Testing string epic");
        task2.setId(2);
        mgr.add(task2);
        System.out.println(mgr.getHistory());

        Subtask task3 = new Subtask(task2.getId(),"Subtask", "Testing string subtask",
                3, TaskStatus.DONE);
        mgr.add(task3);
        System.out.println(mgr.getHistory());

        mgr.add(task3);
        System.out.println(mgr.getHistory());

        mgr.add(task1);
        System.out.println(mgr.getHistory());
    }

}
