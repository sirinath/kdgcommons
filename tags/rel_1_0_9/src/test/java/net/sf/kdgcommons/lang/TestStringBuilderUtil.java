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


public class TestStringBuilderUtil extends TestCase
{
    public void testAppendRepeatChar() throws Exception
    {
        StringBuilder buf = new StringBuilder();
        assertSame(buf, StringBuilderUtil.appendRepeat(buf, '0', 12));
        assertEquals("000000000000", buf.toString());
    }


    public void testAppendHex() throws Exception
    {
        // test 1: characters fill buffer
        assertEquals("12", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x12, 2)
                           .toString());

        // test 2: too-small width
        assertEquals("ABCDEF0", StringBuilderUtil.appendHex(
                            new StringBuilder(), 0x9ABCDEF0, 7)
                            .toString());

        // test 3: width larger than needed
        assertEquals("012", StringBuilderUtil.appendHex(
                            new StringBuilder(), 0x12, 3)
                            .toString());

        // test 4: append
        assertEquals("QQ12", StringBuilderUtil.appendHex(
                            new StringBuilder("QQ"), 0x12, 2)
                            .toString());
    }


    public void testLastChar() throws Exception
    {
        assertEquals('c', StringBuilderUtil.lastChar(new StringBuilder("abc")));
        assertEquals('a', StringBuilderUtil.lastChar(new StringBuilder("a")));
        assertEquals('\0', StringBuilderUtil.lastChar(new StringBuilder()));
        assertEquals('\0', StringBuilderUtil.lastChar(null));
    }


    public void testAppendUnless() throws Exception
    {
        StringBuilder sb = new StringBuilder("(");

        // test 1: ends with paren, so nothing should be appended
        StringBuilderUtil.appendUnless(sb, "(", ",");
        assertEquals("(", sb.toString());

        // test 2: once the paren is no longer the end, then the comma is appended
        sb.append("foo");
        StringBuilderUtil.appendUnless(sb, "(", ",");
        assertEquals("(foo,", sb.toString());

        // test 3: a test string > 1 char that won't apply
        StringBuilderUtil.appendUnless(sb, "foo,", "bar,");
        assertEquals("(foo,", sb.toString());

        // test 4: and one that will
        StringBuilderUtil.appendUnless(sb, "baz,", "bar,");
        assertEquals("(foo,bar,", sb.toString());

        // test 5: a test string that's longer than the builder
        StringBuilderUtil.appendUnless(sb, "(foo,bar,baz", "biff");
        assertEquals("(foo,bar,biff", sb.toString());
    }
}
