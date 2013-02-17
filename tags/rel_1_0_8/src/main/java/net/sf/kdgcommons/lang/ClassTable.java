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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *  A threadsafe <code>Map&lt;Class,T&gt;<code>, in which {@link #get} will walk
 *  the class hiearchy of its argument looking for a mapping (and will cache that
 *  mapping for subsequent use). Primarily useful as a <code>Method</code> cache.
 *  <p>
 *  <em>Warning</em:
 *  Normal use of this class involves calling {@link #put} on a base set of classes,
 *  followed by calls to {@link #get} with arbitrary descendents of those classes.
 *  Interspersed calls to <code>get()</code> and <code>put()</code> are discouraged,
 *  as they may leave different mappings for members of the same class hierarchy. If
 *  you need to replace a mapping, call {@link #replace}.
 */
public class ClassTable<T>
{
    private volatile ConcurrentHashMap<Class<?>,T> _map = new ConcurrentHashMap<Class<?>,T>();


    /**
     *  Returns the number of mappings in this object. Useful for debugging/testing.
     */
    public int size()
    {
        return _map.size();
    }


    /**
     *  Adds a mapping to the table, replacing any previous exact mapping for that
     *  class. Subclass mappings are <em>not</em> affected.
     */
    public void put(Class<?> klass, T object)
    {
        _map.put(klass, object);
    }


    /**
     *  Returns the mapping corresponding to the passed class, traversing its
     *  concrete inheritance hierarchy until a match is found. Returns
     *  <code>null</code> if unable to find a match.
     */
    public T get(Class<?> klass)
    {
        T result = _map.get(klass);
        if (result != null)
            return result;

        Class<?> superclass = klass.getSuperclass();
        if (superclass == null)
            return null;

        result = get(superclass);
        if (result != null)
            _map.put(klass, result);

        return result;
    }


    /**
     *  Returns the mapping corresponding to the passed object's class.
     */
    public T getByObject(Object obj)
    {
        return get(obj.getClass());
    }


    /**
     *  Replaces the mapping for the passed class and all known subclasses. This
     *  is an atomic operation. Note, however, that concurrent threads may see
     *  either the old or new mapping.
     */
    public void replace(Class<?> klass, T value)
    {
        ConcurrentHashMap<Class<?>,T> newMap = new ConcurrentHashMap<Class<?>,T>();
        for (Map.Entry<Class<?>,T> entry : _map.entrySet())
        {
            if (klass.isAssignableFrom(entry.getKey()))
                newMap.put(entry.getKey(), value);
            else
                newMap.put(entry.getKey(), entry.getValue());
        }
        _map = newMap;
    }
}
