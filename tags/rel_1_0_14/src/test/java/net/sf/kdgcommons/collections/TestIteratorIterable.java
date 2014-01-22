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
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;


public class TestIteratorIterable
extends TestCase
{
    public void testBasicOperation() throws Exception
    {
        List<String> src = Arrays.asList("foo", "bar", "baz");
        List<String> dst = new ArrayList<String>();

        Iterator<String> itx = src.iterator();
        IteratorIterable<String> ii = new IteratorIterable<String>(itx);
        for (String s : ii)
        {
            dst.add(s);
        }

        assertEquals(dst, src);
    }


    public void testMultipleIteratorsPointToSameLocation() throws Exception
    {
        List<String> src = Arrays.asList("foo", "bar", "baz");
        Iterator<String> itx = src.iterator();

        IteratorIterable<String> ii1 = new IteratorIterable<String>(itx);
        IteratorIterable<String> ii2 = new IteratorIterable<String>(itx);

        assertEquals("foo", ii1.iterator().next());
        assertEquals("bar", ii2.iterator().next());
        assertEquals("baz", ii1.iterator().next());

        assertFalse(ii1.iterator().hasNext());
        assertFalse(ii2.iterator().hasNext());
    }


}
