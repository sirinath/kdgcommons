// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sf.kdgcommons.lang;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;


public class TestThreadUtil
extends TestCase
{
    public void testSleepQuietlyNormalOperation() throws Exception
    {
        long startTime = System.currentTimeMillis();
        long elapsedTime = ThreadUtil.sleepQuietly(100L);
        long finishTime = System.currentTimeMillis();

        // sleep times won't be exact, both due to scheduling and clock resolution
        // ... although a 50ms delta is probably too much

        assertEquals("test 1: slept for approx 100 ms", 100.0, elapsedTime, 50.0);
        assertEquals("test 1: return time approx real", (finishTime - startTime), elapsedTime, 20.0);
    }


    public void testSleepQuietlyWhenInterrupted() throws Exception
    {
        // the test has to run in a background thread, which means that we need
        // to synchronize interactions; these latches signal the start and end
        // of the background thread's actions
        final CountDownLatch latchStart = new CountDownLatch(1);
        final CountDownLatch latchFinish = new CountDownLatch(1);
        final AtomicLong elapsedTime = new AtomicLong(12345);

        Thread runner = new Thread()
        {
            @Override
            public void run()
            {
                latchStart.countDown();
                long e0 = ThreadUtil.sleepQuietly(500L);
                elapsedTime.set(e0);
                latchFinish.countDown();
            }
        };

        long startTime = System.currentTimeMillis();
        runner.start();
        latchStart.await();
        ThreadUtil.sleepQuietly(100L);
        runner.interrupt();
        latchFinish.await();
        long finishTime = System.currentTimeMillis();

        assertEquals("test 2: slept for approx 100 ms", 100.0, elapsedTime.get(), 50.0);
        assertEquals("test 2: return time approx real", (finishTime - startTime), elapsedTime.get(), 20.0);
    }
}
