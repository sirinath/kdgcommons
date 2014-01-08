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

package net.sf.kdgcommons.tuple;

import junit.framework.TestCase;


public class TestTuple2
extends TestCase
{
    public void testCreateAndGet() throws Exception
    {
        Tuple2<String,String> x = new Tuple2<String,String>("foo", "bar");
        assertEquals("val0", "foo", x.get0());
        assertEquals("val1", "bar", x.get1());
    }


    public void testEqualsAndHashCode() throws Exception
    {
        Tuple2<String,String> x = new Tuple2<String,String>("foo", "bar");
        Tuple2<String,String> y = new Tuple2<String,String>("foo", "bar");
        Tuple2<String,String> z = new Tuple2<String,String>("argle", "bargle");

        assertTrue("identity", x.equals(x));
        assertTrue("same value", x.equals(y));
        assertFalse("different values", x.equals(z));
        
        // this one's for coverage
        assertFalse("not-a-tuple", x.equals("I'm not a tuple"));

        // following tests are known values
        assertTrue("hashcode nonzero", x.hashCode() != 0);
        assertTrue("hashcode equals",  x.hashCode() == y.hashCode());
        assertTrue("hashcode differs", x.hashCode() != z.hashCode());
    }


    public void testEqualsAndHashcodeWithNullComponents() throws Exception
    {
        Tuple2<String,String> x1 = new Tuple2<String,String>(null, "bar");
        Tuple2<String,String> x2 = new Tuple2<String,String>(null, "bar");
        Tuple2<String,String> y1 = new Tuple2<String,String>("foo", null);
        Tuple2<String,String> y2 = new Tuple2<String,String>("foo", null);
        Tuple2<String,String> z1 = new Tuple2<String,String>(null, null);
        Tuple2<String,String> z2 = new Tuple2<String,String>(null, null);

        assertTrue("equality, first component null", x1.equals(x2));
        assertTrue("equality, second component null", y1.equals(y2));
        assertTrue("equality, both components null", z1.equals(z2));
    }


    public void testComparableTuple2() throws Exception
    {
        ComparableTuple2<String,String> x = new ComparableTuple2<String,String>("foo", "bar");
        ComparableTuple2<String,String> y = new ComparableTuple2<String,String>("foo", "bar");
        ComparableTuple2<String,String> z = new ComparableTuple2<String,String>("bar", "foo");
        ComparableTuple2<String,String> n = new ComparableTuple2<String,String>(null, "bar");
        ComparableTuple2<String,String> q = new ComparableTuple2<String,String>("foo", null);
        ComparableTuple2<String,String> r1 = new ComparableTuple2<String,String>(null, null);
        ComparableTuple2<String,String> r2 = new ComparableTuple2<String,String>(null, null);

        assertTrue("equal tuples", x.compareTo(y) == 0);
        assertTrue("greater than", y.compareTo(z) > 0);
        assertTrue("less than",    z.compareTo(y) < 0);

        assertTrue("null first component", n.compareTo(x) < 0);
        assertTrue("null second component", q.compareTo(x) < 0);
        assertTrue("both components null ", r1.compareTo(x) < 0);
        assertTrue("null == null ",         r1.compareTo(r2) == 0);
    }

}
