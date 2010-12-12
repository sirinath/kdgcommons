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
        assertEquals("12", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x12, 2, 0)
                           .toString());
        assertEquals("012", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x12, 3, 0)
                           .toString());
        assertEquals("2", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x12, 1, 0)
                           .toString());

        assertEquals("FF", StringBuilderUtil.appendHex(
                           new StringBuilder(), -1, 2, 0)
                           .toString());
        assertEquals("0FFFFFFFF", StringBuilderUtil.appendHex(
                           new StringBuilder(), -1, 9, 0)
                           .toString());

        assertEquals("12345678", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x12345678, 8, 0)
                           .toString());
        assertEquals("9ABCDEF0", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0x9ABCDEF0, 8, 0)
                           .toString());

        assertEquals("00", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0, 2, 0)
                           .toString());
        assertEquals("00  ", StringBuilderUtil.appendHex(
                           new StringBuilder(), 0, 2, 2)
                           .toString());

        assertEquals("QQ00  ", StringBuilderUtil.appendHex(
                           new StringBuilder("QQ"), 0, 2, 2)
                           .toString());
    }


    public void testLastChar() throws Exception
    {
        assertEquals('c', StringBuilderUtil.lastChar(new StringBuilder("abc")));
        assertEquals('a', StringBuilderUtil.lastChar(new StringBuilder("a")));
        assertEquals('\0', StringBuilderUtil.lastChar(new StringBuilder()));
        assertEquals('\0', StringBuilderUtil.lastChar(null));
    }

}
