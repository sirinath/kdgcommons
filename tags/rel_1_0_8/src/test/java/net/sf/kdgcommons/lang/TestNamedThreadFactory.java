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
import java.util.concurrent.ThreadFactory;

import junit.framework.TestCase;


public class TestNamedThreadFactory
extends TestCase
{
    // these variables let created threads report back to the main thread
    private volatile String _threadName;
    private volatile ThreadGroup _threadGroup;
    private volatile int _priority;
    private volatile boolean _isDaemon;

    // the main thread uses this latch to ensure that the created thread runs
    private CountDownLatch syncLatch = new CountDownLatch(1);

    // and this runnable does the work
    Runnable task = new Runnable()
    {
        public void run()
        {
            _threadName = Thread.currentThread().getName();
            _threadGroup = Thread.currentThread().getThreadGroup();
            _priority = Thread.currentThread().getPriority();
            _isDaemon = Thread.currentThread().isDaemon();
            syncLatch.countDown();
        }
    };


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    public void testSimpleConstructor() throws Exception
    {
        ThreadFactory fact = new NamedThreadFactory("foo");

        fact.newThread(task).start();
        syncLatch.await();

        assertEquals("name",       "foo-thread-0", _threadName);
        assertSame("thread group", Thread.currentThread().getThreadGroup(), _threadGroup);
        assertEquals("priority",   Thread.NORM_PRIORITY, _priority);
        assertTrue("isDaemon",     _isDaemon);
    }


    public void testFactoryRetainsThreadGroupOfCreator() throws Exception
    {
        ThreadGroup factGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup altGroup = new ThreadGroup("foo");
        final ThreadFactory fact = new NamedThreadFactory("foo");

        new Thread(altGroup, new Runnable()
        {
            public void run()
            {
                fact.newThread(task).start();
            }
        }).start();
        syncLatch.await();

        assertSame("thread group", factGroup, _threadGroup);
    }


    public void testFullConstructor() throws Exception
    {
        ThreadGroup altGroup = new ThreadGroup("foo");
        final ThreadFactory fact = new NamedThreadFactory("foo", altGroup, Thread.MIN_PRIORITY, false);

        fact.newThread(task).start();
        syncLatch.await();

        assertEquals("name",       "foo-thread-0", _threadName);
        assertSame("thread group", altGroup, _threadGroup);
        assertEquals("priority",   Thread.MIN_PRIORITY, _priority);
        assertFalse("isDaemon",    _isDaemon);
    }
}
