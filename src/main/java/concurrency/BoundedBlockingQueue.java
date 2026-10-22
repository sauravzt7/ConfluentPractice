package concurrency;

import java.util.concurrent.Semaphore;

class BoundedBlockingQueue {

    int capacity;
    int head = 0;
    int tail = 0;
    int size = 0;
    int[] array;
    private final Semaphore sem  = new Semaphore(1);
    private final Semaphore semProducer;
    private final Semaphore semConsumer;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.semProducer = new Semaphore(capacity);
        this.semConsumer = new Semaphore(0);
        this.array = new int[capacity];
    }

    public void enqueue(int element) throws InterruptedException {


        semProducer.acquire();
        sem.acquire();
        tail = (tail + 1) % capacity;

        array[tail] = element;

        size++;
        sem.release();
        semConsumer.release();

    }

    public int dequeue() throws InterruptedException {


        semConsumer.acquire();
        sem.acquire();
        head = (head + 1) % capacity;
        int v = array[head];
        size--;
        sem.release();
        semProducer.release();
        return v;

    }

    public int size() {
        return this.size;
    }
}