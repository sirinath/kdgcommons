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

package net.sf.kdgcommons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;


/**
 *  Static utility methods for working with collections -- particularly
 *  parameterized collections.
 */
public class CollectionUtil
{
    /**
     *  Returns a set containing the passed elements.
     */
    public static <T> Set<T> asSet(T... elems)
    {
        Set<T> result = new HashSet<T>();
        for (T elem : elems)
            result.add(elem);
        return result;
    }


    /**
     *  Appends an arbitrary number of explicit elements to an existing collection.
     *  Primarily useful when writing testcases.
     */
    public static <T> void addAll(Collection<T> coll, T... elems)
    {
        for (T elem : elems)
            coll.add(elem);
    }


    /**
     *  Appends the values returned by an iterator to the passed collection.
     */
    public static <T> void addAll(Collection<T> coll, Iterator<T> src)
    {
        while (src.hasNext())
            coll.add(src.next());
    }


    /**
     *  Appends the contents of an iterable object to the passed collection.
     */
    public static <T> void addAll(Collection<T> coll, Iterable<T> src)
    {
        addAll(coll, src.iterator());
    }


    /**
     *  Adds a value to the collection if the boolean expression is true.
     *  Returns the collection as a convenience for chained invocations.
     *
     *  @since 1.0.8
     */
    public static <T> Collection<T> addIf(Collection<T> coll, T value, boolean expr)
    {
        if (expr)
            coll.add(value);

        return coll;
    }


    /**
     *  Adds a value to the collection if it's not null. Returns the collection
     *  as a convenience for chained invocations.
     *
     *  @since 1.0.11
     */
    public static <T> Collection<T> addIfNotNull(Collection<T> coll, T value)
    {
        return addIf(coll, value, value != null);
    }


    /**
     *  Adds the specified item to a map if it does not already exist. Returns
     *  either the added item or the existing mapping.
     *  <p>
     *  <em>Note:</em>    The return behavior differs from <code>Map.put()</code>,
     *                    in that it returns the new value if there was no prior
     *                    mapping. I find this more useful, as I typically want
     *                    to do something with the mapping.
     *  <p>
     *  <em>Warning:</em> This operation is not synchronized. In most cases, a
     *                    better approach is to use {@link DefaultMap}, with a
     *                    functor to generate new entries.
     *
     *  @since 1.0.12
     */
    public static <K,V> V putIfAbsent(Map<K,V> map, K key, V value)
    {
        if (! map.containsKey(key))
        {
            map.put(key,value);
            return value;
        }

        return map.get(key);
    }


    /**
     *  Adds entries from <code>add</code> to <code>base</code> where there is not
     *  already a mapping with the same key.
     *
     *  @since 1.0.14
     */
    public static <K,V> void putIfAbsent(Map<K,V> base, Map<K,V> add)
    {
        for (Map.Entry<K,V> entry : add.entrySet())
            putIfAbsent(base, entry.getKey(), entry.getValue());
    }


    /**
     *  Returns the first element of the passed list, <code>null</code> if
     *  the list is empty or null.
     */
    public static <T> T first(List<T> list) {
        return isNotEmpty(list) ? list.get(0) : null;
    }


    /**
     *  Returns the last element of the passed list, <code>null</code> if
     *  the list is empty or null.
     */
    public static <T> T last(List<T> list) {
        return isNotEmpty(list) ? list.get(list.size() - 1) : null;
    }


