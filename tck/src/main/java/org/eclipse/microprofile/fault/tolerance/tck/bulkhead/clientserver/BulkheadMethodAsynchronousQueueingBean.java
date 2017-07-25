package org.eclipse.microprofile.fault.tolerance.tck.bulkhead.clientserver;

import java.util.concurrent.Future;

import org.eclipse.microprofile.fault.tolerance.tck.bulkhead.BulkheadTest;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;

/**
 *  A simple method level Asynchronous @Bulkhead(10)
 *
 */

public class BulkheadMethodAsynchronousQueueingBean implements BulkheadTestBackend {

    @Override
    @Bulkhead(value=10, waitingThreadQueue=10) @Asynchronous
    public Future test(BackendTestDelegate action) {
        BulkheadTest.log("in bean");
        return action.perform();
    }

};