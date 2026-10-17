package scheduler;

import java.util.PriorityQueue;

class Task implements Comparable<Task> {
    Runnable command;
    long executionTime;

    public Task(Runnable command, long delayInMillis) {
        this.command = command;
        this.executionTime = System.currentTimeMillis() + delayInMillis;
    }

    @Override
    public int compareTo(Task other) {
        return Long.compare(this.executionTime, other.executionTime);
    }
}

public class CustomScheduler {
    private final PriorityQueue<Task> taskQueue;
    private final Object lock;
    private final Thread schedulerThread;
    private volatile boolean isRunning;

    public CustomScheduler() {
        this.taskQueue = new PriorityQueue<>();
        this.lock = new Object();
        this.isRunning = true;
        this.schedulerThread = new Thread(this::run);
        this.schedulerThread.start();
    }

    public void schedule(Runnable command, long delayInMillis) {
        synchronized (lock) {
            Task task = new Task(command, delayInMillis);
            taskQueue.offer(task);
            // Notify the scheduler thread that a new task has been added
            lock.notify();
        }
    }

    private void run() {
        while (isRunning) {
            synchronized (lock) {
                while (taskQueue.isEmpty() && isRunning) {
                    try {
                        lock.wait(); // Wait until a task is scheduled
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (!isRunning) break;

                Task nextTask = taskQueue.peek();
                long currentTime = System.currentTimeMillis();

                if (nextTask.executionTime <= currentTime) {
                    taskQueue.poll();
                    // In a production system, you'd execute this in a separate worker thread pool
                    // to prevent a slow task from blocking the scheduler.
                    new Thread(nextTask.command).start();
                } else {
                    try {
                        // Wait until the next task is ready to be executed
                        long waitTime = nextTask.executionTime - currentTime;
                        lock.wait(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    public void shutdown() {
        isRunning = false;
        synchronized (lock) {
            lock.notifyAll(); // Wake up the scheduler thread to terminate
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CustomScheduler scheduler = new CustomScheduler();

        scheduler.schedule(() -> System.out.println("Task 1 executed at " + System.currentTimeMillis()), 3000);
        scheduler.schedule(() -> System.out.println("Task 2 executed at " + System.currentTimeMillis()), 1000);
        scheduler.schedule(() -> System.out.println("Task 3 executed at " + System.currentTimeMillis()), 2000);

        // Allow some time for tasks to finish
        Thread.sleep(4000);
        scheduler.shutdown();
    }
}
