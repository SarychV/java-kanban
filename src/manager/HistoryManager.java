package manager;

import java.util.ArrayList;
import java.util.List;
import task.*;


public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] ids = new String[history.size()];

        int i = 0;
        for (Task task : history) {
            ids[i] = "" + task.getId();
            i++;
        }
        return String.join(",", ids) + "\n";
    }

    static List<Integer> historyFromString(String historyLine) {
        List<Integer> tasks = new ArrayList<>();
        String[] ids = historyLine.split(",");
        for (String id : ids) {
            tasks.add(Integer.parseInt(id));
        }
        return tasks;
    }

}
