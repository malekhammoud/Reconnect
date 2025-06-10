public class Queue {
    private Map.BFSNode[] data; // Changed from int[][] to Map.BFSNode[]
    private int front, rear, max;

    public Queue(int maxItems) {
        data = new Map.BFSNode[maxItems]; // Changed from int[][] to Map.BFSNode[]
        front = -1; //no items in data
        rear = -1;
        max = maxItems;
    }

    //look at the front of the queue
    public Map.BFSNode peek() { // Changed return type from int[] to Map.BFSNode
        return data[front];
    }

    //remove front item from Queue
    public Map.BFSNode dequeue() { // Changed return type from int[] to Map.BFSNode
        front = (front + 1) % max;
        return data[front - 1];
    }

    //add an item to front of Queue
    public void enqueue(Map.BFSNode item) { // Changed parameter type from int[] to Map.BFSNode
        if (isEmpty()) { //first item queued
            front = 0;
            rear = 0;
            data[rear] = item;
        } else {
            rear = (rear + 1) % max;
            data[rear] = item;
        }
    }

    //check if Queue is empty
    public boolean isEmpty() {
        if (front == -1 && rear == -1) return true;
        else return false;
    }

    //get number of items in Queue
    public int size() {
        if (isEmpty()) return 0;
        else return (rear - front + 1);
    }

    //empty the Queue
    public void clear() {
        front = -1;
        rear = -1;
    }
}
