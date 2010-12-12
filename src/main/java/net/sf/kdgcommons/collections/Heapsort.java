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
 *  Implementations of heapsort for a variety of data structures. Heapsort is an
 *  in-place sort (albeit not stable), which is useful in memory-constrained
 *  situations.
 *  <p>
 *  Of particular interest, this class supports sorting <code>int</code> arrays
 *  with an external comparator. This is useful when the array represents an
 *  index into another data structure.
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
     *  Sorts a primitive integer array using an external comparator.
     */
    public static void sort(int[] array, IntComparator comparator)
    {
        heapsort(new IntArrayAccessor(array, comparator));
    }


    /**
     *  Sorts an object array using natural ordering.
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
        heapsort(new ObjectArrayAccessor<T>(array, cmp));
    }


    /**
     *  Sorts a list using natural ordering. Only appropriate for
     *  random-access lists.
     */
    public static <T extends Comparable<T>> void sort(List<T> list)
    {
        sort(list, new ComparableComparator<T>());
    }


    /**
     *  Sorts an object array using the provided comparator.
     */
    public static <T extends Object> void sort(List<T> list, Comparator<T> cmp)
    {
        heapsort(new ListAccessor<T>(list, cmp));
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  We abstract away the array manipulations into instances of this
     *  interface so that we don't have to replicate the sort code.
     */
    private interface Accessor
    {
        public int size();
        public int compare(int index1, int index2);
        public void swap(int index1, int index2);
    }


    /**
     *  The core sort routine.
     */
    private static void heapsort(Accessor acc)
    {
        for (int end = 1 ; end < acc.size() ; end++)
            siftUp(acc, end);

        for (int end = acc.size() - 1 ; end >= 0 ; )
        {
            acc.swap(0, end);
            siftDown(acc, --end);
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
