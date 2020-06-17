/*
 *******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.eclipse.microprofile.fault.tolerance.tck.bulkhead.clientserver;

import org.eclipse.microprofile.fault.tolerance.tck.util.Barrier;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Retry;

/**
 * Tests to ensure that BulkheadExceptions are retried
 * <p>
 * Has a bulkhead size of 1
 * <p>
 * Retries on all exceptions for 3 seconds
 */
@Bulkhead(1)
@Retry(maxRetries = 99999, maxDuration = 3000, delay = 100, jitter = 0)
public class Bulkhead1RetryManySyncClassBean {
    
    public void test(Barrier barrier) {
        barrier.await();
    }

}
