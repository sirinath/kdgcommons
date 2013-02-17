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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;


public class TestMonitoredOutputStream extends TestCase
{
    // an implementation that records all calls and has its own delegate
    private static class MyMonitoredOutputStream
    extends MonitoredOutputStream
    {
        public int numCalls;
        public long lastRead;
        public long totalBytes;

        public MyMonitoredOutputStream(OutputStream stream)
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


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testOneByteWrite() throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MyMonitoredOutputStream out = new MyMonitoredOutputStream(bos);

        assertEquals(0, out.numCalls);
        assertEquals(0, out.lastRead);
        assertEquals(0, out.totalBytes);

        out.write('A');
        assertEquals(1, out.numCalls);
        assertEquals(1, out.lastRead);
        assertEquals(1, out.totalBytes);

        out.write('B');
        assertEquals(2, out.numCalls);
        assertEquals(1, out.lastRead);
        assertEquals(2, out.totalBytes);

        assertEquals("AB", new String(bos.toByteArray(), "UTF-8"));
    }


    public void testMultiByteWrites() throws Exception
    {
        byte[] data = "ABCDEF".getBytes("UTF-8");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MyMonitoredOutputStream out = new MyMonitoredOutputStream(bos);

        out.write(data, 2, 3);
        assertEquals(1, out.numCalls);
        assertEquals(3, out.lastRead);
        assertEquals(3, out.totalBytes);

        out.write(data);
        assertEquals(2, out.numCalls);
        assertEquals(6, out.lastRead);
        assertEquals(9, out.totalBytes);

        assertEquals("CDEABCDEF", new String(bos.toByteArray(), "UTF-8"));
    }


    public void testFlushAndClose() throws Exception
    {
        final AtomicBoolean isFlushed = new AtomicBoolean(false);
        final AtomicBoolean isClosed = new AtomicBoolean(false);
        OutputStream os = new OutputStream()
        {
            @Override
            public void flush() throws IOException
            {
                isFlushed.set(true);
            }

            @Override
            public void close() throws IOException
            {
                isClosed.set(true);
            }

            @Override
            public void write(int b) throws IOException
            {
                // ignored
            }
        };

        MyMonitoredOutputStream out = new MyMonitoredOutputStream(os);
        assertFalse(isFlushed.get());
        assertFalse(isClosed.get());

        out.flush();
        assertTrue(isFlushed.get());
        assertFalse(isClosed.get());

        out.close();
        assertTrue(isClosed.get());
    }
}
