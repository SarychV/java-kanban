public class Node {
    Task item;
    Node prev;
    Node next;

    Node(Node prev, Task task, Node next) {
        this.prev = prev;
        this.item = task;
        this.next = next;
    }
}
