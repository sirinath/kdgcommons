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

public class TestStringUtil extends TestCase
{
    public void testLastChar() throws Exception
    {
        assertEquals('c', StringUtil.lastChar("abc"));
        assertEquals('a', StringUtil.lastChar("a"));
        assertEquals('\0', StringUtil.lastChar(""));
        assertEquals('\0', StringUtil.lastChar(null));
    }


    public void testIsEmpty() throws Exception
    {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("foo"));
    }


    public void testIsBlank() throws Exception
    {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("  "));
        assertTrue(StringUtil.isBlank(" \t "));

        assertFalse(StringUtil.isBlank(" x "));
        assertFalse(StringUtil.isBlank(" \u00A0 "));
    }



    public void testTrim() throws Exception
    {
        assertEquals("foo", StringUtil.trim("  foo \t\n\r "));
    }


    public void testTrimNull() throws Exception
    {
        assertEquals("", StringUtil.trim(null));
    }


    public void testTrimEmpty() throws Exception
    {
        assertEquals("", StringUtil.trim(""));
    }


    public void testTrimToEmpty() throws Exception
    {
        assertEquals("", StringUtil.trim("  \t\r\n  "));
    }


    public void testTrimUntrimmable() throws Exception
    {
        assertSame("foo", StringUtil.trim("foo"));
    }


    public void testContains() throws Exception
    {
        assertTrue(StringUtil.contains("foo", "foo"));
        assertTrue(StringUtil.contains("foo", "oo"));
        assertTrue(StringUtil.contains("foo", "f"));
        assertTrue(StringUtil.contains("foo", "o"));

        assertFalse(StringUtil.contains("foo", "bar"));
        assertFalse(StringUtil.contains("foo", "b"));

        assertTrue(StringUtil.contains("foo", ""));
        assertFalse(StringUtil.contains("", "bar"));

        assertFalse(StringUtil.contains(null, "bar"));
        assertFalse(StringUtil.contains("foo", null));
        assertFalse(StringUtil.contains(null, null));
    }


    public void testContainsIgnoreCase() throws Exception
    {
        assertTrue(StringUtil.containsIgnoreCase("foo", "foo"));
        assertTrue(StringUtil.containsIgnoreCase("FOO", "foo"));
        assertTrue(StringUtil.containsIgnoreCase("foo", "FOO"));

        assertTrue(StringUtil.containsIgnoreCase("foo", "oo"));
        assertTrue(StringUtil.containsIgnoreCase("FOO", "oo"));
        assertTrue(StringUtil.containsIgnoreCase("foo", "OO"));
        assertTrue(StringUtil.containsIgnoreCase("fOo", "OO"));
        assertTrue(StringUtil.containsIgnoreCase("fOo", "oo"));

        assertTrue(StringUtil.containsIgnoreCase("foo", "f"));
        assertTrue(StringUtil.containsIgnoreCase("fOo", "o"));

        assertFalse(StringUtil.containsIgnoreCase("foo", "bar"));
        assertFalse(StringUtil.containsIgnoreCase("foo", "b"));

        assertTrue(StringUtil.containsIgnoreCase("foo", ""));
        assertFalse(StringUtil.containsIgnoreCase("", "bar"));

        assertFalse(StringUtil.containsIgnoreCase(null, "bar"));
        assertFalse(StringUtil.containsIgnoreCase("foo", null));
        assertFalse(StringUtil.containsIgnoreCase(null, null));
    }


    public void testRepeat() throws Exception
    {
        assertEquals("", StringUtil.repeat(' ', 0));
        assertEquals("A", StringUtil.repeat('A', 1));
        assertEquals("BBB", StringUtil.repeat('B', 3));
    }


    public void testToUTF8() throws Exception
    {
        byte[] data = StringUtil.toUTF8("ab\u00e7\u2747");
        assertEquals(7, data.length);
        assertEquals('a',  data[0]);
        assertEquals('b',  data[1]);
        assertEquals(0xC3, data[2] & 0xFF);
        assertEquals(0xA7, data[3] & 0xFF);
        assertEquals(0xE2, data[4] & 0xFF);
        assertEquals(0x9D, data[5] & 0xFF);
        assertEquals(0x87, data[6] & 0xFF);
    }


    public void testLatinString() throws Exception
    {
        byte[] src = new byte[] {(byte)0x00, (byte)0x40, (byte)0x7F,
                                 (byte)0x80, (byte)0xC9, (byte)0xFF};

        String dst1 = StringUtil.toLatinString(src);
        assertEquals(6, dst1.length());
        assertEquals('\u0000', dst1.charAt(0));
        assertEquals('\u0040', dst1.charAt(1));
        assertEquals('\u007F', dst1.charAt(2));
        assertEquals('\u0080', dst1.charAt(3));
        assertEquals('\u00C9', dst1.charAt(4));
        assertEquals('\u00FF', dst1.charAt(5));

        byte[] dst2 = StringUtil.toLatinBytes(dst1);
        assertTrue(ObjectUtil.equals(src, dst2));
    }


    public void testUnicodeEscape() throws Exception
    {
        assertNull(StringUtil.unicodeEscape(null));
        assertEquals("", StringUtil.unicodeEscape(""));

        assertEquals("A\\u0007B", StringUtil.unicodeEscape("A\u0007B"));
        assertEquals("A\\u0007", StringUtil.unicodeEscape("A\u0007"));
        assertEquals("\\u0007B", StringUtil.unicodeEscape("\u0007B"));
        assertEquals("\\u0007", StringUtil.unicodeEscape("\u0007"));

        assertEquals("\\u0022\\u0027\\u005C", StringUtil.unicodeEscape("\"\'\\"));
    }


    public void testUnescape() throws Exception
    {
        assertNull(StringUtil.unescape(null));
        assertEquals("", StringUtil.unescape(""));
        assertEquals("ABC", StringUtil.unescape("ABC"));

        assertEquals("\"", StringUtil.unescape("\\\""));
        assertEquals("\'", StringUtil.unescape("\\\'"));
        assertEquals("\\", StringUtil.unescape("\\\\"));

        assertEquals("\b", StringUtil.unescape("\\b"));
        assertEquals("\t", StringUtil.unescape("\\t"));
        assertEquals("\n", StringUtil.unescape("\\n"));
        assertEquals("\f", StringUtil.unescape("\\f"));
        assertEquals("\r", StringUtil.unescape("\\r"));

        assertEquals("\u1234\u5678\u90AB", StringUtil.unescape("\\u1234\\U5678\\u90AB"));
    }


    public void testParseDigit() throws Exception
    {
        assertEquals(0, StringUtil.parseDigit('0', 10));
        assertEquals(9, StringUtil.parseDigit('9', 10));
        assertEquals(-1, StringUtil.parseDigit('A', 10));

        assertEquals(0, StringUtil.parseDigit('0', 16));
        assertEquals(9, StringUtil.parseDigit('9', 16));
        assertEquals(10, StringUtil.parseDigit('A', 16));
        assertEquals(15, StringUtil.parseDigit('F', 16));
        assertEquals(-1, StringUtil.parseDigit('G', 16));
        assertEquals(10, StringUtil.parseDigit('a', 16));
        assertEquals(15, StringUtil.parseDigit('f', 16));
        assertEquals(-1, StringUtil.parseDigit('g', 16));

        assertEquals(35, StringUtil.parseDigit('Z', 36));
        assertEquals(35, StringUtil.parseDigit('z', 36));

        assertEquals(-1, StringUtil.parseDigit('!', 100));
    }


    public void testIntern() throws Exception
    {
        String s1 = new String("foo");
        String s2 = new String("foo");
        assertNotSame(s1, s2);

        String s3 = StringUtil.intern(s1);
        String s4 = StringUtil.intern(s2);
        assertSame(s3, s4);
    }
}
