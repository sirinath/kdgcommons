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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *  A {@link java.util.concurrent.ThreadFactory} implementation that allows
 *  the user to specify a prefix name for each thread. This is useful for an
 *  application that has multiple thread pools, to distinguish the pools in
 *  a thread dump.
 *  <p>
 *  All threads created by this factory are named "<code>PREFIX-thread-NN</code>",
 *  where PREFIX is provided by the caller, and NN is an auto-incremented number
 *  starting with 0.
 *
 *  @since 1.0.5
 */
public class NamedThreadFactory
implements ThreadFactory
{
    private String _prefix;
    private ThreadGroup _group;
    private int _priority;
    private boolean _isDaemon;
    private AtomicInteger _counter = new AtomicInteger(0);


    /**
     *  A factory that creates daemon threads with NORMAL priority, belonging to
     *  the thread group of the thread that created the factory.
     */
    public NamedThreadFactory(String prefix)
    {
        this(prefix, Thread.currentThread().getThreadGroup(), Thread.NORM_PRIORITY, true);
    }


    /**
     *  A factory that allows complete control over the threads created.
     *
     *  @param  prefix      Initial component of name for created threads.
     *  @param  group       Thread group to which created threads belong.
     *  @param  priority    Priority of created threads.
     *  @param  isDaemon    Whether or not created threads are daemons (ie,
     *                      whether they will keep the JVM running).
     *
     */
    public NamedThreadFactory(String prefix, ThreadGroup group, int priority, boolean isDaemon)
    {
        _prefix = prefix;
        _group = group;
        _priority = priority;
        _isDaemon = isDaemon;
    }


//----------------------------------------------------------------------------
//  ThreadFactory
//----------------------------------------------------------------------------

    public Thread newThread(Runnable r)
    {
        String name = _prefix + "-thread-" + _counter.getAndIncrement();
        Thread ret = new Thread(_group, r, name);
        ret.setPriority(_priority);
        ret.setDaemon(_isDaemon);
        return ret;
    }
}
