package outskirts.util.concurrent;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {  // ScheduledExecutor

    private final Queue<Runnable> scheduledTasks = new LinkedList<>();

    private Thread thread;

    // needs improve! this is not a common-usage way. may better for use a Suppler<Boolean>
    public Scheduler(Thread ownerThread) {
        this.thread = ownerThread;
    }

    public void processTasks() {
        if (!inSchedulerThread())
            throw new IllegalStateException("Not in scheduler thread context. (actual: "+Thread.currentThread().getName()+", expect: "+thread.getName()+")");

        synchronized (scheduledTasks) {
            while (!scheduledTasks.isEmpty()) {
                scheduledTasks.poll().run();
            }
        }
    }

    // rename to offerTask() ..?
    public void addScheduledTask(Runnable runnable) {
        synchronized (scheduledTasks) {
            scheduledTasks.offer(runnable);
        }
    }

    public boolean inSchedulerThread() {
        return Thread.currentThread() == thread;
    }

}
