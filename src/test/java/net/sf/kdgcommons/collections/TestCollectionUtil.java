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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;


public class TestCollectionUtil extends TestCase
{
    public void testAsSet() throws Exception
    {
        Set<String> values = CollectionUtil.asSet("foo", "bar", "baz", "foo");
        assertEquals(3, values.size());
        assertTrue(values.contains("foo"));
        assertTrue(values.contains("bar"));
        assertTrue(values.contains("baz"));
    }


    public void testAddAllFromVarargs() throws Exception
    {
        List<String> list = new ArrayList<String>();
        CollectionUtil.addAll(list, "foo", "bar", "baz");
        assertEquals(3, list.size());


        Set<String> set = new HashSet<String>();
        CollectionUtil.addAll(set, "argle", "bargle");
        assertEquals(2, set.size());
    }


    public void testAddAllFromIterable() throws Exception
    {
        // whitebox test: this also tests adding all from an iterator

        List<String> list = new ArrayList<String>();
        CollectionUtil.addAll(list, Arrays.asList("foo", "bar", "baz"));
        assertEquals(3, list.size());


        Set<String> set = new HashSet<String>();
        CollectionUtil.addAll(set, Arrays.asList("foo", "bar", "baz", "bar"));
        assertEquals(3, set.size());
    }


    public void testCastList() throws Exception
    {
        ArrayList<Object> x = new ArrayList<Object>();
        x.add("foo");
        x.add("bar");
        x.add("baz");

        List<String> y = CollectionUtil.cast(x, String.class);
        assertSame(x, y);
    }


    public void testCastListFailure() throws Exception
    {
        ArrayList<Object> x = new ArrayList<Object>();
        x.add("foo");
        x.add(Integer.valueOf(1));
        x.add("baz");

        try
        {
            CollectionUtil.cast(x, String.class);
            fail("should have thrown");
        }
        catch (ClassCastException ignored)
        {
            // success
        }
    }


    public void testCastListWithNull() throws Exception
    {
        ArrayList<Object> x = new ArrayList<Object>();
        x.add("foo");
        x.add(null);
        x.add("baz");

        List<String> y = CollectionUtil.cast(x, String.class);
        assertSame(x, y);
    }


    public void testCastSet() throws Exception
    {
        HashSet<Object> x = new HashSet<Object>();
        x.add("foo");
        x.add("bar");
        x.add("baz");

        Set<String> y = CollectionUtil.cast(x, String.class);
        assertSame(x, y);
    }


    public void testCastSetFailure() throws Exception
    {
        HashSet<Object> x = new HashSet<Object>();
        x.add("foo");
        x.add(Integer.valueOf(1));
        x.add("baz");

        try
        {
            CollectionUtil.cast(x, String.class);
            fail("should have thrown");
        }
        catch (ClassCastException ignored)
        {
            // success
        }
    }


    public void testCastSetWithNull() throws Exception
    {
        HashSet<Object> x = new HashSet<Object>();
        x.add("foo");
        x.add(null);
        x.add("baz");

        Set<String> y = CollectionUtil.cast(x, String.class);
        assertSame(x, y);
    }


    public void testResize() throws Exception
    {
        ArrayList<String> list1 = new ArrayList<String>();

        assertSame(list1, CollectionUtil.resize(list1, 2));
        assertEquals(2, list1.size());
        assertNull(list1.get(0));
        assertNull(list1.get(1));

        // verify list unchanged if passed same size
        list1.set(0, "foo");
        list1.set(1, "bar");
        assertSame(list1, CollectionUtil.resize(list1, 2));
        assertEquals(2, list1.size());
        assertEquals("foo", list1.get(0));
        assertEquals("bar", list1.get(1));

        // add another element (this test is fluff)
        assertSame(list1, CollectionUtil.resize(list1, 3));
        assertEquals(3, list1.size());
        assertEquals("foo", list1.get(0));
        assertEquals("bar", list1.get(1));
        assertNull(list1.get(2));

        // reduce size
        assertSame(list1, CollectionUtil.resize(list1, 2));
        assertEquals(2, list1.size());
        assertEquals("foo", list1.get(0));
        assertEquals("bar", list1.get(1));

        // verify alternate paths for non-RandomAccess

        LinkedList<String> list2 = new LinkedList<String>();
        list2.add("foo");
        list2.add("bar");

        assertSame(list2, CollectionUtil.resize(list2, 3));
        assertEquals(3, list2.size());
        assertEquals("foo", list2.get(0));
        assertEquals("bar", list2.get(1));
        assertNull(list2.get(2));

        assertSame(list2, CollectionUtil.resize(list2, 2));
        assertEquals(2, list2.size());
        assertEquals("foo", list2.get(0));
        assertEquals("bar", list2.get(1));
    }


