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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


/**
 *  A size-limited LRU cache that uses a retriever function to load values. Instances are
 *  thread-safe (provided the retriever is thread-safe) and provide a variety of blocking
 *  options for retrieval.
 *  <p>
 *  Note that the cache itself implements the {@link #Retriever} interface; caches may be
 *  stacked.
 *  
 *  @since 1.0.15
 */
public class ReadThroughCache<K,V>
{
    /**
     *  This interface defines the retrieval operation: given a key, it will return a value.
     *  By contract, the retriever will wait forever; specific implementations may abandon
     *  retrieval after a timeout.
     */
    public interface Retriever<KK,VV>
    {
        /**
         *  Retrieves the value corresponding to the passed key. May return <code>null</code>.
         */
        VV retrieve(KK key) throws InterruptedException;
    }


    /**
     *  Options for controlling concurrent retrieval.
     */
    public enum Synchronization
    {
        /**
         *  No synchronization: concurrent requests for the same key will invoke concurrent
         *  retrievals. The first value returned is cached, subsequent values are discarded.
         */
        NONE,

        /**
         *  Per-key synchronization: each retrieval request establishes a lock that is cleared
         *  when the retrieval completes. Subsequent retrieves for the same key will block until
         *  the first returns. This is the default behavior.
         */
        BY_KEY,

        /**
         *  Single-threaded: only one invocation of the retriever will take place at a time; all
         *  subsequent requests will block until it completes. This should only be used if the
         *  retriever is not thread-safe.
         */
        SINGLE_THREADED
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    public V retrieve(K key) throws InterruptedException
    {
        // all the intelligence happens in the retriever decorators
        return retriever.retrieve(key);
    }


    /**
     *  Returns the count of mappings currently in the cache.
     */
    public int size()
    {
        synchronized (cacheLock)
        {
            return cache.size();
        }
    }


    /**
     * Removes all cached values.
     */
    public void clear()
    {
        synchronized (cacheLock)
        {
            cache.clear();
        }
    }

//----------------------------------------------------------------------------
//  Constructors and instance variables
//----------------------------------------------------------------------------

    private Retriever<K,V> retriever;
    private Object cacheLock = new Object();
    private Map<K,V> cache = null;

    /**
     *  Base constructor.
     *
     *  @param size         Maximum number of items in the cache; the least recently used
     *                      item will be evicted if retrieval would exceed this limit. To
     *                      prevent resizing the underlying hash table, this value is also
     *                      used as the map's capacity.
     *  @param retriever    The function to retrieve items.
     *  @param syncOpt      The synchronization strategy.
     */
    public ReadThroughCache(final int size, Retriever<K,V> retriever, Synchronization syncOpt)
    {
        switch (syncOpt)
        {
            case NONE :
                this.retriever = new UnsynchronizedRetriever(retriever);
                break;
            case BY_KEY :
                this.retriever = new ByKeyRetriever(retriever);
                break;
            case SINGLE_THREADED :
//                this.retriever = new UnsynchronizedRetriever(retriever);
                this.retriever = new SingleThreadedRetriever(retriever);
                break;
            default :
                throw new IllegalArgumentException("invalid synchronization option: " + syncOpt);
        }

        cache = new LinkedHashMap<K,V>(size, 0.75f, true)
        {
             private static final long serialVersionUID = 1L;

             @Override
             protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size() > size;
             }
        };
    }


    /**
     *  Convenience constructor: creates an instance with specified size and retriever,
     *  using per-key synchronization.
     */
    public ReadThroughCache(int size, Retriever<K,V> retriever)
    {
        this(size, retriever, Synchronization.BY_KEY);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private abstract class AbstractDelegatingRetriever
    implements Retriever<K,V>
    {
        protected Retriever<K,V> delegate;

        protected AbstractDelegatingRetriever(Retriever<K,V> delegate)
        {
            this.delegate = delegate;
        }

        public V retrieve(K key) throws InterruptedException
        {
            synchronized (cacheLock)
            {
                if (cache.containsKey(key))
                    return cache.get(key);
            }
            return retrieve0(key);
        }

        public abstract V retrieve0(K key) throws InterruptedException;
    }


    private class UnsynchronizedRetriever
    extends AbstractDelegatingRetriever
    {
        public UnsynchronizedRetriever(Retriever<K,V> delegate)
        {
            super(delegate);
        }

        @Override
        public V retrieve0(K key) throws InterruptedException
        {
            V value = delegate.retrieve(key);

            synchronized (cacheLock)
            {
                if (cache.containsKey(key))
                    return cache.get(key);

                cache.put(key, value);
                return value;
            }
        }
    }


    private class ByKeyRetriever
    extends AbstractDelegatingRetriever
    {
        private Object internalLock = new Object();
        private Map<K,ReentrantLock> keyLocks = new HashMap<K,ReentrantLock>();

        public ByKeyRetriever(Retriever<K,V> delegate)
        {
            super(delegate);
        }

        @Override
        public V retrieve0(K key) throws InterruptedException
        {
            ReentrantLock keyLock = getOrCreateLock(key);
            if (keyLock == null)
            {
                return doRetrieve(key);
            }
            else
            {
                keyLock.lockInterruptibly();
                keyLock.unlock();
                synchronized (cacheLock)
                {
                    V value = cache.get(key);
                    if ((value != null) || cache.containsKey(key))
                        return value;
                }

                // if we fall through to here, it means that the blocking process has
                // thrown an exception and it's up to us to retrieve the data; we'll
                // assume that *someone* will retrieve the value before we blow stack
                return retrieve0(key);
            }
        }

        private ReentrantLock getOrCreateLock(K key) throws InterruptedException
        {
            synchronized (internalLock)
            {
                ReentrantLock lock = keyLocks.get(key);
                if (lock != null)
                    return lock;

                lock = new ReentrantLock();
                lock.lockInterruptibly();
                keyLocks.put(key, lock);
                return null;
            }
        }

        private void removeLock(K key)
        {
            ReentrantLock lock = null;
            synchronized (internalLock)
            {
                lock = keyLocks.remove(key);
            }
            lock.unlock();
        }

        private V doRetrieve(K key) throws InterruptedException
        {
            try
            {
                V value = delegate.retrieve(key);
                synchronized (cacheLock)
                {
                    cache.put(key, value);
                }
                return value;
            }
            finally
            {
                removeLock(key);
            }
        }
    }


    private class SingleThreadedRetriever
    extends AbstractDelegatingRetriever
    {
        public SingleThreadedRetriever(Retriever<K,V> delegate)
        {
            super(delegate);
        }

        @Override
        public synchronized V retrieve0(K key) throws InterruptedException
        {
            V value = delegate.retrieve(key);

            synchronized (cacheLock)
            {
                if (cache.containsKey(key))
                    return cache.get(key);

                cache.put(key, value);
                return value;
            }
        }
    }
}
