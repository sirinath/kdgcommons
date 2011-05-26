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

import java.util.Comparator;
import java.util.List;


/**
 *  A collection of static sort routines for random-access data structures
 *  that provide the following characteristics:
 *  <ul>
 *  <li> O(NlogN) performance
 *  <li> In-place operation; unlike the JDK's list/array sort, does not consume
 *       additional heap memory
 *  <li> Allows a comparator for sorting primitive <code>int</code>s
 *  <li> Supports sorting of arbitrary data structures (eg, records stored in an
 *       of-heap buffer)
 *  <li> May <em>not</em> be stable (the current implementation is heapsort,
 *       which is not stable)
 *  </u>
 */
public class InplaceSort
{
    /**
     *  Implementations of this class compare two primitive <code>int</code>s,
     *  returning the same values as <code>java.util.Comparator</code>.
     */
    public interface IntComparator
    {
        public int compare(int i1, int i2);
    }


    /**
     *  The search uses an implementation of this interface to access the
     *  array-like structure.
     *  <p>
     *  This interface is intended to be compatible  with the like-named interface
     *  defined in {@link BinarySearch}, allowing a single implementation for use
     *  within a complex data structure.
     */
    public interface Accessor
    {
        /**
         *  The minimum bound of the search (inclusive). Normally this is 0, but
         *  it may vary to search a subset of the array.
         */
        public int start();

        /**
         *  The maximum bound of the search (exclusive). Normally this is the
         *  length of the array, but may be a lower value.
         */
        public int end();

        /**
         *  Compares the values at two positions within the array-like structure.
         *  Returns the normal comparator values: &lt; 0, 0, &gt; 0.
         */
        public int compare(int index1, int index2);

        /**
         *  Swaps the values at two positions within the array-like structure.
         */
        public void swap(int index1, int index2);
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Sorts a primitive integer array using an external comparator.
     *  <p>
     *  This functionality is not provided by the JDK.
     *
     *  @param  array       The array to be sorted
     *  @param  comparator  Used to order array elements
     */
    public static void sort(int[] array, IntComparator comparator)
    {
        sort(new IntArrayAccessor(array, 0, array.length, comparator));
    }


    /**
     *  Sorts a portion of a primitive integer array using an external comparator.
     *  <p>
     *  This functionality is not provided by the JDK.
     *
     *  @param  array       The array to be sorted
     *  @param  fromIndex   The minimum bound of the search (inclusive)
     *  @param  toIndex     The maximum bound of the search (exclusive, following
     *                      similar usage in the JDK; to sort the end of the array,
     *                      pass <code>array.length</code>)
     *  @param  comparator  Used to order array elements
     */
    public static void sort(int[] array, int fromIndex, int toIndex, IntComparator comparator)
    {
        sort(new IntArrayAccessor(array, fromIndex, toIndex, comparator));
    }


    /**
     *  Sorts an object array using natural ordering.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid creating
     *  the working array used by the JDK's <code>Arrays.sort()</code> (a cost of 4 or
     *  8 bytes per element, depending on JVM).
     *
     *  @param  array       The array to be sorted
     */
    public static <T extends Comparable<T>> void sort(T[] array)
    {
        sort(array, new ComparableComparator<T>());
    }


    /**
     *  Sorts a portion of an object array using natural ordering.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid creating
     *  the working array used by the JDK's <code>Arrays.sort()</code> (a cost of 4 or
     *  8 bytes per element, depending on JVM).
     *
     *  @param  array       The array to be sorted
     *  @param  fromIndex   The minimum bound of the search (inclusive)
     *  @param  toIndex     The maximum bound of the search (exclusive, following
     *                      similar usage in the JDK; to sort the end of the array,
     *                      pass <code>array.length</code>)
     */
    public static <T extends Comparable<T>> void sort(T[] array, int fromIndex, int toIndex)
    {
        sort(array, fromIndex, toIndex, new ComparableComparator<T>());
    }


