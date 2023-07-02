package manager;

import task.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Formatter {
    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] ids = new String[history.size()];

        int i = 0;
        for (Task task : history) {
            ids[i] = "" + task.getId();
            i++;
        }
        return String.join(",", ids) + "\n";
    }

    public static List<Integer> historyFromString(String historyLine) {
        List<Integer> tasks = new ArrayList<>();
        String[] ids = historyLine.split(",");
        for (String id : ids) {
            if (!id.isEmpty())
                tasks.add(Integer.parseInt(id));
        }
        return tasks;
    }

    public static String toString(Task task) {
        return String.join(",", parseTaskToFields(task));
    }

    private static String[] parseTaskToFields(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add("" + task.getId());
        fields.add(formTypeField(task));
        fields.add(task.getTitle());
        fields.add(task.getStatus().name());
        fields.add(task.getDescription());

        if (task instanceof Subtask) {
            int epicId = ((Subtask) task).getParentEpicId();
            fields.add("" + epicId);
        } else {
            fields.add("");
        }

        LocalDateTime startTime = task.getStartTime();
        if (startTime != null)
            fields.add(startTime.toString());
        else
            fields.add("");

        fields.add("" + task.getDuration());

        if (task instanceof EpicTask) {
            startTime = task.getEndTime();
            if (startTime != null)
                fields.add(startTime.toString());
            else
                fields.add("");
        }
        return fields.toArray(new String[0]);
    }

    private static String formTypeField(Task task) {
        if (task instanceof SimpleTask) {
            return TaskType.TASK.name();
        } else if (task instanceof EpicTask) {
            return TaskType.EPIC.name();
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK.name();
        } else {
            return "UNDEF";
        }
    }
}
