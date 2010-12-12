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

import java.io.IOException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;


public class TestByteBufferInputStream
extends TestCase
{
    public void testSingleByteRead() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        assertEquals(0x01, in.read());
        assertEquals(0x02, in.read());
        assertEquals(0x03, in.read());

        // duplication intentional; always double-check EOF
        assertEquals(-1, in.read());
        assertEquals(-1, in.read());
    }


    // this is a redundant test given current implementation; leave it, in case
    // implementation changes
    public void testArrayRead() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        byte[] retr = new byte[1024];
        assertEquals(3, in.read(retr));
        assertEquals(0x01, retr[0]);
        assertEquals(0x02, retr[1]);
        assertEquals(0x03, retr[2]);

        assertEquals(-1, in.read(retr));
        assertEquals(-1, in.read(retr));
    }


    public void testArrayReadWithOffset() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        byte[] retr = new byte[1024];
        assertEquals(2, in.read(retr, 2, 2));
        assertEquals(0x00, retr[0]);
        assertEquals(0x00, retr[1]);
        assertEquals(0x01, retr[2]);
        assertEquals(0x02, retr[3]);
        assertEquals(0x00, retr[4]);

        assertEquals(1, in.read(retr, 0, 2));
        assertEquals(0x03, retr[0]);
        assertEquals(0x00, retr[1]);
        assertEquals(0x01, retr[2]);
        assertEquals(0x02, retr[3]);
        assertEquals(0x00, retr[4]);

        assertEquals(-1, in.read(retr, 0, 2));
        assertEquals(0x03, retr[0]);
        assertEquals(0x00, retr[1]);
        assertEquals(0x01, retr[2]);
        assertEquals(0x02, retr[3]);
        assertEquals(0x00, retr[4]);

        assertEquals(-1, in.read(retr, 0, 2));
    }


    public void testConstructWithOffset() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf, 3);
        assertEquals(0x04, in.read());

        assertEquals(-1, in.read());
        assertEquals(-1, in.read());
    }


    // curre t implementation makes use of available() internally, but test it anyway
    public void testAvailable() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        assertEquals(4, in.available());

        in.read();
        assertEquals(3, in.available());

        in.read(new byte[1024]);
        assertEquals(0, in.available());
    }


    public void testSkip() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        assertEquals(0x01, in.read());

        assertEquals(3L, in.skip(3L));
        assertEquals(0x05, in.read());

        assertEquals(2L, in.skip(Long.MAX_VALUE));
        assertEquals(-1, in.read());
    }



    public void testMarkAndReset() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        assertTrue(in.markSupported());

        assertEquals(0x01, in.read());
        in.mark(Integer.MAX_VALUE);

        assertEquals(0x02, in.read());
        assertEquals(0x03, in.read());
        in.reset();
        assertEquals(0x02, in.read());
        assertEquals(0x03, in.read());

        // note: we can reset even when at EOF
        in.read(new byte[1024]);
        assertEquals(-1, in.read());
        in.reset();
        assertEquals(0x02, in.read());
        assertEquals(0x03, in.read());
    }


    public void testResetFailsWithoutMark() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        try
        {
            in.reset();
            fail("reset without mark");
        }
        catch (IOException ee)
        {
            // success
        }
    }


    public void testReadFailsAfterClose() throws Exception
    {
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        ByteBuffer buf = ByteBuffer.wrap(data);

        ByteBufferInputStream in = new ByteBufferInputStream(buf);
        in.close();

        try
        {
            in.read();
            fail("read() succeeded after cose");
        }
        catch (IOException ee)
        {
            // success
        }

        try
        {
            in.read(new byte[1024]);
            fail("read(byte[]) succeeded after cose");
        }
        catch (IOException ee)
        {
            // success
        }

        try
        {
            in.read(new byte[1024], 0, 12);
            fail("read(byte[],off,len) succeeded after cose");
        }
        catch (IOException ee)
        {
            // success
        }
    }

}
