import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeTaskIfInHistory(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    private static class CustomLinkedList {
        Node head;
        Node tail;

        // Для быстрого поиска задач сделаем хеш.
        private final Map<Integer, Node> nodeCatalog = new HashMap<>();

        public void linkLast(Task task) {
            // Получить идентификатор задачи.
            int taskId;
            Node newNode;
            if (task == null) return;
                else taskId = task.getId();

            if (head == null) {   // Список пуст
                newNode = new Node(null, task, null);
                nodeCatalog.put(taskId, newNode);
                head = newNode;
                tail = newNode;
            } else {
                removeTaskIfInHistory(taskId);
                // Добавить в список последний просмотр задачи.
                newNode = new Node(tail, task, null);
                tail.next = newNode;    // Привязали новый узел к хвосту.
                tail = newNode;         // Сделали новый узел хвостом.
                nodeCatalog.put(taskId, newNode);
            }
        }

        public List<Task> getTasks() {
            List<Task> list = new ArrayList<>();

            if (head != null) {
                Node hasNode = head;
                while (hasNode != null) {
                    list.add(hasNode.item);
                    hasNode = hasNode.next;
                }
            }
            return list;
        }

        public void removeTaskIfInHistory(int taskId) {
            if (nodeCatalog.containsKey(taskId)) {
                // Убрать из списка предыдущий просмотр задачи.
                removeNode(nodeCatalog.get(taskId));
                nodeCatalog.remove(taskId);
            }
        }

        private void removeNode(Node node) {
            Node previous;
            Node next;

            if (node != null) {
                previous = node.prev;
                next = node.next;

                // если это элемент в середине списка
                if (previous != null && next != null) {
                    previous.next = next;
                    next.prev = previous;
                    return;
                }

                // если это tail-элемент
                if (previous != null) {
                    previous.next = null;
                    tail = previous;
                    return;
                }

                // если это head-элемент
                if (next != null) {
                    next.prev = null;
                    head = next;
                    return;
                }

                // если это единственный элемент
                head = null;
                tail = null;
            }
        }
    }

    public static void main(String[] args) {
        InMemoryHistoryManager mgr = new InMemoryHistoryManager();
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

        mgr.remove(task1.getId());
        System.out.println(mgr.getHistory());
    }

}
