public class Queue {
    private int[][] data;
    private int front, rear, max;

    public Queue(int maxItems) {
        data = new int[maxItems][];
        front = -1; //no items in data
        rear = -1;
        max = maxItems;
    }

    //look at the front of the queue
    public int[] peek() {
        return data[front];
    }

    //remove front item from Queue
    public int[] dequeue() {
        front = (front + 1) % max;
        return data[front - 1];
    }

    //add an item to front of Queue
    public void enqueue(int[] item) {
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