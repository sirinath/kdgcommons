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

import net.sf.kdgcommons.test.ArrayAsserts;


public class TestHexCodec
extends TestCase
{
    public void testNull() throws Exception
    {
        HexCodec codec = new HexCodec();

        String str = codec.toString(null);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes(null);
        ArrayAsserts.assertEquals("conversion to byte[]", new byte[0], dst);
    }


    public void testEmpty() throws Exception
    {
        HexCodec codec = new HexCodec();

        String str = codec.toString(new byte[0]);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes("");
        ArrayAsserts.assertEquals("conversion to byte[]", new byte[0], dst);
    }


    public void testUnbrokenString() throws Exception
    {
        HexCodec codec = new HexCodec();

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "123456789ABCDEF0", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testBrokenString() throws Exception
    {
        HexCodec codec = new HexCodec(4, "X");

        byte[] src = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        String str = codec.toString(src);
        assertEquals("conversion to string", "1234X5678X9ABCXDEF0", str);

        byte[] dst = codec.toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", src, dst);
    }


    public void testConversionToBytesIgnoresWhitespace() throws Exception
    {
        String str = "123456 78  9ABC\nDEF\t0";
        byte[] exp = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0 };

        byte[] dst = new HexCodec().toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", exp, dst);
    }


    public void testConversionToBytesIgnoresExtraNibble() throws Exception
    {
        String str = "12345";
        byte[] exp = new byte[] { 0x12, 0x34 };

        byte[] dst = new HexCodec().toBytes(str);
        ArrayAsserts.assertEquals("conversion to byte[]", exp, dst);
    }


    public void testConversionToBytesThrowsOnNonHex() throws Exception
    {
        try
        {
            new HexCodec().toBytes("foo");
            fail("converted string with non-hex character");
        }
        catch (InvalidSourceByteException ex)
        {
            assertEquals("exception identifies incorrect byte", 'o', ex.getInvalidByte());
        }
    }
}
