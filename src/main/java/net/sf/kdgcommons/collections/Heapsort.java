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
 *  Implementations of Heapsort for various array/collection types. Of particular
 *  note, this class includes an <code>int[]</code> sort that uses an external
 *  comparator (unlike the JDK).
 *  <p>
 *  Heapsort is an in-place sort. Unlike the mergesort used by the JDK's
 *  <code>Collections.sort()</code> and <code>Arrays.sort(Object[])</code>
 *  methods, it does not need to create a working array (which can use up a
 *  surprisingly large amount of memory).
 *  <p>
 *  On the down-side, Heapsort is not a stable sort (objects that compare equal
 *  may occupy different locations in subsequent sorts). It is also slower than
 *  the JDK's sorts (by a factor of 3 for object sorts, a factor of 5 for
 *  integer sorts).
 */
public class Heapsort
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
     *  The sort implementation accesses the collection using an instance of
     *  this interface. This allows, on the one hand, a way to avoid duplicate
     *  sort code. On the other, it allows direct manipulation of collections
     *  that aren't array-based.
     */
    private interface Accessor
    {
        public int size();
        public int compare(int index1, int index2);
        public void swap(int index1, int index2);
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Sorts a primitive integer array using an external comparator.
     */
    public static void sort(int[] array, IntComparator comparator)
    {
        sort(array, 0, array.length, comparator);
    }


    /**
     *  Sorts a portion of a primitive integer array using an external comparator.
     */
    public static void sort(int[] array, int off, int len, IntComparator comparator)
    {
        sort(new IntArrayAccessor(array, comparator), off, len);
    }


    /**
     *  Sorts an object array using natural ordering. You would use this method
     *  rather than <code>Arrays.sort()</code> to avoid creation of a working
     *  array, which will consume 4/8 bytes per element.
     */
    public static <T extends Comparable<T>> void sort(T[] array)
    {
        sort(array, new ComparableComparator<T>());
    }


    /**
     *  Sorts an object array using the provided comparator.
     */
    public static <T extends Object> void sort(T[] array, Comparator<T> cmp)
    {
        sort(array, 0, array.length, cmp);
    }


    /**
     *  Sorts a section of an object array using the provided comparator.
     */
    public static <T extends Object> void sort(T[] array, int off, int len, Comparator<T> cmp)
    {
        sort(new ObjectArrayAccessor<T>(array, cmp), off, len);
    }


    /**
     *  Sorts a list using natural ordering. Only appropriate for random-access
     *  lists. You would use this method rather than <code>Collections.sort()</code>
     *  to avoid creation of working arrays, which will consume 4/8 bytes per
     *  element.
     */
    public static <T extends Comparable<T>> void sort(List<T> list)
    {
        sort(list, new ComparableComparator<T>());
    }


    /**
     *  Sorts a list using the provided comparator. Only appropriate for
     *  random-access lists.
     */
    public static <T extends Object> void sort(List<T> list, Comparator<T> cmp)
    {
        sort(list, 0, list.size(), cmp);
    }


    /**
     *  Sorts a portion of a list using the provided comparator. Only appropriate
     *  for random-access lists.
     */
    public static <T extends Object> void sort(List<T> list, int off, int len, Comparator<T> cmp)
    {
        sort(new ListAccessor<T>(list, cmp), off, len);
    }


    /**
     *  Sorts a collection encapsulated by the provided <code>Accessor</code>.
     *  This is used to physically sort a collection that is not backed by an
     *  array (eg, a memory-mapped file).
     */
    public static void sort(Accessor acc)
    {
        sort(acc, 0, acc.size());
    }


    /**
     *  Sorts a subset of the collection encapsulated by the provided
     *  <code>Accessor</code>. This is used to physically sort a collection
     *  that is not backed by an array (eg, a memory-mapped file).
     */
    public static void sort(Accessor acc, int off, int len)
    {
        for (int end = off+1 ; end < off + len ; end++)
            siftUp(acc, end);

        for (int end = off + len - 1 ; end >= off ; )
        {
            acc.swap(0, end);
            siftDown(acc, --end);
        }
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------


    /**
     *  Accessor implementation for <code>int[]</code>.
     */
    private static class IntArrayAccessor
    implements Accessor
    {
        private int[] _array;
        private IntComparator _comparator;

        public IntArrayAccessor(int[] array, IntComparator comparator)
        {
            _array = array;
            _comparator = comparator;
        }

        public int size()
        {
            return _array.length;
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
        private Comparator<T> _comparator;

        public ObjectArrayAccessor(T[] array, Comparator<T> comparator)
        {
            _array = array;
            _comparator = comparator;
        }

        public int size()
        {
            return _array.length;
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
        private Comparator<T> _comparator;

        public ListAccessor(List<T> list, Comparator<T> comparator)
        {
            _list = list;
            _comparator = comparator;
        }

        public int size()
        {
            return _list.size();
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


    /**
     *  Extends an already-constructed max-heap one element to the right.
     */
    private static void siftUp(Accessor acc, int end)
    {
        while (end > 0)
        {
            int parent = (end - 1) / 2;
            if (acc.compare(parent, end) > 0)
                break;
            acc.swap(parent, end);
            end = parent;
        }
    }


    /**
     *  Re-creates a mex-heap over the range <code>0 .. end</code> by moving
     *  the first element to its proper position.
     */
    private static void siftDown(Accessor acc, int end)
    {
        for (int parent = 0 ; parent < end ; )
        {
            int child1 = parent * 2 + 1;
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
