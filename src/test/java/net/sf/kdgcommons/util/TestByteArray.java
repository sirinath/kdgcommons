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


public class TestByteArray extends TestCase
{
    public void testConstructors() throws Exception
    {
        ByteArray a1 = new ByteArray();
        assertEquals(0, a1.size());

        ByteArray a2 = new ByteArray(new byte[4]);
        assertEquals(4, a2.size());

        // only ASCII characters in string, please
        ByteArray a3 = new ByteArray("ABC");
        assertEquals(3, a3.size());
    }


    public void testAddGet() throws Exception
    {
        ByteArray array = new ByteArray();

        array.add((byte)254);
        assertEquals(1, array.size());
        assertEquals((byte)254, array.get(0));

        array.add(new byte[] {11, 21, 31});
        assertEquals(4, array.size());
        assertEquals((byte)11, array.get(1));
        assertEquals((byte)21, array.get(2));
        assertEquals((byte)31, array.get(3));

        array.add(new byte[0]);
        assertEquals(4, array.size());

        array.add('+');
        assertEquals(5, array.size());
        assertEquals((byte)43, array.get(4));

        // ascii chars only!
        array.add("ABC");
        assertEquals(8, array.size());
        assertEquals((byte)65, array.get(5));
        assertEquals((byte)66, array.get(6));
        assertEquals((byte)67, array.get(7));

        array.add(new ByteArray("abc"));
        assertEquals(11, array.size());
        assertEquals((byte)97, array.get(8));
        assertEquals((byte)98, array.get(9));
        assertEquals((byte)99, array.get(10));

        try
        {
            array.get(11);
            fail("able to get outside array bounds");
        }
        catch (IndexOutOfBoundsException e)
        {
            // success
        }
    }


    public void testGetArray() throws Exception
    {
        ByteArray array = new ByteArray("ABCDEFGHIJKL");

        byte[] t1 = array.getArray();
        byte[] t2 = array.getArray();
        assertSame(t1, t2);
        assertTrue(t1.length >= 12);
        assertEquals('A', t1[0]);

        byte[] t3 = array.getBytes();
        assertNotSame(t1, t3);
        assertEquals(12, t3.length);
        assertEquals('A', t3[0]);
        assertEquals('L', t3[11]);

        byte[] t4 = array.getBytes(2, 4);
        assertEquals(4, t4.length);
        assertEquals('C', t4[0]);
        assertEquals('F', t4[3]);

        byte[] t5 = array.getBytes(6);
        assertEquals(6, t5.length);
        assertEquals('G', t5[0]);
        assertEquals('L', t5[5]);
    }


    public void testInsert() throws Exception
    {
        ByteArray array = new ByteArray();

        array.insert(0, new byte[0]);
        assertEquals(0, array.size());

        array.insert(0, new byte[] {1, 2, 3});
        assertEquals(3, array.size());
        assertEquals(1, array.get(0));
        assertEquals(2, array.get(1));
        assertEquals(3, array.get(2));

        array.insert(1, new byte[] {11, 12, 13}, 1, 2);
        assertEquals(5, array.size());
        assertEquals(1, array.get(0));
        assertEquals(12, array.get(1));
        assertEquals(13, array.get(2));
        assertEquals(2, array.get(3));
        assertEquals(3, array.get(4));

        try
        {
            array.insert(1, new byte[] {11, 12, 13}, 1, 17);
            fail("able to insert invalid source offsets");
        }
        catch (IllegalArgumentException e)
        {
            // success
        }

        array.insert(1, new ByteArray());
        assertEquals(5, array.size());

        array.insert(1, new ByteArray("ABC"));
        assertEquals(8, array.size());
        assertEquals(1, array.get(0));
        assertEquals(65, array.get(1));
        assertEquals(66, array.get(2));
        assertEquals(67, array.get(3));
        assertEquals(12, array.get(4));
        assertEquals(13, array.get(5));
        assertEquals(2, array.get(6));
        assertEquals(3, array.get(7));

        array.insert(1, new ByteArray("abc"), 1, 1);
        assertEquals(9, array.size());
        assertEquals(1, array.get(0));
        assertEquals(98, array.get(1));
        assertEquals(65, array.get(2));
        assertEquals(66, array.get(3));
        assertEquals(67, array.get(4));
        assertEquals(12, array.get(5));
        assertEquals(13, array.get(6));
        assertEquals(2, array.get(7));
        assertEquals(3, array.get(8));
    }


    public void testResize() throws Exception
    {
        ByteArray array = new ByteArray("ABCDEFGHIJKL");
        assertEquals(12, array.size());

        array.setSize(6);
        assertEquals(6, array.size());
        assertEquals('A', array.get(0));
        assertEquals('F', array.get(5));

        array.setSize(12);
        assertEquals(12, array.size());
        assertEquals('A', array.get(0));
        assertEquals('F', array.get(5));
        for (int ii = 6 ; ii < 12 ; ii++)
        {
            assertEquals("index: " + ii, 0, array.get(ii));
        }
    }


    public void testAddSegment() throws Exception
    {
        ByteArray array = new ByteArray();
        byte[] bytes = new byte[] { (byte)1, (byte)2, (byte)3 };

        array.add(bytes, 1, 1);
        assertEquals(1, array.size());
        assertEquals(2, array.get(0));

        array.add(bytes, 0, 2);
        assertEquals(3, array.size());
        assertEquals(1, array.get(1));
        assertEquals(2, array.get(2));
    }
}