    /**
     *  Verifies that the passed list contains only elements of the given
     *  type, and returns it as a parameterized type. Throws if any element
     *  is a different type.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<?> list, Class<T> klass)
    {
        for (Object obj : list)
        {
            klass.cast(obj);
        }
        return (List<T>)list;
    }


    /**
     *  Verifies that the passed set contains only elements of the given
     *  type, and returns it as a parameterized type. Throws if any element
     *  is a different type.
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> cast(Set<?> set, Class<T> klass)
    {
        for (Object obj : set)
        {
            klass.cast(obj);
        }
        return (Set<T>)set;
    }


    /**
     *  Resizes the passed list to N entries. Will add the specified object if
     *  the list is smaller than the desired size, discard trailing entries if
     *  larger. This is primarily used to presize a list that will be accessed
     *  by index, but it may also be used to truncate a list for display.
     *
     *  @return The list, as a convenience for callers
     *
     *  @throws UnsupportedOperationException if the list does not implement
     *          <code>RandomAccess</code> and its list iterator does not
     *          support the <code>remove()</code> operation
     */
    public static <T> List<T> resize(List<T> list, int newSize, T obj)
    {
        if (list instanceof ArrayList)
            ((ArrayList<T>)list).ensureCapacity(newSize);

        if (list.size() < newSize)
        {
            for (int ii = list.size() ; ii < newSize ; ii++)
                list.add(obj);
        }
        else if (list.size() > newSize)
        {
            if (list instanceof RandomAccess)
            {
                for (int ii = list.size() - 1 ; ii >= newSize ; ii--)
                    list.remove(ii);
            }
            else
            {
                ListIterator<T> itx = list.listIterator(newSize);
                while (itx.hasNext())
                {
                    itx.next();
                    itx.remove();
                }
            }
        }

        return list;
    }


    /**
     *  Resizes the passed list to N entries. Will add nulls if the list is
     *  smaller than the desired size, discard trailing entries if larger.
     *  This is primarily used to presize a list that will be accessed by
     *  index, but it may also be used to truncate a list for display.
     *
     *  @return The list, as a convenience for callers
     *  @throws UnsupportedOperationException if the list does not implement
     *          <code>RandomAccess</code> and its list iterator does not
     *          support the <code>remove()</code> operation
     */
    public static <T> List<T> resize(List<T> list, int newSize)
    {
        return resize(list, newSize, null);
    }


    /**
     *  Iterates the passed collection, converts its elements to strings, then
     *  concatenates those strings with the specified delimiter between them.
     *  Nulls are converted to empty strings.
     *
     *  @since 1.0.2
     */
    public static <T> String join(Iterable<T> coll, String delim)
    {
        if (coll == null)
            return "";

        boolean isFirst = true;
        StringBuilder buf = new StringBuilder(1024);
        for (T item : coll)
        {
            if (isFirst)
                isFirst = false;
            else
                buf.append(delim);

            if (item != null)
                buf.append(String.valueOf(item));
        }
        return buf.toString();
    }


    /**
     *  Adds all elements of the <code>src</code> collections to <code>dest</code>,
     *  returning <code>dest</code>. This is typically used when you need to combine
     *  collections temporarily for a method argument.
     *
     *  @since 1.0.7
     */
    public static <T> List<T> combine(List<T> dest, Collection<T>... src)
    {
        for (Collection<T> cc : src)
        {
            dest.addAll(cc);
        }
        return dest;
    }


    /**
     *  Adds all elements of the <code>src</code> collections to <code>dest</code>,
     *  returning <code>dest</code>. This is typically used when you need to combine
     *  collections temporarily for a method argument.
     *
     *  @since 1.0.7
     */
    public static <T> Set<T> combine(Set<T> dest, Collection<T>... src)
    {
        for (Collection<T> cc : src)
        {
            dest.addAll(cc);
        }
        return dest;
    }


    /**
     *  Adds all elements of the <code>src</code> collections to <code>dest</code>,
     *  returning <code>dest</code>. This is typically used when you need to combine
     *  collections temporarily for a method argument.
     *  <p>
     *  Note: source maps are added in order; if the same keys are present in multiple
     *  sources, the last one wins.
     *
     *  @since 1.0.7
     */
    public static <K,V> Map<K,V> combine(Map<K,V> dest, Map<K,V>... src)
    {
        for (Map<K,V> cc : src)
        {
            dest.putAll(cc);
        }
        return dest;
    }


    /**
     *  Returns <code>true</code> if the passed collection is either <code>null</code>
     *  or has size 0.
     */
    public static boolean isEmpty(Collection<?> c)
    {
        return (c == null)
             ? true
             : (c.size() == 0);
    }


