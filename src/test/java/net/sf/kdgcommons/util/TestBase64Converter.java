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

package net.sf.kdgcommons.util;

import junit.framework.TestCase;

import net.sf.kdgcommons.lang.StringUtil;


public class TestBase64Converter extends TestCase
{
    /**
     *  Test strings from RFC4648 section 10. First string is the unencoded
     *  value, second is the encoded.
     */
    private final static String[][] TEST_STRINGS = new String[][]
    {
        new String[] { "",       "" },
        new String[] { "f",      "Zg==" },
        new String[] { "fo",     "Zm8=" },
        new String[] { "foo",    "Zm9v" },
        new String[] { "foob",   "Zm9vYg==" },
        new String[] { "fooba",  "Zm9vYmE=" },
        new String[] { "foobar", "Zm9vYmFy" }
    };

    
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    /**
     *  Performs an encoding of a source array, and asserts that it produced
     *  the expected bytes. This takes strings as both input and output, for
     *  compactness (and also because our source of test data uses strings).
     */
    private void assertEncoding(String source, String expected)
    {
        byte[] src = StringUtil.toLatinBytes(source);
        byte[] exp = StringUtil.toLatinBytes(expected);
        byte[] dst = Base64Converter.encode(src);
        assertEquals(exp.length, dst.length);
        for (int ii = 0; ii < exp.length ; ii++)
            assertEquals("byte " + ii, exp[ii], dst[ii]);
    }

    
    /**
     *  Performs a decoding of a source array, and asserts that it produced
     *  the expected bytes. This takes strings as both input and output, for
     *  compactness (and also because our source of test data uses strings).
     */
    private void assertDecoding(String source, String expected)
    {
        byte[] src = StringUtil.toLatinBytes(source);
        byte[] exp = StringUtil.toLatinBytes(expected);
        byte[] dst = Base64Converter.decode(src);
        assertEquals(exp.length, dst.length);
        for (int ii = 0; ii < exp.length ; ii++)
            assertEquals("byte " + ii, exp[ii], dst[ii]);
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testValidChars() throws Exception
    {
        assertTrue(Base64Converter.isBase64Char('A'));
        assertTrue(Base64Converter.isBase64Char('q'));
        assertTrue(Base64Converter.isBase64Char('9'));
        assertTrue(Base64Converter.isBase64Char('+'));
        assertTrue(Base64Converter.isBase64Char('/'));
        assertTrue(Base64Converter.isBase64Char('='));

        assertFalse(Base64Converter.isBase64Char('\r'));
        assertFalse(Base64Converter.isBase64Char('\n'));
        assertFalse(Base64Converter.isBase64Char('@'));
        assertFalse(Base64Converter.isBase64Char((char)0xFF));
    }


    public void testEncode() throws Exception
    {
        // this will throw if we actually try to write something
        assertEquals(0, Base64Converter.encode(new byte[0], 0, 0, new byte[0], 0));

        for (int ii = 0 ; ii < TEST_STRINGS.length ; ii++)
            assertEncoding(TEST_STRINGS[ii][0], TEST_STRINGS[ii][1]);
    }


    public void testDecode() throws Exception
    {
        // this will throw if we actually try to write something
        assertEquals(0, Base64Converter.decode(new byte[0], 0, 0, new byte[0], 0));

        for (int ii = 0 ; ii < TEST_STRINGS.length ; ii++)
            assertDecoding(TEST_STRINGS[ii][1], TEST_STRINGS[ii][0]);
    }


    public void testDecodeString() throws Exception
    {
        for (int ii = 0 ; ii < TEST_STRINGS.length ; ii++)
        {
            String decoded = StringUtil.toLatinString(
                                Base64Converter.decode(TEST_STRINGS[ii][1]));
            assertEquals(TEST_STRINGS[ii][0], decoded);
        }

        assertEquals("foobar",
                     StringUtil.toLatinString( Base64Converter.decode("Z m9v\nYm\n?Fy")));
    }
}
