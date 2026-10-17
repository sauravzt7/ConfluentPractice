package concurrency;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class BoundedBuffer {
    private final int CAP = 10;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Deque<Integer> buffer = new ArrayDeque<>();


    public void producer(int v) throws InterruptedException{
     lock.lock(); // acquire lock
     try{
         while(buffer.size() == CAP)notFull.await(); // wait as long as buffer is full
         buffer.add(v);
         notEmpty.signal();
     } finally {
         lock.unlock();
     }
    }
    public int consumer() throws InterruptedException{
        lock.lock();
        try{
            while(buffer.size() == 0)notEmpty.await();
            int v = buffer.pollFirst();
            notFull.signal();
            return v;
        } finally {
            lock.unlock();
        }

    }
}
