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


public class TestBase64Codec extends TestCase
{
    public void testNull() throws Exception
    {
        Base64Codec codec = new Base64Codec();

        String str = codec.toString(null);
        assertEquals("conversion to string", "", str);

        byte[] dst = codec.toBytes(null);
        ArrayAsserts.assertEquals("conversion to byte[]", new byte[0], dst);
    }


    public void testEmpty() throws Exception
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
}