    /**
     *  Returns <code>true</code> if the passed collection is not <code>null</code>
     *  and has size &gt; 0.
     */
    public static boolean isNotEmpty(Collection<?> c)
    {
        return (c != null) && (c.size() > 0);
    }


    /**
     *  Compares two collections of <code>Comparable</code> elements. The two collections are
     *  iterated, and the first not-equal <code>compareTo()</code> result is returned. If the
     *  collections are of equal length and contain the same elements in iteration order, they
     *  are considered equal. If they are of unequal length but contain the same elements in
     *  iteration order, the shorter is considered less than the longer.
     *  <p>
     *  Note that two collections that are equal based on their intrinsic <code>equals()</code>
     *  method, but iterate in a different order (ie, hash-based collections) are not considered
     *  equal by this method.
     *
     *   @since 1.0.14
     */
    @SuppressWarnings("rawtypes")
    public static int compare(Collection<? extends Comparable> c1, Collection<? extends Comparable> c2)
    {
        Iterator<? extends Comparable> itx1 = c1.iterator();
        Iterator<? extends Comparable> itx2 = c2.iterator();

        while (itx1.hasNext())
        {
            if (! itx2.hasNext())
                return 1;

            Comparable v1 = itx1.next();
            Comparable v2 = itx2.next();
            int cmp = v1.compareTo(v2);
            if (cmp != 0)
                return cmp;
        }

        if (itx2.hasNext())
            return -1;
        else
            return 0;
    }


    /**
     *  Returns the second iterable if the first is null. This is used for a null-safe
     *  for loop.
     */
    public static <T> Iterable<T> defaultIfNull(Iterable<T> reg, Iterable<T> def)
    {
        return (reg == null) ? def : reg;
    }


    /**
     *  Returns the default collection if the regular object is null or empty.
     */
    public static <T> Collection<T> defaultIfEmpty(Collection<T> reg, Collection<T> def)
    {
        return ((reg == null) || (reg.size() == 0)) ? def : reg;
    }


    /**
     *  Applies the specified functor to every element of the given collection, in
     *  its natural iteration order, and returns a list of the results.
     *  <p>
     *  If the functor throws, it will be rethrown in a {@link CollectionUtil.MapException},
     *  which provides detailed information and partial work.
     *
     *  @since 1.0.10
     */
    public static <V,R> List<R> map(Collection<V> coll, IndexValueMapFunctor<V,R> functor)
    {
        List<R> result = new ArrayList<R>(coll.size());
        int index = 0;
        for (V value : coll)
        {
            try
            {
                result.add(functor.invoke(index, value));
                index++;
            }
            catch (Throwable ex)
            {
                throw new MapException(ex, index, value, result);
            }
        }
        return result;
    }


    /**
     *  Performs a parallel map operation. For each element in the source collection,
     *  a callable is dispatched to the passed <code>ExecutorService</code> to invoke
     *  the specified functor. The results are accumulated and returned as a list, in
     *  the order of the original collection's iterator.
     *  <p>
     *  If any element causes an exception, this method throws {@link CollectionUtil.MapException}.
     *  While that exception returns partial results, there is no guarantee that the
     *  results represent a particular range of the source collection.
     *  <p>
     *  This method will wait until all of the elements of the collection have been
     *  processed, unless it is interrupted. If multiple invocations threw, one will
     *  be chosen arbitrarily; there is no guarantee that it represents the first
     *  collection element to cause an exception.
     *
     *  @since 1.0.10
     */
    public static <V,R> List<R> map(ExecutorService threadpool, Collection<V> values, final IndexValueMapFunctor<V,R> functor)
    throws InterruptedException
    {
        int count = values.size();
        ArrayList<V> values2 = new ArrayList<V>(values);
        ArrayList<Future<R>> futures = new ArrayList<Future<R>>(count);

        ArrayList<R> results = new ArrayList<R>();
        resize(results, count);

        for (int ii = 0 ; ii < count ; ii++)
        {
            final int index = ii;
            final V value = values2.get(ii);
            futures.add(threadpool.submit(new Callable<R>()
            {
                public R call() throws Exception
                {
                    return functor.invoke(index, value);
                }
            }));
        }

        int failureIndex = 0;
        Throwable failureException = null;
        for (int ii = 0 ; ii < count ; ii++)
        {
            Future<R> future = futures.get(ii);
            try
            {
                results.set(ii, future.get());
            }
            catch (CancellationException ex)
            {
                // I don't think we can ever get this exception, since we
                // don't let the Future escape (and immediate shutdown of
                // the pool should create an ExecutionException); but, we
                // should treat it differently if we ever do get it
                failureIndex = ii;
                failureException = ex;
            }
            catch (ExecutionException ex)
            {
                failureIndex = ii;
                failureException = ex.getCause();
            }
        }

        if (failureException != null)
            throw new MapException(failureException, failureIndex, values2.get(failureIndex), results);
        else
            return results;
    }


