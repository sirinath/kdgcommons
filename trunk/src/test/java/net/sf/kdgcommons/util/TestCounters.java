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

package net.sf.kdgcommons.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import net.sf.kdgcommons.collections.MapBuilder;

//
// note: we do not attempt to test concurrent access; doing so would require
//       the ability to suspend one thread in the middle of an operation, and
//       the public API provides no way to do that
//

public class TestCounters
extends TestCase
{
    public void testPutGetAndRemove() throws Exception
    {
        Counters<String> counters = new Counters<String>();

        assertEquals("object get from new instance",        null,               counters.get("foo"));
        assertEquals("primitive get from new instance",     0,                  counters.getLong("foo"));

        assertEquals("return from put, new mapping",        null,               counters.put("foo", Long.valueOf(12)));

        assertEquals("object get, existing mapping",        Long.valueOf(12),   counters.get("foo"));
        assertEquals("primitive get, existing mapping",     12,                 counters.getLong("foo"));

        counters.putLong("foo", 13);
        assertEquals("primitive get after primitive put",   13,                 counters.getLong("foo"));

        assertEquals("return value from remove()",          Long.valueOf(13),   counters.remove("foo"));

        assertEquals("object get after remove()",           null,               counters.get("foo"));
        assertEquals("primitive get after remove()",        0,                  counters.getLong("foo"));
    }


    public void testIncrementAndDecrement() throws Exception
    {
        Counters<String> counters = new Counters<String>();

        assertEquals("increment creates counter",           1,                  counters.increment("foo"));
        assertEquals("post-increment primitive get",        1,                  counters.getLong("foo"));
        assertEquals("post-increment object get",           Long.valueOf(1),    counters.get("foo"));

        assertEquals("increment of existing counte",        2,                  counters.increment("foo"));
        assertEquals("post-increment primitive get",        2,                  counters.getLong("foo"));
        assertEquals("post-increment object get",           Long.valueOf(2),    counters.get("foo"));

        assertEquals("decrement creates counter",           -1,                 counters.decrement("bar"));
        assertEquals("post-decrement primitive get",        -1,                 counters.getLong("bar"));
        assertEquals("post-decrement object get",           Long.valueOf(-1),   counters.get("bar"));

        assertEquals("decrement of existing counter",       -2,                 counters.decrement("bar"));
        assertEquals("post-decrement primitive get",        -2,                 counters.getLong("bar"));
        assertEquals("post-decrement object get",           Long.valueOf(-2),   counters.get("bar"));
    }


    public void testSize() throws Exception
    {
        Counters<String> counters = new Counters<String>();

        assertEquals("size of empty map",                   0,                  counters.size());
        assertEquals("empty map isEmpty()",                 true,               counters.isEmpty());

        counters.put("foo", Long.valueOf(12));

        assertEquals("size after put",                      1,                  counters.size());
        assertEquals("isEmpty() after put",                 false,              counters.isEmpty());

        counters.remove("foo");

        assertEquals("size after remover",                  0,                  counters.size());
        assertEquals("isEmpty() after remove",              true,               counters.isEmpty());
    }


    public void testPutIfAbsent() throws Exception
    {
        Counters<String> counters = new Counters<String>();

        counters.put("foo", Long.valueOf(12));
        counters.putIfAbsent("foo", Long.valueOf(17));
        assertEquals("putIfAbsent() with existing",         Long.valueOf(12),   counters.get("foo"));

        counters.putIfAbsent("bar", Long.valueOf(17));
        assertEquals("putIfAbsent() no existing",           Long.valueOf(17),   counters.get("bar"));
    }


    public void testBulkOperations() throws Exception
    {
        Counters<String> counters = new Counters<String>();

        counters.putAll(new MapBuilder<String,Long>(new HashMap<String,Long>())
                        .put("foo", Long.valueOf(12))
                        .put("bar", Long.valueOf(15))
                        .put("baz", Long.valueOf(17))
                        .toMap());

        assertEquals("size after putAll()",                 3,                  counters.size());
        assertEquals("get() after putAll()",                Long.valueOf(15),   counters.get("bar"));

        counters.clear();

        assertEquals("size after clear()",                  0,                  counters.size());
        assertEquals("get() after clear()",                 null,               counters.get("bar"));
    }


    public void testContains() throws Exception
    {
        Counters<String> counters = new Counters<String>();
        counters.put("foo", Long.valueOf(12));
        counters.put("bar", Long.valueOf(13));

        assertTrue("expected key (foo)",                    counters.containsKey("foo"));
        assertFalse("missing key (baz)",                    counters.containsKey("baz"));

        assertTrue("expected value (12)",                   counters.containsValue(Long.valueOf(12)));
        assertFalse("missing value (15)",                   counters.containsValue(Long.valueOf(15)));

        assertTrue("expected value as Integer",             counters.containsValue(Integer.valueOf(12)));
        assertTrue("expected value as Double",              counters.containsValue(Double.valueOf(12)));
        assertTrue("expected value as truncated Double",    counters.containsValue(Double.valueOf(12.5)));
    }


    public void testSetRetrieval() throws Exception
    {
        Counters<String> counters = new Counters<String>();
        counters.put("foo", Long.valueOf(12));
        counters.put("bar", Long.valueOf(13));

        Set<String> keys = counters.keySet();
        assertEquals("keySet size",                         2,                  keys.size());
        assertTrue("keySet.contains(foo)",                  keys.contains("foo"));
        assertTrue("keySet.contains(bar)",                  keys.contains("bar"));

        Collection<Long> values = counters.values();
        assertEquals("values size",                         2,                  values.size());
        assertTrue("values.contains(foo)",                  values.contains(Long.valueOf(12)));
        assertTrue("values.contains(bar)",                  values.contains(Long.valueOf(13)));

        Set<Map.Entry<String,Long>> entries = counters.entrySet();
        assertEquals("entrySet size",                       2,                  entries.size());

        int itxCount = 0;
        for (Map.Entry<String,Long> entry : entries)
        {
            itxCount++;
            if (entry.getKey().equals("foo"))
            {
                assertEquals("value: foo", Long.valueOf(12), entry.getValue());
            }
            if (entry.getKey().equals("bar"))
            {
                assertEquals("value: bar", Long.valueOf(13), entry.getValue());
            }
        }
        assertEquals("entries iteration count", 2, itxCount);
    }


    public void testIterator() throws Exception
    {
        Counters<String> counters = new Counters<String>();
        counters.put("foo", Long.valueOf(12));
        counters.put("bar", Long.valueOf(13));
        counters.put("baz", Long.valueOf(14));

        Iterator<Map.Entry<String,Long>> itx = counters.iterator();

        int itxCount = 0;
        while (itx.hasNext())
        {
            Map.Entry<String,Long> entry = itx.next();
            itxCount++;
            if (entry.getKey().equals("foo"))
            {
                assertEquals("value: foo", Long.valueOf(12), entry.getValue());
            }
            if (entry.getKey().equals("bar"))
            {
                assertEquals("value: bar", Long.valueOf(13), entry.getValue());
                itx.remove();
            }
            if (entry.getKey().equals("baz"))
            {
                assertEquals("value: baz", Long.valueOf(14), entry.getValue());
                entry.setValue(Long.valueOf(17));
            }
        }

        assertEquals("iteration count",             3,                  itxCount);
        assertEquals("size after remove()",         2,                  counters.size());
        assertEquals("removed key not present",     null,               counters.get("bar"));
        assertEquals("updated value",               Long.valueOf(17),   counters.get("baz"));
    }


}
