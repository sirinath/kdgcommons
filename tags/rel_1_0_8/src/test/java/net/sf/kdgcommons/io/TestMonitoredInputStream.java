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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;


public class TestMonitoredInputStream extends TestCase
{
    // an implementation that records all calls
    private static class MyMonitoredInputStream
    extends MonitoredInputStream
    {
        public int numCalls;
        public long lastRead;
        public long totalBytes;

        public MyMonitoredInputStream(InputStream stream)
        {
            super(stream);
        }

        @Override
        public void progress(long last, long total)
        {
            numCalls++;
            lastRead = last;
            totalBytes = total;
        }
    }


    private static MyMonitoredInputStream createTestStream(String content)
    throws Exception
    {
        return new MyMonitoredInputStream(
                new ByteArrayInputStream(
                        content.getBytes("UTF-8")));
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testOneByteRead() throws Exception
    {
        MyMonitoredInputStream in = createTestStream("ABC");

        assertEquals(0, in.numCalls);
        assertEquals(0, in.lastRead);
        assertEquals(0, in.totalBytes);

        assertEquals('A', in.read());
        assertEquals(1, in.numCalls);
        assertEquals(1, in.lastRead);
        assertEquals(1, in.totalBytes);

        assertEquals('B', in.read());
        assertEquals(2, in.numCalls);
        assertEquals(1, in.lastRead);
        assertEquals(2, in.totalBytes);

        assertEquals('C', in.read());
        assertEquals(3, in.numCalls);
        assertEquals(1, in.lastRead);
        assertEquals(3, in.totalBytes);

        assertEquals(-1, in.read());
        assertEquals(4, in.numCalls);
        assertEquals(0, in.lastRead);
        assertEquals(3, in.totalBytes);
    }


    public void testMultiByteReads() throws Exception
    {
        MyMonitoredInputStream in = createTestStream("ABCDEFGHI");

        byte[] buf1 = new byte[4];
        assertEquals(2, in.read(buf1, 2, 2));
        assertEquals('A', buf1[2]);
        assertEquals('B', buf1[3]);
        assertEquals(1, in.numCalls);
        assertEquals(2, in.lastRead);
        assertEquals(2, in.totalBytes);

        byte[] buf2 = new byte[4];
        assertEquals(4, in.read(buf2));
        assertEquals('C', buf2[0]);
        assertEquals('F', buf2[3]);
        assertEquals(2, in.numCalls);
        assertEquals(4, in.lastRead);
        assertEquals(6, in.totalBytes);

        byte[] buf3 = new byte[16];
        assertEquals(3, in.read(buf3));
        assertEquals('G', buf3[0]);
        assertEquals('I', buf3[2]);
        assertEquals(0, buf3[3]);
        assertEquals(3, in.numCalls);
        assertEquals(3, in.lastRead);
        assertEquals(9, in.totalBytes);

        byte[] buf4 = new byte[16];
        assertEquals(-1, in.read(buf4));
        assertEquals(4, in.numCalls);
        assertEquals(0, in.lastRead);
        assertEquals(9, in.totalBytes);
    }


    public void testMarkAndReset() throws Exception
    {
        ByteArrayInputStream delegate = new ByteArrayInputStream("ABCDEFGHIJKL".getBytes("UTF-8"));
        MyMonitoredInputStream in = new MyMonitoredInputStream(delegate);
        assertTrue(in.markSupported());

        byte[] buf1 = new byte[3];
        assertEquals(3, in.read(buf1));
        in.mark(1000);

        byte[] buf2 = new byte[6];
        assertEquals(6, in.read(buf2));
        assertEquals(2, in.numCalls);
        assertEquals(6, in.lastRead);
        assertEquals(9, in.totalBytes);

        in.reset();

        byte[] buf3 = new byte[6];
        assertEquals(6, in.read(buf3));
        assertEquals(3, in.numCalls);
        assertEquals(6, in.lastRead);
        assertEquals(15, in.totalBytes);

        assertTrue("read after reset returns expected bytes",
                   Arrays.equals(buf2, buf3));
    }


    public void testSkip() throws Exception
    {
        MyMonitoredInputStream in = createTestStream("ABCDEFGHI");

        in.skip(4);
        assertEquals(1, in.numCalls);
        assertEquals(4, in.lastRead);
        assertEquals(4, in.totalBytes);

        assertEquals('E', in.read());
    }



    public void testAvailable() throws Exception
    {
        MyMonitoredInputStream in = createTestStream("ABCDEFGHI");

        assertEquals(9, in.available());
        assertEquals(0, in.numCalls);
        assertEquals(0, in.lastRead);
        assertEquals(0, in.totalBytes);
    }


    public void testClose() throws Exception
    {
        // ByteArrayInputStream doesn't care about close, so we'll create
        // something that does
        final AtomicBoolean isClosed = new AtomicBoolean(false);
        InputStream is = new InputStream()
        {
            @Override
            public void close() throws IOException
            {
                isClosed.set(true);
            }

            @Override
            public int read() throws IOException
            {
                return 0;
            }
        };

        MyMonitoredInputStream in = new MyMonitoredInputStream(is);
        in.close();
        assertTrue(isClosed.get());
    }
}