    /**
     *  Sorts an object array using the provided comparator.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid creating
     *  the working array used by the JDK's <code>Arrays.sort()</code> (a cost of 4 or
     *  8 bytes per element, depending on JVM).
     *
     *  @param  array       The array to be sorted
     *  @param  comparator  Used to order array elements
     */
    public static <T extends Object> void sort(T[] array, Comparator<T> cmp)
    {
        sort(new ObjectArrayAccessor<T>(array, 0, array.length, cmp));
    }


    /**
     *  Sorts a portion of an object array using the provided comparator.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid creating
     *  the working array used by the JDK's <code>Arrays.sort()</code> (a cost of 4 or
     *  8 bytes per element, depending on JVM).
     *
     *  @param  array       The array to be sorted
     *  @param  fromIndex   The minimum bound of the search (inclusive)
     *  @param  toIndex     The maximum bound of the search (exclusive, following
     *                      similar usage in the JDK; to sort the end of the array,
     *                      pass <code>array.length</code>)
     *  @param  comparator  Used to order array elements
     */
    public static <T extends Object> void sort(T[] array, int fromIndex, int toIndex, Comparator<T> cmp)
    {
        sort(new ObjectArrayAccessor<T>(array, fromIndex, toIndex, cmp));
    }


    /**
     *  Sorts a list using natural ordering. This method is only appropriate for
     *  random-access lists.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid
     *  creating the working array used by the JDK's <code>Collections.sort()</code>
     *  (a cost of 8 or 16 bytes per element, depending on JVM).
     *
     *  @param  list        The list to be sorted
     */
    public static <T extends Comparable<T>> void sort(List<T> list)
    {
        sort(list, new ComparableComparator<T>());
    }


    /**
     *  Sorts a portion of a list using natural ordering. This method is only
     *  appropriate for random-access lists.
     *  <p>
     *  This method does not have an equivalent in the JDK.
     *
     *  @param  list        The list to be sorted
     *  @param  fromIndex   The minimum bound of the search (inclusive)
     *  @param  toIndex     The maximum bound of the search (exclusive, following
     *                      similar usage in the JDK; to sort the end of the list,
     *                      pass <code>list.size()</code>)
     */
    public static <T extends Comparable<T>> void sort(List<T> list, int fromIndex, int toIndex)
    {
        sort(list, fromIndex, toIndex, new ComparableComparator<T>());
    }


    /**
     *  Sorts a list using the provided comparator. This method is only appropriate
     *  for random-access lists.
     *  <p>
     *  You would use this method in a constrained-memory situation, to avoid
     *  creating the working array used by the JDK's <code>Collections.sort()</code>
     *  (a cost of 8 or 16 bytes per element, depending on JVM).
     *
     *  @param  list        The list to be sorted
     *  @param  comparator  Used to order list elements
     */
    public static <T extends Object> void sort(List<T> list, Comparator<T> cmp)
    {
        sort(new ListAccessor<T>(list, 0, list.size(), cmp));
    }


    /**
     *  Sorts a portion of a list using natural ordering. This method is only
     *  appropriate for random-access lists.
     *  <p>
     *  This method does not have an equivalent in the JDK.
     *
     *  @param  list        The list to be sorted
     *  @param  fromIndex   The minimum bound of the search (inclusive)
     *  @param  toIndex     The maximum bound of the search (exclusive, following
     *                      similar usage in the JDK; to sort the end of the list,
     *                      pass <code>list.size()</code>)
     *  @param  comparator  Used to order list elements
     */
    public static <T extends Object> void sort(List<T> list, int fromIndex, int toIndex, Comparator<T> cmp)
    {
        sort(new ListAccessor<T>(list, fromIndex, toIndex, cmp));
    }


