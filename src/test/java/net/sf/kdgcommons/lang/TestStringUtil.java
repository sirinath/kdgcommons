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

import net.sf.kdgcommons.test.NumericAsserts;


public class TestStringUtil extends TestCase
{
    public void testLength() throws Exception
    {
        assertEquals("non-empty", 3, StringUtil.length("foo"));
        assertEquals("empty",     0, StringUtil.length(""));
        assertEquals("null",      0, StringUtil.length(null));
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
        assertEquals("no trimming",         "foo", StringUtil.trim("foo"));
        assertEquals("trimmed non-empty",   "foo", StringUtil.trim("  foo\t  "));
        assertEquals("trimmed empty",       "",    StringUtil.trim("  \r\t  "));
        assertEquals("initially empty",     "",    StringUtil.trim(""));
        assertEquals("initially null",      "",    StringUtil.trim(null));
    }


    public void testTrimToNull() throws Exception
    {
        assertEquals("no trimming",         "foo", StringUtil.trimToNull("foo"));
        assertEquals("trimmed non-empty",   "foo", StringUtil.trimToNull("  foo  "));
        assertEquals("trimmed empty",       null,  StringUtil.trimToNull("    "));
        assertEquals("initially empty",     null,  StringUtil.trimToNull(""));
        assertEquals("initially null",      null,  StringUtil.trimToNull(null));
    }


    public void testPadding() throws Exception
    {
        assertEquals("qqqqq", StringUtil.padLeft("", 5, 'q'));
        assertEquals("qqqqq", StringUtil.padRight("", 5, 'q'));

        assertEquals("qqABC", StringUtil.padLeft("ABC", 5, 'q'));
        assertEquals("ABCqq", StringUtil.padRight("ABC", 5, 'q'));

        assertEquals("supercali", StringUtil.padLeft("supercali", 5, 'q'));
        assertEquals("fragilist", StringUtil.padRight("fragilist", 5, 'q'));
    }


    public void testPaddingNull() throws Exception
    {
        assertEquals("qqqqq", StringUtil.padLeft(null, 5, 'q'));
        assertEquals("qqqqq", StringUtil.padRight(null, 5, 'q'));
    }


