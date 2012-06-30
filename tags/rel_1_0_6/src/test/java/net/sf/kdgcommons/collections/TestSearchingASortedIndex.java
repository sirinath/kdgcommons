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

import java.util.Random;

import junit.framework.TestCase;


/**
 *  This is a combined test for {@link InplaceSort} and {@link BinarySearch},
 *  which builds an index over a large number of values. It is meant not only
 *  as a test, but also as an example of how these two classes work together.
 */
public class TestSearchingASortedIndex
extends TestCase
{
    public void testBuildAndSearchAnIndex() throws Exception
    {
        final int size = 10000;
        final Integer[] data = new Integer[size];
        final int[] index = new int[size];

        // note: I saw a test failure in the binary search, but was unable to
        //       reproduce in several dozen runs; if it happens again, the seed
        //       will be logged and a regression written
        long seed = System.currentTimeMillis();
        Random rnd = new Random(seed);
        for (int ii = 0 ; ii < size ; ii++)
        {
            data[ii] = new Integer(rnd.nextInt());
            index[ii] = ii;
        }

        InplaceSort.IntComparator sortComparator = new InplaceSort.IntComparator()
        {
            // note: this is passed values from the index array, which must be used
            //       to index the actual data array
            public int compare(int i1, int i2)
            {
                Integer v1 = data[i1];
                Integer v2 = data[i2];
                return v1.compareTo(v2);
            }
        };
        InplaceSort.sort(index, sortComparator);

        // verify that the index is sorted
        for (int ii = 1 ; ii < size ; ii++)
        {
            Integer v1 = data[index[ii - 1]];
            Integer v2 = data[index[ii]];
            assertTrue("index is not a sorted view: data[index[" + (ii-1) + "]] = " + v1 + ", data[index[" + ii + "] = " + v2,
                       v1.compareTo(v2) <= 0);
        }


        BinarySearch.IndexedComparator<Integer> searchComparator = new BinarySearch.IndexedComparator<Integer>()
        {
            // note: again, idx is an index into the data array
            public int compare(Integer value, int idx)
            {
                return value.compareTo(data[idx]);
            }
        };

        // we'll assert that we can find everything ... note that the returned
        // search value is an index into the *index* array, not the data array
        for (int ii = 0 ; ii < size ; ii++)
        {
            int ret = BinarySearch.search(index, data[index[ii]], searchComparator);
            assertEquals("BinarySearch failed; seed was " + seed, ii, ret);
        }
    }
}