    /**
     *  Sorts a collection encapsulated by the provided {@link #Accessor}. This
     *  is used to physically sort a collection that is not backed by an array
     *  (eg, a memory-mapped file).
     *  <p>
     *  Since the <code>Accessor</code> includes both range and comparator, this
     *  is the only variant of this method.
     */
    public static void sort(Accessor acc)
    {
        int start = acc.start();
        int end = acc.end();

        for (int ii = start+1 ; ii < end ; ii++)
            siftUp(acc, start, ii);

        for (int ii = end - 1 ; ii >= start ; )
        {
            acc.swap(start, ii);
            siftDown(acc, start, --ii);
        }
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  Extends an already-constructed max-heap one element to the right.
     */
    private static void siftUp(Accessor acc, int start, int end)
    {
        while (end > start)
        {
            int parent = start + (end - start - 1) / 2;
            if (acc.compare(parent, end) > 0)
                break;
            acc.swap(parent, end);
            end = parent;
        }
    }


    /**
     *  Re-creates a mex-heap over the range <code>start .. end</code> by moving
     *  the first element to its proper position.
     */
    private static void siftDown(Accessor acc, int start, int end)
    {
        for (int parent = start ; parent < end ; )
        {
            int child1 = start + (parent - start) * 2 + 1;
            int child2 = child1 + 1;
            int child = (child2 > end) ? child1
                      : (acc.compare(child1, child2) < 0) ? child2 : child1;
            if (child > end)
                break;
            if (acc.compare(parent, child) < 0)
                acc.swap(parent, child);
            parent = child;
        }
    }


//----------------------------------------------------------------------------
//  Accessor implementations to support built-in collection types
//----------------------------------------------------------------------------

    /**
     *  Accessor implementation for <code>int[]</code>.
     */
    private static class IntArrayAccessor
    implements Accessor
    {
        private int[] _array;
        private int _start;
        private int _end;
        private IntComparator _comparator;

        public IntArrayAccessor(int[] array, int start, int end, IntComparator comparator)
        {
            _array = array;
            _start = start;
            _end = end;
            _comparator = comparator;
        }

        public int start()
        {
            return _start;
        }

        public int end()
        {
            return _end;
        }

        public int compare(int index1, int index2)
        {
            return _comparator.compare(_array[index1], _array[index2]);
        }

        public void swap(int index1, int index2)
        {
            int tmp = _array[index1];
            _array[index1] = _array[index2];
            _array[index2] = tmp;
        }
    }


    /**
     *  Accessor implementation for <code>Object[]</code>.
     */
    private static class ObjectArrayAccessor<T extends Object>
    implements Accessor
    {
        private T[] _array;
        private int _start;
        private int _end;
        private Comparator<T> _comparator;

        public ObjectArrayAccessor(T[] array, int start, int end, Comparator<T> comparator)
        {
            _array = array;
            _start = start;
            _end = end;
            _comparator = comparator;
        }

        public int start()
        {
            return _start;
        }

        public int end()
        {
            return _end;
        }

        public int compare(int index1, int index2)
        {
            return _comparator.compare(_array[index1], _array[index2]);
        }

        public void swap(int index1, int index2)
        {
            T tmp = _array[index1];
            _array[index1] = _array[index2];
            _array[index2] = tmp;
        }
    }


    /**
     *  Accessor implementation for <code>List</code>.
     */
    private static class ListAccessor<T extends Object>
    implements Accessor
    {
        private List<T> _list;
        private int _start;
        private int _end;
        private Comparator<T> _comparator;

        public ListAccessor(List<T> list, int start, int end, Comparator<T> comparator)
        {
            _list = list;
            _start = start;
            _end = end;
            _comparator = comparator;
        }

        public int start()
        {
            return _start;
        }

        public int end()
        {
            return _end;
        }

        public int compare(int index1, int index2)
        {
            return _comparator.compare(_list.get(index1), _list.get(index2));
        }

        public void swap(int index1, int index2)
        {
            T tmp = _list.get(index1);
            _list.set(index1, _list.get(index2));
            _list.set(index2, tmp);
        }
    }


//----------------------------------------------------------------------------
//  Miscellaneous Utility Classes
//----------------------------------------------------------------------------

    private static class ComparableComparator<T extends Comparable<T>>
    implements Comparator<T>
    {
        public int compare(T o1, T o2)
        {
            return o1.compareTo(o2);
        }
    }
}
