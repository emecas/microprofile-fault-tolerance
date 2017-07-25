package org.eclipse.microprofile.fault.tolerance.tck.bulkhead.clientserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.fault.tolerance.tck.bulkhead.BulkheadTest;
import org.testng.Assert;

/**
 * A simple sleeping test backend worker
 */
public class Checker implements BackendTestDelegate {

    private int millis = 1;
    private static AtomicInteger workers = new AtomicInteger(0);
    private static AtomicInteger maxSimultaneousWorkers = new AtomicInteger(0);
    private static AtomicInteger instances = new AtomicInteger(0);
    private static AtomicInteger tasksScheduled = new AtomicInteger(0);
    private static int expectedInstances;
    private static int expectedMaxSimultaneousWorkers;
    private static int expectedTasksScheduled;

    public static void setExpectedInstances(int expectedInstances) {
        Checker.expectedInstances = expectedInstances;
    }

    public static void setExpectedMaxWorkers(int expectedMaxWorkers) {
        Checker.expectedMaxSimultaneousWorkers = expectedMaxWorkers;
    }

    static final String BAR = "**************************************************************************************+++";

    /**
     * @param i
     *            how long to sleep for in milliseconds
     */
    public Checker(int sleepMillis) {
        millis = sleepMillis;
        instances.incrementAndGet();
    }

    /*
     * Work
     * 
     * @see org.eclipse.microprofile.fault.tolerance.tck.bulkhead.clientserver.
     * BulkheadTestAction#perform()
     */
    @Override
    public Future<String> perform() {
        try {
            int taskId = tasksScheduled.incrementAndGet();
            int now = workers.incrementAndGet();
            int max = maxSimultaneousWorkers.get();

            while ((now > max) && !maxSimultaneousWorkers.compareAndSet(max, now)) {
                max = maxSimultaneousWorkers.get();
            }

            BulkheadTest.log("Task " + taskId + " sleeping for " + millis + " milliseconds. " + now + " workers from " + instances
                    + " instances " + BAR.substring(0, now));
            Thread.sleep(millis);
            workers.decrementAndGet();
            BulkheadTest.log("woke");
        }
        catch (InterruptedException e) {
            BulkheadTest.log(e.toString());
        }
        CompletableFuture<String> result = new CompletableFuture<>();
        result.complete("max workers was " + maxSimultaneousWorkers.get());
        return result;
    }

    /**
     * Prepare the static state for the next test
     */
    public static void reset() {
        instances.set(0);
        workers.set(0);
        maxSimultaneousWorkers.set(0);
        tasksScheduled.set(0);
    }

    /**
     * Check the test ran successfully
     */
    public static void check() {
        Assert.assertEquals(workers.get(), 0, "Not all workers finished");
        Assert.assertEquals(instances.get(), expectedInstances, " Not all workers launched");
        Assert.assertTrue(maxSimultaneousWorkers.get() <= expectedMaxSimultaneousWorkers,
                " Bulkhead appears to have been breeched " + maxSimultaneousWorkers.get() + " workers, expected "
                        + expectedMaxSimultaneousWorkers);
        Assert.assertFalse(expectedMaxSimultaneousWorkers > 1 && maxSimultaneousWorkers.get() == 1,
                " Workers are not in parrallel ");
        Assert.assertTrue(expectedMaxSimultaneousWorkers == maxSimultaneousWorkers.get(),
                " Work is not being done simultaneously enough, only  " + maxSimultaneousWorkers + ". "
                        + " workers are once. Expecting " + expectedMaxSimultaneousWorkers + ". ");
        Assert.assertFalse(expectedTasksScheduled != 0 && tasksScheduled.get() < expectedTasksScheduled,
                " Some tasks are missing, expected " + expectedTasksScheduled + " got " + tasksScheduled.get() + ".");
        
        
        BulkheadTest.log("Checks passed");
    }

    public static void setExpectedTasksScheduled(int expected) {
        expectedTasksScheduled = expected;
    }
}
