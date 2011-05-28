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

package net.sf.kdgcommons.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import junit.framework.TestCase;


public class TestByteBufferOutputStream extends TestCase
{
    public void testSingleByteWrite() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        out.write(0x12);
        assertEquals(0x12, data[0]);

        // note that the high-order bits are discarded
        out.write(0x1234);
        assertEquals(0x34, data[1]);
        assertEquals(0x7F, data[2]);
    }


    public void testFullArrayWrite() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        out.write(new byte[] { 0x01, 0x02, 0x03, 0x04 });
        assertEquals(0x01, data[0]);
        assertEquals(0x02, data[1]);
        assertEquals(0x03, data[2]);
        assertEquals(0x04, data[3]);
        assertEquals(0x7F, data[4]);
    }


    public void testPartialArrayWrite() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        out.write(new byte[] { 0x01, 0x02, 0x03, 0x04 }, 1, 2);
        assertEquals(0x02, data[0]);
        assertEquals(0x03, data[1]);
        assertEquals(0x7F, data[2]);
    }


    public void testSetInitialPosition() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf, 2);

        out.write(new byte[] { 0x01, 0x02 });
        assertEquals(0x7F, data[0]);
        assertEquals(0x7F, data[1]);
        assertEquals(0x01, data[2]);
        assertEquals(0x02, data[3]);
        assertEquals(0x7F, data[4]);
    }


    public void testOversizeWriteFails() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        try
        {
            out.write(new byte[] { 0x01, 0x02, 0x03, 0x04 });
            fail("able to write outside of buffer");
        }
        catch (IOException ex)
        {
            // success
        }
    }


    public void testOversizeWriteFails2() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        out.write(0x01);
        out.write(0x02);

        try
        {
            out.write(0x03);
            fail("able to write outside of buffer");
        }
        catch (IOException ex)
        {
            // success
        }
    }


    public void testWriteFailsAfterClose() throws Exception
    {
        byte[] data = new byte[] { 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F };
        ByteBuffer buf = ByteBuffer.wrap(data);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buf);

        out.close();

        try
        {
            out.write(0x03);
            fail("able to write after close");
        }
        catch (IOException ex)
        {
            // success
        }

        try
        {
            out.write(new byte[] { 0x01 });
            fail("able to write after close");
        }
        catch (IOException ex)
        {
            // success
        }
    }


}