    public void testLastChar() throws Exception
    {
        assertEquals('c', StringUtil.lastChar("abc"));
        assertEquals('a', StringUtil.lastChar("a"));
        assertEquals('\0', StringUtil.lastChar(""));
        assertEquals('\0', StringUtil.lastChar(null));
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


    public void testToUTF8ForNull() throws Exception
    {
        byte[] data = StringUtil.toUTF8(null);
        assertEquals(0, data.length);
    }


    public void testFromUTF8() throws Exception
    {
        byte[] data = new byte[]
                      {
                      (byte)'a', (byte)'b', (byte)0xC3, (byte)0xA7,
                      (byte)0xE2, (byte)0x9D, (byte)0x87
                      };
        assertEquals("ab\u00e7\u2747", StringUtil.fromUTF8(data));
    }


    public void testFromUTF8ForNull() throws Exception
    {
        assertEquals("", StringUtil.fromUTF8(null));
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


    public void testRandomAlpha() throws Exception
    {
        final int reps = 1000;
        final int minLength = 3;
        final int maxLength = 6;

        String[] strings = new String[reps];
        for (int ii = 0 ; ii < strings.length ; ii++)
            strings[ii] = StringUtil.randomAlphaString(minLength, maxLength);

        int[] lengthCounts = new int[maxLength + 1];
        int[] charCounts = new int[256];
        int totChars = 0;
        for (int ii = 0 ; ii < strings.length ; ii++)
        {
            int len = strings[ii].length();
            lengthCounts[len]++;
            for (int jj = 0 ; jj < len ; jj++)
            {
                charCounts[strings[ii].charAt(jj)]++;
                totChars++;
            }
        }

        int distByLength = reps / (maxLength - minLength + 1);
        for (int len = minLength ; len < maxLength ; len++)
        {
            NumericAsserts.assertApproximate(distByLength, lengthCounts[len], 30);
        }

        for (int c = 0 ; c < 256 ; c++)
        {
            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')))
            {
                assertTrue("alpha char with 0 count", charCounts[c] > 0);
            }
            else
            {
                assertEquals("non-alpha char with + count", 0, charCounts[c]);
            }
        }
    }


    public void testIntern() throws Exception
    {
        String s1 = new String("foo");
        String s2 = new String("foo");
        assertNotSame(s1, s2);

        String s3 = StringUtil.intern(s1);
        String s4 = StringUtil.intern(s2);
        assertSame(s3, s4);

        // to fully validate the behavior of intern(), we need to force a
        // garbage collection that actually cleans up the weak references
        // ... that's really difficult to do reliable, so we'll punt
    }


    public void testExtract() throws Exception
    {
        // the happy path
        assertEquals("foo",         StringUtil.extractLeft("foo:bar", ":"));
        assertEquals("bar",         StringUtil.extractRight("foo:bar", ":"));

        // the sad path
        assertEquals("foobar",      StringUtil.extractLeft("foobar", ":"));
        assertEquals("",            StringUtil.extractRight("foobar", ":"));

        // the bozo paths
        assertEquals("foobar",      StringUtil.extractLeft("foobar", ""));
        assertEquals("",            StringUtil.extractRight("foobar", ""));
        assertEquals("foobar",      StringUtil.extractLeft("foobar", null));
        assertEquals("",            StringUtil.extractRight("foobar", null));
        assertEquals("",            StringUtil.extractLeft(null, "baz"));
        assertEquals("",            StringUtil.extractRight(null, "baz"));

        // and a test to make sure that we handle a multi-character target
        assertEquals("foo",         StringUtil.extractLeft("foobazbar", "baz"));
        assertEquals("bar",         StringUtil.extractRight("foobazbar", "baz"));
    }


    public void testExtractFromLast() throws Exception
    {
        // the happy path
        assertEquals("/foo/bar",     StringUtil.extractLeftOfLast("/foo/bar/baz", "/"));
        assertEquals("baz",          StringUtil.extractRightOfLast("/foo/bar/baz", "/"));

        // the sad path
        assertEquals("/foo/bar/baz", StringUtil.extractLeftOfLast("/foo/bar/baz", ":"));
        assertEquals("",             StringUtil.extractRightOfLast("/foo/bar/baz", ":"));

        // the bozo paths
        assertEquals("/foo/bar/baz", StringUtil.extractLeftOfLast("/foo/bar/baz", ""));
        assertEquals("",             StringUtil.extractRightOfLast("/foo/bar/baz", ""));
        assertEquals("/foo/bar/baz", StringUtil.extractLeftOfLast("/foo/bar/baz", null));
        assertEquals("",             StringUtil.extractRightOfLast("/foo/bar/baz", null));
        assertEquals("",             StringUtil.extractLeftOfLast(null, "/"));
        assertEquals("",             StringUtil.extractRightOfLast(null, "/"));

        // and a check for multi-character separators
        assertEquals("foobazbar",    StringUtil.extractLeftOfLast("foobazbarbazbif", "baz"));
        assertEquals("bif",          StringUtil.extractRightOfLast("foobazbarbazbif", "baz"));
    }


    public void testIsIn() throws Exception
    {
        assertTrue("happy path",        StringUtil.isIn("foo", "foo", "bar", "baz"));
        assertFalse("sad path",         StringUtil.isIn("bif", "foo", "bar", "baz"));

        assertTrue("null target",       StringUtil.isIn("foo", null, "foo", "bar", "baz"));
        assertFalse("null test",        StringUtil.isIn(null,  "foo", "bar", "baz"));
        assertTrue("null teest/target", StringUtil.isIn(null, "foo", null, "bar", "baz"));
    }


    public void testEqualOrEmpty() throws Exception
    {
        assertTrue("equal strings",      StringUtil.equalOrEmpty("foo", "foo"));
        assertFalse("unequal strings",   StringUtil.equalOrEmpty("foo", "bar"));
        assertFalse("unequal to null 1", StringUtil.equalOrEmpty("foo", null));
        assertFalse("unequal to null 1", StringUtil.equalOrEmpty(null, "bar"));

        assertTrue("empty to null 1",    StringUtil.equalOrEmpty("", null));
        assertTrue("empty to null 2",    StringUtil.equalOrEmpty(null, ""));
    }
}
