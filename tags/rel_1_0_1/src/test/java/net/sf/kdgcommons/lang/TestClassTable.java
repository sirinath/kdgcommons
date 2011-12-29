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

package net.sf.kdgcommons.lang;

import junit.framework.TestCase;


public class TestClassTable
extends TestCase
{
    public void testPutAndGetExactClass() throws Exception
    {
        ClassTable<String> table = new ClassTable<String>();
        assertEquals(0, table.size());

        table.put(Integer.class, "foo");
        table.put(Double.class, "bar");
        assertEquals(2, table.size());

        assertEquals("foo", table.get(Integer.class));
        assertEquals("bar", table.get(Double.class));
        assertNull(table.get(String.class));

        assertEquals("foo", table.getByObject(new Integer(1)));
        assertEquals("foo", table.getByObject(new Integer(2)));
        assertEquals("bar", table.getByObject(new Double(1)));      // same key per equals()
        assertEquals("bar", table.getByObject(new Double(123.45)));
        assertNull(table.getByObject("baz"));
    }


    public void testGetBySuperclass() throws Exception
    {
        ClassTable<String> table = new ClassTable<String>();
        table.put(Integer.class, "foo");
        table.put(Number.class, "bar");
        assertEquals(2, table.size());

        assertEquals("foo", table.get(Integer.class));
        assertEquals("bar", table.get(Double.class));
        assertNull(table.get(String.class));

        assertEquals("table caches discovered classes", 3, table.size());

        assertEquals("bar", table.getByObject(new Long(1)));
        assertEquals("table caches discovered classes in getByObject", 4, table.size());
    }


    // note: this does not test concurrent operation
    public void testReplaceAll() throws Exception
    {
        ClassTable<String> table = new ClassTable<String>();
        table.put(String.class, "foo");
        table.put(Number.class, "bar");
        assertEquals(2, table.size());

        // add a few subclass mappings
        table.getByObject(new Integer(1));
        table.getByObject(new Long(1));
        assertEquals(4, table.size());

        table.replace(Number.class, "baz");
        assertEquals(4, table.size());
        assertEquals("baz", table.get(Number.class));
        assertEquals("baz", table.get(Integer.class));
        assertEquals("baz", table.get(Long.class));
        assertEquals("baz", table.get(Double.class));

        // and verify that we didn't lose anything in translation
        assertEquals("foo", table.get(String.class));
    }
}
