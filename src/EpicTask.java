import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subtasks;

    public EpicTask(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public List<Integer> getSubtasks() {
        return List.copyOf(subtasks);
    }

    public void bindSubtask(int id) {
        subtasks.add(id);
    }

    public void unbindSubtask(int id) {
        subtasks.remove((Integer)id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{"
                + super.toString()
                + ", subtasks='" + subtasks + "'}";
    }
}
