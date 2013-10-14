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


public class TestCharSequenceUtil
extends TestCase
{
    public void testContainsAt() throws Exception
    {
        String src = "foobarbaz";

        assertTrue("search at start",  CharSequenceUtil.containsAt(src, "foo", 0));
        assertTrue("search in middle", CharSequenceUtil.containsAt(src, "bar", 3));
        assertTrue("search at end",    CharSequenceUtil.containsAt(src, "baz", 6));

        assertFalse("failed search",   CharSequenceUtil.containsAt(src, "foo", 1));

        assertFalse("overrunning search doesn't throw", CharSequenceUtil.containsAt(src, "bazzle", 6));
        assertFalse("below-bounds loc doesn't throw",   CharSequenceUtil.containsAt(src, "foo", -5));
        assertFalse("above-bounds loc doesn't throw",   CharSequenceUtil.containsAt(src, "foo", 1000));

        assertFalse("null source string doesn't throw", CharSequenceUtil.containsAt(null, "foo", 0));
        assertFalse("null search string doesn't throw", CharSequenceUtil.containsAt(src, null, 0));
    }


    public void testStartsWith() throws Exception
    {
        String src = "foobarbaz";

        assertTrue("happy path", CharSequenceUtil.startsWith(src, "foo"));
        assertFalse("sad path",  CharSequenceUtil.startsWith(src, "bar"));

        assertFalse("null source string doesn't throw", CharSequenceUtil.startsWith(null, "foo"));
        assertFalse("null search string doesn't throw", CharSequenceUtil.startsWith(src, null));
   }


    public void testEndsWith() throws Exception
    {
        String src = "foobarbaz";

        assertTrue("happy path", CharSequenceUtil.endsWith(src, "baz"));
        assertFalse("sad path",  CharSequenceUtil.endsWith(src, "bar"));

        assertFalse("null source string doesn't throw", CharSequenceUtil.endsWith(null, "foo"));
        assertFalse("null search string doesn't throw", CharSequenceUtil.endsWith(src, null));

        assertFalse("too-large search string doesn't throw", CharSequenceUtil.endsWith(src, "foofibblebaz"));
   }
}
