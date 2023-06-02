package manager;

import java.util.List;
import task.*;


public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();

}
