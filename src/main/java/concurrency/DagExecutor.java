package concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/*
    Dependency DAG executor
    Given a list of tasks where each task has a list of dependencies and a list of dependents,
    this class executes the tasks in a dependency-aware manner using a provided ExecutorService.
 */
class Task {
    List<Task> deps = new ArrayList<>(); // list of tasks that this task depends on
    List<Integer> dependents = new ArrayList<>(); // indices of tasks that depend on this task
    AtomicInteger remaining = new AtomicInteger(0); // remaining dependencies to execute
    Runnable runnable;
}

public class DagExecutor {
    private final List<Task> tasks;
    private final ExecutorService pool;

    DagExecutor(List<Task> tasks, ExecutorService pool) {
        this.tasks = tasks;
        this.pool = pool;
        for(Task t : tasks) t.remaining.set(t.deps.size());
    }

    // submit tasks that have no remaining dependencies
    public void start(){
        for(int id = 0; id < tasks.size(); id++){
            if(tasks.get(id).remaining.get() == 0){
                submit(id);
            }
        }
    }

    private void submit(int id){
        pool.submit(() -> {
            tasks.get(id).runnable.run();
            onComplete(id);
        });
    }


    void onComplete(int id){
        for(int next: tasks.get(id).dependents){
            if(tasks.get(next).remaining.decrementAndGet() == 0){
                submit(next);
            }
        }
    }
}
