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


/**
 *  A collection of static methods for searching array-like data structures:
 *  those that can be accessed using an integer index. Examples of such structures
 *  include a <code>java.nio.LongBuffer</code>, or a file consisting of fixed-size
 *  records.
 *  <p>
 *  There are two ways to search these structures. For structures that are sorted,
 *  you can implement {@link BinarySearch.Accessor}: the search will provide that
 *  interface with indexes to compare. Or, you can create an <code>int[]</code>
 *  that holds sorted indexes into the original data structure, and implement
 *  {@link BinarySearch.IndexedComparator}: the search will provide the comparator
 *  with indexes retrieved from that array.
 *  <p>
 *  In either case, you must implement a <code>compare()</code> method that
 *  compares an actual object instance against the instance stored at a particular
 *  index in your data structure.
 *  <p>
 *  As with <code>Arrays.binarySearch()</code>, each of these methods returns
 *  the index if successful, <code>(-(insertionPoint) - 1)</code> if not. The
 *  specific meaning of <em>insertionPoint</code> will vary depending on the
 *  method called; see the method docs for details.
 */
public class BinarySearch
{
    /**
     *  Implement this interface if you're searching an already-sorted array-like
     *  structure. You can define accessors that look at the entire structure, or
     *  at a partial structure, depending on how you implement {@link #start} and
     *  {@link #end}.
     *  <p>
     *  It is intended to be compatible  with the like-named interface defined by
     *  {@link InplaceSort}, allowing a single implementation class for both.
     */
    public interface Accessor<T>
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
         *  Compares an explicit value against the value at a given location in
         *  the array-like structure. Returns the normal comparator values:
         *  &lt;0, 0, &gt;0.
         */
        public int compare(T value, int index);
    }


    /**
     *  Implement this interface if you have an <code>int[]</code> that contains
     *  sorted indexes into the array-like structure.
     */
    public interface IndexedComparator<T>
    {
        /**
         *  Compares an explicit value against the value at a given location in
         *  the array-like structure. Returns the normal comparator values:
         *  &lt;0, 0, &gt;0.
         */
        public int compare(T value, int index);
    }


//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Searches a sorted array-like structure using the provided
     *  <code>Accessor</code>. If the object is found, the returned value will
     *  be the object's location within that structure.
     *  <p>
     *  If the object is not found, the value of the returned insertion point
     *  will depend on the accessor's defined range. If the accessor's range
     *  covers the entire structure, or if the object would appear within the
     *  defined range, then the returned insertion point will indicate the
     *  position where the object should be inserted. If the accessor covers
     *  a subset of the structure, and the missing object would appear outside
     *  that structure, then the insertion point will be just before or just
     *  after the defined range, <em>not</e> the absolute position in the array.
     *  <p>
     *  This is best illustrated with examples.
     *  Given the array <code>['B', 'D', 'F', 'H', 'J', 'L', 'N', 'P', 'R']</code>:
     *  <ul>
     *  <li> A search for the value 'C', covering the entire range, would return
     *       -2. This is the same behavior as the JDK's built-in searches.
     *  <li> A search for the value 'C', covering elements 2..4, would return
     *       -3. This is because the search doesn't know of any values outside
     *       the range, so it would indicate that the value should be inserted
     *       just before the range.
     *  <li> A search for the value 'B', covering the elements 2..4, would also
     *       return -3. Although 'B' appears in the complete array, it is not in
     *       the search range. And, like 'C', the search only knows that it should
     *       be inserted before the range.
     *  <li> A search for the value 'F', covering the elements 2..4, could
     *       return +2.
     *  <li> A search for the value 'G', covering the elements 2..4, would
     *       return -4. This insertion point is within the range being searched,
     *       so is accurate.
     *  <li> A search for the value 'J', covering the elements 2..4, would
     *       return -5. This is because, although the value of element 4 is 'J',
     *       the upper bound of the range is exclusive. So the search can't find
     *       the value, and reports that it would be inserted after the range.
     *  <li> A search for the value 'L', covering the elements 2..4, would also
     *       return -5, indicating that the value could not be found within the
     *       bounds of the search (even though it exists outside those bounds).
     *  </ul>
     */
    public static <T> int search(Accessor<T> accessor, T value)
    {
        int min = accessor.start();
        int max = accessor.end() - 1;

        if (max < min)    // empty array check
            return -1;

        while (max > min)
        {
            int mid = min + (max - min) / 2;
            if (accessor.compare(value, mid) <= 0)
                max = mid;
            else
                min = mid + 1;
        }

        int rslt = accessor.compare(value, min);
        return (rslt == 0) ? min
             : (rslt < 0)  ? -min - 1
                           : -(min+1) - 1;
    }


    /**
     *  Searches sorted array of indexes into some other array-like object (which
     *  does not need to be sorted). Returns the position/insertion point of the
     *  requested value <em>within the index array</em>.
     */
    public static <T> int search(int[] index, T value, IndexedComparator<T> cmp)
    {
        return search(new IndexedAccessor<T>(index, cmp), value);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static class IndexedAccessor<T>
    implements Accessor<T>
    {
        private int[] _array;
        private IndexedComparator<T> _cmp;

        public IndexedAccessor(int[] array, IndexedComparator<T> cmp)
        {
            _array = array;
            _cmp = cmp;
        }

        public int start()
        {
            return 0;
        }

        public int end()
        {
            return _array.length;
        }

        public int compare(T value, int index)
        {
            return _cmp.compare(value, _array[index]);
        }
    }
}
