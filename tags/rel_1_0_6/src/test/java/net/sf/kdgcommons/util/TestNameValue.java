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

import junit.framework.TestCase;


public class TestNameValue extends TestCase
{
    public void testConstructionAndGet() throws Exception
    {
        String name = "foo";
        Object val1 = "bar";
        Object val2 = new Integer(123);

        NameValue<Object> nv1 = new NameValue<Object>(name, val1);
        assertSame(name, nv1.getName());
        assertSame(val1, nv1.getValue());

        NameValue<Object> nv2 = new NameValue<Object>(name, val2);
        assertSame(name, nv2.getName());
        assertSame(val2, nv2.getValue());
    }


    public void testEqualsAndHashCode() throws Exception
    {
        NameValue<Integer> nv1  = new NameValue<Integer>("foo", new Integer(1));
        NameValue<Integer> nv1b = new NameValue<Integer>("foo", new Integer(1));
        NameValue<Integer> nv2  = new NameValue<Integer>("foo", new Integer(2));
        NameValue<Integer> nv3  = new NameValue<Integer>("bar", new Integer(2));

        assertTrue("same name, same value",       nv1.equals(nv1b));
        assertFalse("same name, different value", nv1.equals(nv2));
        assertFalse("different name, same value", nv2.equals(nv3));

        assertFalse("null",  nv1.equals(null));
        assertFalse("bogus", nv1.equals(new Object()));

        // note: second/third assertion depends on values chosen
        assertTrue(nv1.hashCode() == nv1b.hashCode());
        assertFalse(nv1.hashCode() == nv2.hashCode());
        assertFalse(nv1.hashCode() == nv3.hashCode());
    }


    public void testToString() throws Exception
    {
        // this test exists primarily to ensure that toString() doesn't blow up
        String value = new NameValue<String>("foo", "bar").toString();

        // but we might as well make some assertions
        assertTrue(value.contains("foo"));
        assertTrue(value.contains("bar"));

        // and if we're going to blow up, null values are the place to do it
        // ... so just try to create, don't assert content
        new NameValue<String>(null, null).toString();
    }


    public void testComparison() throws Exception
    {
        NameValue<Object> n1 = new NameValue<Object>("foo", null);
        NameValue<Object> n2 = new NameValue<Object>("bar", null);

        assertTrue(n1.compareTo(n2) > 0);
        assertTrue(n2.compareTo(n1) < 0);
        assertTrue(n1.compareTo(n1) == 0);

        NameValue<Object> s1 = new NameValue<Object>("x", "foo");
        NameValue<Object> s2 = new NameValue<Object>("x", "bar");

        assertTrue(s1.compareTo(s2) > 0);
        assertTrue(s2.compareTo(s1) < 0);
        assertTrue(s1.compareTo(s1) == 0);

        NameValue<Object> i1 = new NameValue<Object>("x", new Integer(123));
        NameValue<Object> i2 = new NameValue<Object>("x", new Integer(13));

        assertTrue(i1.compareTo(i2) > 0);
        assertTrue(i2.compareTo(i1) < 0);
        assertTrue(i1.compareTo(i1) == 0);
    }
}