    /**
     *  Applies the specified functor to every element in the given collection, with
     *  the expectation that it will return a single value based on the item and any
     *  previous value.
     *
     *  @since 1.0.10
     */
    public static <V,R> R reduce(Collection<V> coll, IndexValueReduceFunctor<V,R> functor)
    {
        R pendingResult = null;
        int index = 0;
        for (V value : coll)
        {
            try
            {
                pendingResult = functor.invoke(index, value, pendingResult);
                index++;
            }
            catch (Throwable ex)
            {
                throw new ReduceException(ex, index, value, pendingResult);
            }
        }
        return pendingResult;
    }


    /**
     *  Applies the specified predicate functor to every element of a collection,
     *  in its natural iteration order, and returns a list containing only those
     *  elements for which the predicate returned <code>true</code>.
     *  <p>
     *  If the functor throws, it will be rethrown in a {@link CollectionUtil.FilterException},
     *  which provides detailed information and partial work.
     *
     *  @since 1.0.11
     */
    public static <V> List<V> filter(Collection<V> coll, Predicate<V> predicate)
    {
        List<V> result = new ArrayList<V>(coll.size());
        int index = 0;
        for (V value : coll)
        {
            try
            {
                if (predicate.invoke(index, value))
                {
                    result.add(value);
                }
                index++;
            }
            catch (Throwable ex)
            {
                throw new FilterException(ex, index, value, result);
            }
        }
        return result;
    }


    /**
     *  Applies the given regex to the string value of every item in the passed
     *  list, building a new list from those value that either match or do not
     *  match. Null entries are treated as an empty string for matching, but
     *  will be returned as null.
     *
     *  @since 1.0.3
     *
     *  @param  list    The source list; this is unmodified.
     *  @param  regex   Regex applied to every string in the list.
     *  @param  include If <code>true</code>, strings that match are copied
     *                  to the output list; if <code>false</code>, strings
     *                  that don't match are copied.
     */
    public static <T> List<T> filter(List<T> list, String regex, final boolean include)
    {
        final Pattern pattern = Pattern.compile(regex);
        return filter(list, new Predicate<T>()
        {
            public boolean invoke(int index, T value) throws Exception
            {
                String str = (value == null) ? "" : value.toString();
                return pattern.matcher(str).matches() == include;
            }
        });
    }


//----------------------------------------------------------------------------
//  Supporting Objects
//----------------------------------------------------------------------------

    /**
     *  A functor interface for {@link #map}. The {@link #invoke} function is
     *  called for every element in the collection, and is passed the element
     *  value and its position (0-based) in the iteration order.
     *  <p>
     *  The implementation is permitted to throw anything, checked or not.
     *
     *  @since 1.0.10
     */
    public interface IndexValueMapFunctor<V,R>
    {
        public R invoke(int index, V value)
        throws Exception;
    }


