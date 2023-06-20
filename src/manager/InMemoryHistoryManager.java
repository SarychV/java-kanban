package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private class CustomLinkedList {
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
                if (tail != null) {
                    tail.next = newNode;    // Привязали новый узел к хвосту.
                } else {
                    head = newNode;  // Если tail==null, значит список пуст и новый узел нужно привязать еще и к голове
                }
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
}
