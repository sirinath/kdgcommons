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

package net.sf.kdgcommons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;


public class TestTeeInputStream extends TestCase
{
    private final static String TEST_DATA = "Hello, World";

    private ByteArrayInputStream _base;
    private ByteArrayOutputStream _tee;
    private TeeInputStream _test;


    @Override
    protected void setUp() throws Exception
    {
        _base = new ByteArrayInputStream(TEST_DATA.getBytes("UTF-8"));
        _tee = new ByteArrayOutputStream();
        _test = new TeeInputStream(_base, _tee);
    }


    public void testAvailable() throws Exception
    {
        assertEquals(_base.available(), _test.available());
    }


    public void testReadSingleBytes() throws Exception
    {
        int b = _test.read();
        assertEquals((int)TEST_DATA.charAt(0), b);

        byte[] out = _tee.toByteArray();
        assertEquals(1, out.length);
        assertEquals((int)TEST_DATA.charAt(0), (int)out[0]);

        int b2 = _test.read();
        assertEquals((int)TEST_DATA.charAt(1), b2);

        byte[] out2 = _tee.toByteArray();
        assertEquals(2, out2.length);
        assertEquals((int)TEST_DATA.charAt(0), (int)out2[0]);
        assertEquals((int)TEST_DATA.charAt(1), (int)out2[1]);
    }


    public void testReadSingleByteAtEOF() throws Exception
    {
        _test = new TeeInputStream(new ByteArrayInputStream(new byte[0]),
                                   _tee);
        int b = _test.read();
        assertEquals(-1, b);

        byte[] out = _tee.toByteArray();
        assertEquals(0, out.length);

        int b2 = _test.read();
        assertEquals(-1, b2);

        byte[] out2 = _tee.toByteArray();
        assertEquals(0, out2.length);
    }


    public void testReadIntoBuffer() throws Exception
    {
        byte[] buf = new byte[3];
        assertEquals(3, _test.read(buf));
        assertEquals((byte)TEST_DATA.charAt(0), buf[0]);
        assertEquals((byte)TEST_DATA.charAt(1), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(2), buf[2]);

        byte[] out = _tee.toByteArray();
        assertEquals(3, out.length);
        assertEquals((byte)TEST_DATA.charAt(0), out[0]);
        assertEquals((byte)TEST_DATA.charAt(1), out[1]);
        assertEquals((byte)TEST_DATA.charAt(2), out[2]);
    }


    public void testReadIntoOversizedBuffer() throws Exception
    {
        byte[] buf = new byte[1024];
       assertEquals(TEST_DATA.length(), _test.read(buf));

        byte[] out = _tee.toByteArray();
        assertEquals(TEST_DATA.length(), out.length);
    }


    public void testReadIntoBufferWithOffset() throws Exception
    {
        byte[] buf = new byte[5];
        assertEquals(3, _test.read(buf, 1, 3));
        assertEquals((byte)0, buf[0]);
        assertEquals((byte)TEST_DATA.charAt(0), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(1), buf[2]);
        assertEquals((byte)TEST_DATA.charAt(2), buf[3]);
        assertEquals((byte)0, buf[4]);

        byte[] out = _tee.toByteArray();
        assertEquals(3, out.length);
        assertEquals((byte)TEST_DATA.charAt(0), out[0]);
        assertEquals((byte)TEST_DATA.charAt(1), out[1]);
        assertEquals((byte)TEST_DATA.charAt(2), out[2]);
    }


    public void testMarkAndReset() throws Exception
    {
        assertTrue(_test.markSupported());

        byte[] buf = new byte[3];

        _test.mark(10);
        assertEquals(3, _test.read(buf));
        assertEquals((byte)TEST_DATA.charAt(0), buf[0]);
        assertEquals((byte)TEST_DATA.charAt(1), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(2), buf[2]);

        _test.reset();
        assertEquals(3, _test.read(buf));
        assertEquals((byte)TEST_DATA.charAt(0), buf[0]);
        assertEquals((byte)TEST_DATA.charAt(1), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(2), buf[2]);

        byte[] out = _tee.toByteArray();
        assertEquals(6, out.length);
        assertEquals((byte)TEST_DATA.charAt(0), out[0]);
        assertEquals((byte)TEST_DATA.charAt(1), out[1]);
        assertEquals((byte)TEST_DATA.charAt(2), out[2]);
        assertEquals((byte)TEST_DATA.charAt(0), out[3]);
        assertEquals((byte)TEST_DATA.charAt(1), out[4]);
        assertEquals((byte)TEST_DATA.charAt(2), out[5]);
    }


    public void testSkip() throws Exception
    {
        byte[] buf = new byte[3];

        assertEquals(2, _test.skip(2L));
        assertEquals(3, _test.read(buf));
        assertEquals((byte)TEST_DATA.charAt(2), buf[0]);
        assertEquals((byte)TEST_DATA.charAt(3), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(4), buf[2]);

        assertEquals(4, _test.skip(4L));
        assertEquals(3, _test.read(buf));
        assertEquals((byte)TEST_DATA.charAt(9), buf[0]);
        assertEquals((byte)TEST_DATA.charAt(10), buf[1]);
        assertEquals((byte)TEST_DATA.charAt(11), buf[2]);

        byte[] out = _tee.toByteArray();
        assertEquals(6, out.length);
        assertEquals((byte)TEST_DATA.charAt(2), out[0]);
        assertEquals((byte)TEST_DATA.charAt(3), out[1]);
        assertEquals((byte)TEST_DATA.charAt(4), out[2]);
        assertEquals((byte)TEST_DATA.charAt(9), out[3]);
        assertEquals((byte)TEST_DATA.charAt(10), out[4]);
        assertEquals((byte)TEST_DATA.charAt(11), out[5]);
    }
}