    public void testResizeWithValue() throws Exception
    {
        List<String> list = new ArrayList<String>();
        assertSame(list, CollectionUtil.resize(list, 2, "foo"));
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("foo", list.get(1));
    }


    public void testJoin() throws Exception
    {
        // part 1: join various numbers of elements
        assertEquals("",            CollectionUtil.join(Arrays.asList(), ","));
        assertEquals("foo",         CollectionUtil.join(Arrays.asList("foo"), ","));
        assertEquals("foo,bar",     CollectionUtil.join(Arrays.asList("foo", "bar"), ","));
        assertEquals("foo,bar,baz", CollectionUtil.join(Arrays.asList("foo", "bar", "baz"), ","));

        // part 2: arbitrary objects
        assertEquals("1,foo,2",     CollectionUtil.join(Arrays.asList(new Integer(1), "foo", new Long(2)), ","));

        // part 3: nulls in various places
        assertEquals("",            CollectionUtil.join(null, ","));
        assertEquals("foo,,baz",    CollectionUtil.join(Arrays.asList("foo", null, "baz"), ","));
        assertEquals(",,baz",       CollectionUtil.join(Arrays.asList(null, null, "baz"), ","));
        assertEquals("foo,,",       CollectionUtil.join(Arrays.asList("foo", null, null), ","));
        assertEquals(",bar,",       CollectionUtil.join(Arrays.asList(null, "bar", null), ","));
    }


    public void testFilterStringList() throws Exception
    {
        List<String> src = Arrays.asList("foo", "bar", "baz", "");

        assertEquals(Arrays.asList("foo", "bar", "baz"),
                     CollectionUtil.filter(src, ".+", true));
        assertEquals(Arrays.asList(""),
                     CollectionUtil.filter(src, ".+", false));

        assertEquals(Arrays.asList("bar", "baz"),
                     CollectionUtil.filter(src, ".a.", true));
        assertEquals(Arrays.asList("foo", ""),
                     CollectionUtil.filter(src, ".a.", false));
    }


    public void testFilterNonStringList() throws Exception
    {
        Integer i1 = Integer.valueOf(1);
        Integer i2 = Integer.valueOf(2);
        Integer i3 = Integer.valueOf(12);

        List<Integer> src = Arrays.asList(i1, i2, i3);

        assertEquals(Arrays.asList(i1, i2, i3),
                     CollectionUtil.filter(src, ".+", true));
        assertEquals(Collections.emptyList(),
                     CollectionUtil.filter(src, ".+", false));

        assertEquals(Arrays.asList(i2, i3),
                     CollectionUtil.filter(src, ".*2.*", true));
        assertEquals(Arrays.asList(i1),
                     CollectionUtil.filter(src, ".*2.*", false));
    }


    public void testFilterListWithNull() throws Exception
    {
        List<String> src = Arrays.asList("foo", null, "bar");

        assertEquals(Arrays.asList("foo", "bar"),
                     CollectionUtil.filter(src, ".+", true));
        assertEquals(Arrays.asList((String)null),
                     CollectionUtil.filter(src, ".+", false));
    }


    public void testIsEmpty() throws Exception
    {
        assertTrue(CollectionUtil.isEmpty(null));
        assertTrue(CollectionUtil.isEmpty(Arrays.asList()));
        assertFalse(CollectionUtil.isEmpty(Arrays.asList("foo")));

        assertFalse(CollectionUtil.isNotEmpty(null));
        assertFalse(CollectionUtil.isNotEmpty(Arrays.asList()));
        assertTrue(CollectionUtil.isNotEmpty(Arrays.asList("foo")));
    }


    public void testDefaultIfNull() throws Exception
    {
        Iterable<String> it1 = CollectionUtil.defaultIfNull(Arrays.asList("foo"), Arrays.asList("bar"));
        assertEquals("foo", it1.iterator().next());

        Iterable<String> it2 = CollectionUtil.defaultIfNull(null, Arrays.asList("bar"));
        assertEquals("bar", it2.iterator().next());
    }


    public void testDefaultIfEmpty() throws Exception
    {
        Collection<String> c1 = CollectionUtil.defaultIfEmpty(Arrays.asList("foo"), Arrays.asList("bar"));
        assertEquals("foo", c1.iterator().next());

        Collection<String> c2 = CollectionUtil.defaultIfEmpty(Arrays.<String>asList(), Arrays.asList("bar"));
        assertEquals("bar", c2.iterator().next());

        Collection<String> c3 = CollectionUtil.defaultIfEmpty(null, Arrays.asList("bar"));
        assertEquals("bar", c3.iterator().next());
    }
}
