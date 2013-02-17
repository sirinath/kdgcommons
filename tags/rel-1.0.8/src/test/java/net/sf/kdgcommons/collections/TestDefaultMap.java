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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import net.sf.kdgcommons.collections.DefaultMap.ValueFactory;

public class TestDefaultMap
extends TestCase
{
//----------------------------------------------------------------------------
//  Support code
//----------------------------------------------------------------------------

    // a factory that returns new String instances (ctor is required here)
    ValueFactory<String> stringFactory = new ValueFactory<String>()
    {
        public String newInstance()
        {
            return new String("baz");
        }
    };


//----------------------------------------------------------------------------
//  Test cases
//----------------------------------------------------------------------------

    public void testStaticValue() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");

        String g1 = map.get("foo");
        String g2 = map.get("bar");

        assertSame(g1, g2);
        assertEquals("baz", g1);

        assertFalse(delegate.containsKey("foo"));
        assertFalse(delegate.containsKey("bar"));
    }


    public void testDynamicValue() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, stringFactory);

        String g1 = map.get("foo");
        String g2 = map.get("bar");

        assertNotSame(g1, g2);
        assertEquals("baz", g1);
        assertEquals("baz", g2);

        assertEquals("baz", delegate.get("foo"));
        assertEquals("baz", delegate.get("bar"));
    }


    public void testDynamicValueReadOnly() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, stringFactory, false);

        String g1 = map.get("foo");
        String g2 = map.get("foo");

        assertNotSame(g1, g2);
        assertEquals("baz", g1);
        assertEquals("baz", g2);

        assertNull(delegate.get("foo"));
    }


    public void testDefaultValueDoesNotOverrideActual() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        delegate.put("foo", "bar");

        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");
        assertEquals("bar", map.get("foo"));
        assertEquals("baz", map.get("blah"));
    }



    public void testPutAndRemove() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertEquals("baz", map.get("foo"));

        assertNull(map.put("foo", "bar"));      // note: not default value
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertEquals("bar", map.get("foo"));
        assertEquals("bar", delegate.get("foo"));

        assertEquals("baz", map.get("blah"));

        assertEquals("bar", map.put("foo", "zzz"));
        assertEquals("zzz", map.get("foo"));
        assertEquals(1, map.size());

        assertEquals("zzz", map.remove("foo"));
        assertEquals(0, map.size());
        assertEquals("baz", map.get("foo"));

        // and just for giggles, we'll remove an entry that was never there
        assertNull(map.remove("argle"));
    }


    public void testContainsIgnoresDefault() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");
        map.put("foo", "bar");

        assertTrue(map.containsKey("foo"));
        assertFalse(map.containsKey("bar"));

        assertTrue(map.containsValue("bar"));
        assertFalse(map.containsValue("baz"));
    }


    public void testSubcollectionsIgnoreDefault() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");
        map.put("foo", "bar");

        Set<String> keySet = map.keySet();
        assertEquals(1, keySet.size());
        assertTrue(keySet.contains("foo"));

        Collection<String> values = map.values();
        assertEquals(1, values.size());
        assertEquals("bar", values.iterator().next());

        Set<Map.Entry<String,String>> entries = map.entrySet();
        assertEquals(1, entries.size());
        Map.Entry<String,String> entry = entries.iterator().next();
        assertEquals("foo", entry.getKey());
        assertEquals("bar", entry.getValue());
    }


    // this test is just for coverage
    public void testPutAllAndClear() throws Exception
    {
        Map<String,String> delegate = new HashMap<String,String>();
        Map<String,String> map = new DefaultMap<String,String>(delegate, "baz");

        Map<String,String> add = new HashMap<String,String>();
        add.put("argle", "bargle");
        add.put("wargle", "zargle");

        map.putAll(add);
        assertEquals(2, map.size());
        assertEquals(2, delegate.size());
        assertEquals("bargle", map.get("argle"));
        assertEquals("zargle", map.get("wargle"));

        map.clear();
        assertEquals(0, map.size());
        assertEquals(0, delegate.size());
    }
}
