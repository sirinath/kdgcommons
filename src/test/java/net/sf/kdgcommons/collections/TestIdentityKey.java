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

import java.util.HashSet;

import junit.framework.TestCase;


public class TestIdentityKey extends TestCase
{
    public void testEquals() throws Exception
    {
        // these two values will be equal but not have the same identity
        Integer i1 = new Integer(123);
        Integer i2 = new Integer(123);

        assertTrue(new IdentityKey(i1).equals(new IdentityKey(i1)));
        assertFalse(new IdentityKey(i1).equals(new IdentityKey(i2)));
        
        // test null and a bogus value to make sure nothing explodes        
        assertFalse(new IdentityKey(null).equals(null)); 
        assertFalse(new IdentityKey(i1).equals(null));
        assertFalse(new IdentityKey(new Object()).equals(new IdentityKey(new Object())));
    }


    public void testHashCode() throws Exception
    {
        Integer i1 = new Integer(123);

        assertEquals(new IdentityKey(i1).hashCode(),
                     new IdentityKey(i1).hashCode());

        // this test isn't guaranteed to work -- the JVM could return
        // the same identity hashcode for each object
        for (int ii = 0 ; ii < 10 ; ii++)
        {
            Integer i2 = new Integer(123);
            if (new IdentityKey(i1).hashCode() != new IdentityKey(i2).hashCode())
                return;
        }
        fail("too many objects have same identity hashcode");
    }


    public void testInSitu() throws Exception
    {
        // note: not valueOf(), we want distinct objects
        Integer i1 = new Integer(123);
        Integer i2 = new Integer(123);

        HashSet<IdentityKey> set = new HashSet<IdentityKey>();

        set.add(new IdentityKey(i1));
        assertTrue(set.contains(new IdentityKey(i1)));
        assertFalse(set.contains(new IdentityKey(i2)));
    }
}
