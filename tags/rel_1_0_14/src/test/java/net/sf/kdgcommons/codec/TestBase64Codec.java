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

package net.sf.kdgcommons.codec;

import junit.framework.TestCase;

import net.sf.kdgcommons.codec.Base64Codec.Option;
import net.sf.kdgcommons.test.ArrayAsserts;


public class TestBase64Codec
extends TestCase
{
    public void testNullArray() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] enc = codec.encode(null);
        assertTrue("encode(null) returned empty array", enc.length == 0);

        byte[] dec = codec.decode(null);
        assertTrue("decode(null) returned empty array", dec.length == 0);
    }


    public void testEmptyArray() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] enc = codec.encode(new byte[0]);
        assertTrue("encode(byte[0]) returned empty array", enc.length == 0);

        byte[] dec = codec.decode(new byte[0]);
        assertTrue("decode(byte[0]) returned empty array", dec.length == 0);
    }


    public void testNullString() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        String str = codec.toString(null);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes(null);
        ArrayAsserts.assertEquals("conversion to byte[]", new byte[0], dst);
    }


    public void testEmptyString() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        String str = codec.toString(new byte[0]);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes("");
        ArrayAsserts.assertEquals("conversion to byte[]", new byte[0], dst);
    }


    public void testUnbrokenStringOneByte() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] src = new byte[] { 0x12 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "Eg==", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testUnbrokenStringTwoBytes() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] src = new byte[] { 0x12, 0x34 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "EjQ=", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testUnbrokenStringThreeBytes() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] src = new byte[] { 0x12, 0x34, 0x56 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "EjRW", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testUnbrokenStringMultipleGroups() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "EjRWeJq83vA=", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testSeparator() throws Exception
    {
        Base64Codec codec = new Base64Codec(4, "X");

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "EjRWXeJq8X3vA=", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testMultibyteSeparator() throws Exception
    {
        Base64Codec codec = new Base64Codec(4, "XYZ");

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "EjRWXYZeJq8XYZ3vA=", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testEndOfTableStandardEncoding() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] src = new byte[] { (byte)0xEF, (byte)0xCF, (byte)0x7E, (byte)0xFC };

        String str = codec.toString(src);
        assertEquals("conversion to string", "789+/A==", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testFilenameEncoding() throws Exception
    {
        Base64Codec codec = new Base64Codec(Option.FILENAME);

        byte[] src = new byte[] { (byte)0xEF, (byte)0xCF, (byte)0x7E, (byte)0xFC };

        String str = codec.toString(src);
        assertEquals("conversion to string", "789-_A", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testConversionToBytesIgnoresWhitespace() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        byte[] exp = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };
        String str = "Ej  RW\reJ\t q8\n3vA=";

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", exp, dst);
    }


    public void testConversionToBytesThrowsOnInvalid() throws Exception
    {
        try
        {
            new HexCodec().toBytes("f^oo");
            fail("converted string with non-Base64 character");
        }
        catch (InvalidSourceByteException ex)
        {
            assertEquals("exception identifies incorrect byte", '^', ex.getInvalidByte());
        }
    }


    public void testConversionToBytesThrowsIfUnpaddedAndPaddingRequired() throws Exception
    {
        try
        {
            new HexCodec().toBytes("3vA");
            fail("converted incorrectly-padded string");
        }
        catch (CodecException ex)
        {
            // success
        }
    }
}