    /**
     *  An exception wrapper for {@link #map}. Contains the wrapped exception,
     *  the value and index that caused the exception, and the results-to-date.
     *  <p>
     *  Note: because Java does not allow parameterization of <code>Throwable</code>
     *  subclasses (JLS 8.1.2), the value and results are held as <code>Object</code>s.
     *
     *  @since 1.0.10
     */
    public static class MapException
    extends RuntimeException
    {
        private static final long serialVersionUID = 1;

        private int _index;
        private Object _value;
        private List<?> _partialResults;

        public MapException(Throwable cause, int index, Object value, List<?> partialResults)
        {
            super(cause);
            _index = index;
            _value = value;
            _partialResults = partialResults;
        }

        /**
         *  Returns the position (0-based) in the original collection's iteration where the
         *  wrapped exception was thrown.
         */
        public int getIndex()
        {
            return _index;
        }

        /**
         *  Returns the value that caused the exception.
         */
        public Object getValue()
        {
            return _value;
        }

        /**
         *  Returns any partial results from the map operation.
         *  <p>
         *  Warning: the contents of this list are undefined in the case of a parallel map
         *  operation.
         */
        public List<?> getPartialResults()
        {
            return _partialResults;
        }
    }


    /**
     *  A functor used for the {@link #reduce} operation. The {@link #invoke}
     *  function is called for every element of a collection, and is responsible
     *  for aggregating the results. On the first invocation, the "pending"
     *  result is <code>null</code>; on subsequent invocations, it is the value
     *  returned from the previous invocation.
     *
     *  @since 1.0.10
     */
    public interface IndexValueReduceFunctor<V, R>
    {
        public R invoke(int index, V value, R pendingResult)
        throws Exception;
    }


    /**
     *  An exception wrapper for {@link #reduce}. Contains the wrapped exception,
     *  the value and index that caused the exception, and the results-to-date.
     *  <p>
     *  Note: because Java does not allow parameterization of <code>Throwable</code>
     *  subclasses (JLS 8.1.2), the value and results are held as <code>Object</code>s.
     *
     *  @since 1.0.10
     */
    public static class ReduceException
    extends RuntimeException
    {
        private static final long serialVersionUID = 1;

        private int _index;
        private Object _value;
        private Object _partialResults;

        public ReduceException(Throwable cause, int index, Object value, Object partialResults)
        {
            super(cause);
            _index = index;
            _value = value;
            _partialResults = partialResults;
        }

        /**
         *  Returns the position (0-based) in the original collection's iteration where the
         *  wrapped exception was thrown.
         */
        public int getIndex()
        {
            return _index;
        }

        /**
         *  Returns the value that caused the exception.
         */
        public Object getValue()
        {
            return _value;
        }

        /**
         *  Returns any partial results. This is the <code>pendingResult</code>
         *  value passed to the functor at the time the exception was thrown.
         */
        public Object getPartialResults()
        {
            return _partialResults;
        }
    }


    /**
     *  Implement this for the {@link #filter} operation: return <code>true</code> to
     *  include an element in the result, <code>false</code> to skip it. Implementations
     *  may throw exceptions; these will cause the filter to stop processing.
     *  <p>
     *  Purists may object, but each invocation is given the element index, in iteration
     *  order.
     *
     *  @since 1.0.11
     */
    public interface Predicate<V>
    {
        public boolean invoke(int index, V value)
        throws Exception;
    }


    /**
     *  An exception wrapper for {@link #filter}. Contains the wrapped exception,
     *  the value and index that caused the exception, and the results-to-date.
     *  <p>
     *  Note: because Java does not allow parameterization of <code>Throwable</code>
     *  subclasses (JLS 8.1.2), the value and results are held as <code>Object</code>s.
     *
     *  @since 1.0.11
     */
    public static class FilterException
    extends RuntimeException
    {
        private static final long serialVersionUID = 1;

        private int _index;
        private Object _value;
        private List<?> _partialResults;

        public FilterException(Throwable cause, int index, Object value, List<?> partialResults)
        {
            super(cause);
            _index = index;
            _value = value;
            _partialResults = partialResults;
        }

        /**
         *  Returns the position (0-based) in the original collection's iteration where the
         *  wrapped exception was thrown.
         */
        public int getIndex()
        {
            return _index;
        }

        /**
         *  Returns the value that caused the exception.
         */
        public Object getValue()
        {
            return _value;
        }

        /**
         *  Returns any partial results from the map operation.
         *  <p>
         *  Warning: the contents of this list are undefined in the case of a parallel map
         *  operation.
         */
        public List<?> getPartialResults()
        {
            return _partialResults;
        }
    }
}
