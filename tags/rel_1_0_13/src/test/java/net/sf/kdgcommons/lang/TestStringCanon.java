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


public class TestStringCanon extends TestCase
{
    public void testCanonicalization() throws Exception
    {
        StringCanon canon = new StringCanon();

        String s1 = new String("foo");
        String s2 = canon.intern(s1);
        assertNotSame(s1, s2);
        assertEquals(1, canon.size());

        String s3 = canon.intern(new String("foo"));
        assertSame(s2, s3);
    }


    public void testClearedOnGC() throws Exception
    {
        StringCanon canon = new StringCanon();

        String str = canon.intern(new String("foo"));
        int firstHash = System.identityHashCode(str);
        str = null;

        System.gc();
        str = canon.intern(new String("foo"));
        int secondHash = System.identityHashCode(str);

        assertFalse(firstHash == secondHash);
    }
}
