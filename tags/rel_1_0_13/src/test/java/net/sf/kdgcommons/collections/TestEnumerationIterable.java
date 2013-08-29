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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;


public class TestEnumerationIterable extends TestCase
{
    public void testBasicOperation() throws Exception
    {
        Vector<String> src = new Vector<String>(Arrays.asList("foo", "bar", "baz"));
        Vector<String> dst = new Vector<String>();

        Enumeration<String> itx = src.elements();
        EnumerationIterable<String> ii = new EnumerationIterable<String>(itx);
        for (String s : ii)
        {
            dst.add(s);
        }

        assertEquals(dst, src);
    }


    public void testMultipleIteratorsPointToSameLocation() throws Exception
    {
        Vector<String> src = new Vector<String>(Arrays.asList("foo", "bar", "baz"));
        Enumeration<String> itx = src.elements();

        EnumerationIterable<String> ei1 = new EnumerationIterable<String>(itx);
        EnumerationIterable<String> ei2 = new EnumerationIterable<String>(itx);

        assertEquals("foo", ei1.iterator().next());
        assertEquals("bar", ei2.iterator().next());
        assertEquals("baz", ei1.iterator().next());

        assertFalse(ei1.iterator().hasNext());
        assertFalse(ei2.iterator().hasNext());
    }
}
