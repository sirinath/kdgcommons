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
        StringBuilder sb = new StringBuilder("foobarbaz");

        assertTrue("search at start",  CharSequenceUtil.containsAt(sb, "foo", 0));
        assertTrue("search in middle", CharSequenceUtil.containsAt(sb, "bar", 3));
        assertTrue("search at end",    CharSequenceUtil.containsAt(sb, "baz", 6));

        assertFalse("failed search",   CharSequenceUtil.containsAt(sb, "foo", 1));

        assertFalse("bad location doesn't throw",       CharSequenceUtil.containsAt(sb, "foo", 1000));
        assertFalse("overrunning search doesn't throw", CharSequenceUtil.containsAt(sb, "bazzle", 6));

        assertFalse("null stringbuilder doesn't throw", CharSequenceUtil.containsAt(null, "foo", 0));
        assertFalse("null search string doesn't throw", CharSequenceUtil.containsAt(sb, null, 0));
    }
}
