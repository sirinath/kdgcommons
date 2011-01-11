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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;


public class TestSortUtil
extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    // the "big" tests will all start with a random int[]
    private static int[] createRandomArray(int size)
    {
        int[] arr = new int[size];
        for (int ii = 0 ; ii < arr.length ; ii++)
            arr[ii] = (int)(Math.random() * Integer.MAX_VALUE);
        return arr;
    }


    // and will need to compare to an already-sorted array
    private static int[] createSortedCopy(int[] src)
    {
        int[] ret = new int[src.length];
        System.arraycopy(src, 0, ret, 0, src.length);
        Arrays.sort(ret);
        return ret;
    }


    // and we'll convert these to objects if needed
    private static Integer[] toObjectArray(int[] src)
    {
        Integer[] ret = new Integer[src.length];
        for (int ii = 0 ; ii < src.length ; ii++)
            ret[ii] = Integer.valueOf(src[ii]);
        return ret;
    }


    // we'll throw a twist into the ordering for the int[] tess
    public static class ReversingIntComparator
    implements SortUtil.IntComparator
    {
        public int compare(int i1, int i2)
        {
            return (i1 > i2) ? -1
                 : (i1 < i2) ? 1
                 : 0;
        }
    }


    // this will be used to verify the O(NlogN) property; it orders by
    // increasing values so we can compare result to Arrays.sort()
    public static class CountingIntComparator
    implements SortUtil.IntComparator
    {
        public int count;
        public int expectedCount;

        public CountingIntComparator(int size)
        {
            // our implementation of heapsort should perform at most 3 compares per element
            expectedCount = 3 * size * (int)Math.ceil(Math.log(size) / Math.log(2));
        }

        public int compare(int i1, int i2)
        {
            count++;
            return (i1 < i2) ? -1
                 : (i1 > i2) ? 1
                 : 0;
        }

        public void assertCompareCount()
        {
            assertTrue("expected > 0", count > 0);
            assertTrue("expected < " + expectedCount + ", was " + count,
                       count < expectedCount);
        }
    }


    // but use a straightforward comparison for integers
    public static class ForwardComparator<T extends Comparable<T>>
    implements Comparator<T>
    {
        public int compare(T o1, T o2)
        {
            return o1.compareTo(o2);
        }
    }


    private void assertEquals(int[] expected, int[] actual)
    {
        // we'll convert to List<Integer> because the reporting is nicer

        ArrayList<Integer> expected2 = new ArrayList<Integer>(expected.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
            expected2.add(Integer.valueOf(expected[ii]));

        ArrayList<Integer> actual2 = new ArrayList<Integer>(expected.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
            actual2.add(Integer.valueOf(actual[ii]));

        assertEquals(expected2, actual2);
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testIntSortEmptyArray() throws Exception
    {
        int[] src = new int[0];
        int[] exp = new int[0];

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testIntSortOneElement() throws Exception
    {
        int[] src = new int[] { 3 };
        int[] exp = new int[] { 3 };

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testIntSortTwoElements() throws Exception
    {
        int[] src = new int[] { 3, 5 };
        int[] exp = new int[] { 5, 3 };

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testIntSortThreeElements() throws Exception
    {
        int[] src = new int[] { 5, 3, 4 };
        int[] exp = new int[] { 5, 4, 3 };

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testIntSortFourElements() throws Exception
    {
        int[] src = new int[] { 5, 3, 4, 12 };
        int[] exp = new int[] { 12, 5, 4, 3 };

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    // this test added for internal comparator coverage; it won't affect
    // affect coverage of Heapsort itself, but I want to cover all cases
    public void testIntSortWithEqualElements() throws Exception
    {
        int[] src = new int[] { 5, 3, 3, 4, 12 };
        int[] exp = new int[] { 12, 5, 4, 3, 3 };

        SortUtil.sort(src, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testIntSortManyElements() throws Exception
    {
        final int size = 10000;

        int[] src = createRandomArray(size);
        int[] exp = createSortedCopy(src);

        CountingIntComparator cmp = new CountingIntComparator(size);
        SortUtil.sort(src, cmp);
        assertEquals(exp, src);

        cmp.assertCompareCount();
    }


    public void testIntSortPortionOfArray() throws Exception
    {
        int[] src = new int[] { 5, 3, 2, 4, 12 };
        int[] exp = new int[] { 5, 4, 3, 2, 12 };

        SortUtil.sort(src, 1, 3, new ReversingIntComparator());
        assertEquals(exp, src);
    }


    public void testObjectSort() throws Exception
    {
        int[] base = createRandomArray(1000);
        Integer[] src = toObjectArray(base);
        Integer[] exp = toObjectArray(createSortedCopy(base));

        // coverage note: this call is delegated to all other variants
        SortUtil.sort(src);
        assertEquals(Arrays.asList(exp), Arrays.asList(src));
    }


    public void testListSort() throws Exception
    {
        List<Integer> base = Arrays.asList(toObjectArray(createRandomArray(1000)));
        List<Integer> src = new ArrayList<Integer>(base);
        List<Integer> exp = new ArrayList<Integer>(base);
        Collections.sort(exp);

        // coverage note: this call is delegated to all other variants
        SortUtil.sort(src);
        assertEquals(exp, src);
    }

}
