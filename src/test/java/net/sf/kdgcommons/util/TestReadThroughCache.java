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

package net.sf.kdgcommons.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import net.sf.kdgcommons.util.ReadThroughCache.Retriever;
import net.sf.kdgcommons.util.ReadThroughCache.Synchronization;


public class TestReadThroughCache
extends TestCase
{
//----------------------------------------------------------------------------
//  Support code
//----------------------------------------------------------------------------

    private final static long DEFAULT_DELAY = 50;

    /**
     *  Convenience function for starting a bunch of threads.
     */
    private static void start(Thread... threads)
    {
        for (Thread thread : threads)
            thread.start();
    }

    /**
     *  Convenience function for joining a bunch of threads.
     */
    private static void join(Thread... threads) throws InterruptedException
    {
        for (Thread thread : threads)
            thread.join();
    }

    /**
     *  A retriever that returns distinct Integer objects based on the provided key.
     */
    private static class DistinctValueRetriever
    implements Retriever<Integer,Integer>
    {
        public Integer retrieve(Integer key) throws InterruptedException
        {
            return new Integer(key.intValue());
        }
    }

    // the following three objects are used for concurrency testing

    /**
     *  The delegate retriever. Because there is only a single retriever for all callers,
     *  it has to depend on the task to manage any state (including locks).
     */
    private static class ConcurrentRetriever
    implements Retriever<Object,Object>
    {
        private Map<Thread,ConcurrentRetrieveTask> configMap = new HashMap<Thread,ConcurrentRetrieveTask>();

        public Thread addTask(ConcurrentRetrieveTask task)
        {
            Thread thread = new Thread(task);
            configMap.put(thread, task);
            return thread;
        }

        public Object retrieve(Object key) throws InterruptedException
        {
            ConcurrentRetrieveTask task = configMap.get(Thread.currentThread());
            return task.retrieve();
        }
    }

    /**
     *  A task that executes a retrieval against the cache, and records information about it.
     */
    private static class ConcurrentRetrieveTask
    implements Runnable
    {
        private ReadThroughCache<Object,Object> cache;
        private Object key;
        private Object value = new Object();
        private long retrieveDelay;

        private CountDownLatch taskLatch  = new CountDownLatch(1);
        private CountDownLatch retrieveLatch  = new CountDownLatch(1);

        public long startTimestamp;             // when the task started running (after latch)
        public long retrieveEntryTimestamp;     // when the cache called the retriever
        public long retrieveExitTimestamp;      // when the retriever returned the object
        public long finishTimestmap;            // when the task finished running
        public Object result;

        public ConcurrentRetrieveTask(ReadThroughCache<Object,Object> cache, Object key, long retrieveDelay)
        {
            this.cache = cache;
            this.key = key;
            this.retrieveDelay = retrieveDelay;
        }

        public void releaseTask()
        {
            taskLatch.countDown();
        }

        public void waitForRetrieve() throws InterruptedException
        {
            retrieveLatch.await();
        }

        public void releaseRetrieve()
        {
            retrieveLatch.countDown();
        }

        public Object retrieve() throws InterruptedException
        {
            retrieveEntryTimestamp = System.currentTimeMillis();
            waitForRetrieve();
            Thread.sleep(retrieveDelay);
            retrieveExitTimestamp = System.currentTimeMillis();
            return value;
        }

        public void run()
        {
            try
            {
                taskLatch.await();
                startTimestamp = System.currentTimeMillis();
                result = cache.retrieve(key);
                finishTimestmap = System.currentTimeMillis();
            }
            catch (InterruptedException ex)
            {
                // ignored
            }
        }
    }

    /**
     *  A variant task that throws when the retriever attempts to get the object.
     */
    private static class ThrowingConcurrentRetrieveTask
    extends ConcurrentRetrieveTask
    implements Thread.UncaughtExceptionHandler
    {
        public Throwable exception;

        public ThrowingConcurrentRetrieveTask(ReadThroughCache<Object,Object> cache, Object key, long retrieveDelay)
        {
            super(cache, key, retrieveDelay);
        }

        @Override
        public void run()
        {
            Thread.currentThread().setUncaughtExceptionHandler(this);
            super.run();
        }

        @Override
        public Object retrieve()
        {
            throw new RuntimeException("oops!");
        }

        public void uncaughtException(Thread t, Throwable e)
        {
            exception = e;
        }
    }


//----------------------------------------------------------------------------
//  Test cases
//----------------------------------------------------------------------------

    public void testBasicOperation() throws Exception
    {
        Integer k1 = Integer.valueOf(1);
        Integer k2 = Integer.valueOf(2);

        ReadThroughCache<Integer,Integer> cache = new ReadThroughCache<Integer,Integer>(10, new DistinctValueRetriever());
        assertEquals("initial size", 0, cache.size());

        Integer v1 = cache.retrieve(k1);
        assertEquals("first retrieve(v1)", 1, v1.intValue());
        assertEquals("size after first retrieve(k1)", 1, cache.size());

        assertTrue("second get(v1) returned same value", v1 == cache.retrieve(k1));
        assertEquals("size after second retrieve(k1)", 1, cache.size());

        assertEquals("retrieve(k2)", 2, cache.retrieve(k2).intValue());
        assertEquals("size after retrieve(k2)", 2, cache.size());

        cache.clear();
        assertEquals("size after clear", 0, cache.size());
        assertFalse("returned same value after clear", v1 == cache.retrieve(k1));
    }


    @SuppressWarnings("unused")
    public void testDiscardLRUEntry() throws Exception
    {
        ReadThroughCache<Integer,Integer> cache = new ReadThroughCache<Integer,Integer>(3, new DistinctValueRetriever());

        Integer v1 = cache.retrieve(1);
        Integer v2 = cache.retrieve(2);
        Integer v3 = cache.retrieve(3);
        Integer v4 = cache.retrieve(4);

        assertFalse("re-retrieve of first value is same object", v1 == cache.retrieve(1));
        assertTrue("re-retrieve of third value is same object", v3 == cache.retrieve(3));

        Integer v5 = cache.retrieve(5);
        Integer v6 = cache.retrieve(6);

        assertTrue("retrieving object moved it to front of list", v3 == cache.retrieve(3));
        assertFalse("retrieving object dropped a value added later", v4 == cache.retrieve(4));
    }


    public void testUnsynchronizedRetrieval() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.NONE);

        ConcurrentRetrieveTask task1 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);

        thread1.start();
        thread2.start();
        task1.releaseTask();            // task 1 starts first
        Thread.sleep(DEFAULT_DELAY);
        task2.releaseTask();
        task2.releaseRetrieve();        // but task 2 completes first
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();
        thread1.join();
        thread2.join();

        assertTrue("thread 1 should start before thread 2",  task1.startTimestamp < task2.startTimestamp);
        assertTrue("thread 1 should get object after thread 2",  task1.retrieveExitTimestamp > task2.retrieveExitTimestamp);
        assertTrue("thread 1 should complete after thread 2",  task1.finishTimestmap > task2.finishTimestmap);
        assertTrue("thread 1 should have thread 2's object", task1.result == task2.value);
        assertTrue("thread 2 should have thread 2's object", task2.result == task2.value);
    }


    public void testUnsynchronizedRetrievalWithException() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.NONE);

        ThrowingConcurrentRetrieveTask task1 = new ThrowingConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);

        start(thread1, thread2);
        task1.releaseTask();        // task 1 starts first
        Thread.sleep(DEFAULT_DELAY);
        task2.releaseTask();
        task2.releaseRetrieve();    // task 2 starts and completes
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();    // task 1 should throw here
        join(thread1, thread2);

        assertTrue("thread 1 should have had an exception",  task1.exception != null);
        assertTrue("thread 1 object should be null",         task1.result == null);
        assertTrue("thread 2 should have thread 2's object", task2.result == task2.value);
    }


    public void testSynchronizedByKeyRetrieval() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.BY_KEY);

        ConcurrentRetrieveTask task1 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task3 = new ConcurrentRetrieveTask(cache, "bar", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);
        Thread thread3 = retriever.addTask(task3);

        start(thread1, thread2, thread3);
        task1.releaseTask();        // task 1 starts first -- it will grab the key used by task2
        Thread.sleep(DEFAULT_DELAY);
        task3.releaseTask();        // task 3 (independent) should run to completion while the others wait
        task3.releaseRetrieve();
        task2.releaseTask();        // task 2 should run, but be blocked by queue before it hits retrieve
        task2.releaseRetrieve();
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();    // task 1 is done -- task2 is now unblocked, but should be fulfilled by cache
        join(thread1, thread2, thread3);

        assertTrue("thread 1 should start before thread 2",     task1.startTimestamp < task2.startTimestamp);
        assertTrue("thread 1 should finish at/before thread 2", task1.finishTimestmap <= task2.finishTimestmap);
        assertTrue("thread 1 should start before thread 3",     task1.startTimestamp < task3.startTimestamp);
        assertTrue("thread 1 should finish after thread 3",     task1.finishTimestmap > task3.finishTimestmap);
        assertTrue("thread 2 should never enter retrieve",      task2.retrieveEntryTimestamp == 0);

        assertTrue("thread 1 should have thread 1's object", task1.result == task1.value);
        assertTrue("thread 2 should have thread 1's object", task2.result == task1.value);
        assertTrue("thread 3 should get its own object",     task3.result == task3.value);
    }


    public void testSynchronizedByKeyRetrievalWithException() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.BY_KEY);

        ThrowingConcurrentRetrieveTask task1 = new ThrowingConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);

        start(thread1, thread2);
        task1.releaseTask();        // task 1 starts first -- it will grab the key used by task2
        Thread.sleep(DEFAULT_DELAY);
        task2.releaseTask();        // task 2 should run, but be blocked by queue before it hits retrieve
        task2.releaseRetrieve();
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();    // task 1 throws, task 2 should now enter retrieve
        join(thread1, thread2);

        assertTrue("thread 1 should start before thread 2",          task1.startTimestamp < task2.startTimestamp);
        assertTrue("thread 1 should enter retrieve before thread 2", task1.retrieveEntryTimestamp < task2.retrieveEntryTimestamp);
        assertTrue("thread 1 should never leave retrieve",           task1.retrieveExitTimestamp == 0);
        assertTrue("thread 2 should execute retrieve",               task2.retrieveExitTimestamp != 0);

        assertTrue("thread 1 should have had an exception",  task1.exception != null);
        assertTrue("thread 1 object should be null",         task1.result == null);
        assertTrue("thread 2 should have thread 2's object", task2.result == task2.value);
    }


    public void testFullySynchronizedRetrieval() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.SINGLE_THREADED);

        ConcurrentRetrieveTask task1 = new ConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "bar", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);

        start(thread1, thread2);
        task1.releaseTask();        // task 1 starts first -- it will lock the cache
        Thread.sleep(DEFAULT_DELAY);
        task2.releaseTask();        // task 2 should run, but be blocked by queue before it hits retrieve
        task2.releaseRetrieve();
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();    // task 1 is done -- task2 is now unblocked
        join(thread1, thread2);

        assertTrue("thread 1 should start before thread 2",                   task1.startTimestamp < task2.startTimestamp);
        assertTrue("thread 1 should exit retrieve at/before thread 2 enters", task1.retrieveExitTimestamp <= task2.retrieveEntryTimestamp);

        assertTrue("thread 1 should have thread 1's object", task1.result == task1.value);
        assertTrue("thread 2 should have thread 2's object", task2.result == task2.value);
    }


    public void testFullySynchronizedRetrievalWithException() throws Exception
    {
        ConcurrentRetriever retriever = new ConcurrentRetriever();
        ReadThroughCache<Object,Object> cache = new ReadThroughCache<Object,Object>(10, retriever, Synchronization.SINGLE_THREADED);

        ThrowingConcurrentRetrieveTask task1 = new ThrowingConcurrentRetrieveTask(cache, "foo", DEFAULT_DELAY);
        ConcurrentRetrieveTask task2 = new ConcurrentRetrieveTask(cache, "bar", DEFAULT_DELAY);

        Thread thread1 = retriever.addTask(task1);
        Thread thread2 = retriever.addTask(task2);

        start(thread1, thread2);
        task1.releaseTask();        // task 1 starts first -- it will lock the cache
        Thread.sleep(DEFAULT_DELAY);
        task2.releaseTask();        // task 2 should run, but be blocked by queue before it hits retrieve
        task2.releaseRetrieve();
        Thread.sleep(DEFAULT_DELAY);
        task1.releaseRetrieve();    // task 1 throws -- task2 is now unblocked
        join(thread1, thread2);

        assertTrue("thread 1 should start before thread 2",          task1.startTimestamp < task2.startTimestamp);
        assertTrue("thread 1 should enter retrieve before thread 2", task1.retrieveEntryTimestamp < task2.retrieveEntryTimestamp);
        assertTrue("thread 1 should not have exited retrieve",       task1.retrieveExitTimestamp == 0);

        assertTrue("thread 1 should have thrown",            task1.exception != null);
        assertTrue("thread 2 should have thread 2's object", task2.result == task2.value);
    }

}
