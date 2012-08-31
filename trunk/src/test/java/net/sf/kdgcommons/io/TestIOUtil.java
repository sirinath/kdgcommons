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
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

import net.sf.kdgcommons.test.SimpleMock;


public class TestIOUtil
extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    /**
     *  A stream that will only play out its data a smidge at a time. Rather
     *  than give it data, we simply walk the byte values 0..127.
     */
    private static class ReadLimitedInputStream
    extends InputStream
    {
        private int _readLimit;
        private int _max;
        private int _next;
        private boolean _wasClosed = false;

        public ReadLimitedInputStream(int readLimit, int max)
        {
            _readLimit = readLimit;
            _max = max;
        }

        private byte nextByte()
        {
            return (byte)(_next++ % 128);
        }

        public boolean isClosed()
        {
            return _wasClosed;
        }


        @Override
        public void close() throws IOException
        {
            _wasClosed = true;
        }

        @Override
        public int read() throws IOException
        {
            return (_next < _max) ? nextByte() : -1;
        }

        @Override
        public synchronized int read(byte[] b, int off, int len)
        {
            int ret = 0;
            for (int ii = 0 ; ii < Math.min(len, _readLimit) ; ii++)
            {
                if (_next >= _max)
                    break;
                b[off + ii] = nextByte();
                ret++;
            }
            if (ret > 0)
                return ret;
            if (_next >= _max)
                return -1;
            return 0;
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            return read(b, 0, b.length);
        }

        @Override
        public long skip(long n) throws IOException
        {
            int toSkip = (int)Math.min(_readLimit, n);
            if ((_next + toSkip) > _max)
                toSkip = _max - _next;
            _next += toSkip;
            return toSkip;
        }
    }


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    public void testCloseQuietly() throws Exception
    {
        SimpleMock proxy = new SimpleMock();

        Closeable mock = proxy.getInstance(Closeable.class);
        IOUtil.closeQuietly(mock);

        proxy.assertCallCount(1);
        proxy.assertCall(0, "close");
    }


    public void testCloseQuietlyWithException() throws Exception
    {
        Closeable mock = new Closeable()
        {
            public void close() throws IOException
            {
                throw new IOException();
            }
        };

        // getting through here is sufficient
        IOUtil.closeQuietly(mock);
    }


    public void testCloseQuietlyWithNull() throws Exception
    {
        // getting through here is sufficient
        IOUtil.closeQuietly(null);
    }


    public void testCopy() throws Exception
    {
        // whitebox test: we want to read at least one full buffer, so need
        // to create a large string to copy
        StringBuilder buf = new StringBuilder();
        while (buf.length() < 65536)
            buf.append("12345678901234567890");
        String content = buf.toString();

        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertEquals("bytes copied", content.length(), IOUtil.copy(in, out));
        assertEquals("content", content, new String(out.toByteArray(), "UTF-8"));
    }


    public void testCopyWithNullParams() throws Exception
    {
        assertEquals("input null",
                     0, IOUtil.copy(null, new ByteArrayOutputStream()));
        assertEquals("output null",
                     0, IOUtil.copy(new ByteArrayInputStream(new byte[4]), null));
    }


    public void testOpenFile() throws Exception
    {
        File file = File.createTempFile("TestIOUtil", ".tmp");
        file.deleteOnExit();
        FileOutputStream out = new FileOutputStream(file);
        out.write("test".getBytes());
        out.close();

        // by using the file's name, we test two functions for the price of one
        // ... and there's no good reason to read more than one byte, we're not
        // testing file writing here
        InputStream in = IOUtil.openFile(file.getPath());
        assertEquals('t', in.read());
        in.close();
    }


    public void testOpenGZippedFile() throws Exception
    {
        File file = File.createTempFile("TestIOUtil", ".tmp.gz");
        file.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(file);
        GZIPOutputStream out = new GZIPOutputStream(fos);
        out.write("test".getBytes());
        out.close();

        InputStream in = IOUtil.openFile(file.getPath());
        assertEquals('t', in.read());
        in.close();
    }


    // this test is for coverage ... I don't know any way to track the number of
    // open file descriptors from the JDK, and looping until FD exhaustion seems
    // like a bad test
    public void testOpenFileFailure() throws Exception
    {
        File file = File.createTempFile("TestIOUtil", ".tmp.gz");
        file.deleteOnExit();
        FileOutputStream out = new FileOutputStream(file);
        out.write("test".getBytes());
        out.close();

        try
        {
            // we claim to be GZipped but aren't; the GZIPInputStream ctor will throw
            IOUtil.openFile(file.getPath());
            fail("did not throw on invalid file format");
        }
        catch (IOException ex)
        {
            // success ... now we just need to measure open FDs
        }
    }


    // this doesn't fully test the method; we'd have to restart the JVM to do that
    public void testCreateTempFile() throws Exception
    {
        final String prefix = "testCreateTempFile";
        final long size = 8192;

        File file = IOUtil.createTempFile(prefix, size);
        assertTrue(file.exists());
        assertTrue("prefix", file.getName().startsWith(prefix));
        assertTrue("suffix", file.getName().endsWith(".tmp"));
        assertEquals("size", size, file.length());
    }


    public void testCreateTempFileFromStream() throws Exception
    {
        byte[] content = "this is a test".getBytes("UTF-8");

        // I need to track that the input stream was closed, so need an override
        final AtomicBoolean wasClosed = new AtomicBoolean(false);
        ByteArrayInputStream in = new ByteArrayInputStream(content)
        {
            @Override
            public void close() throws IOException
            {
                wasClosed.set(true);
                super.close();
            }
        };

        File file = IOUtil.createTempFile(in, "testCreateTempFileFromStream");
        assertEquals("file size", content.length, (int)file.length());
        assertTrue("file was closed", wasClosed.get());

        // whitebox: this method is based on copy(), so no need to validate content if length is right
    }


    public void testReadFully() throws Exception
    {
        ReadLimitedInputStream in = new ReadLimitedInputStream(250, 1024);

        // first assert that raw reads reads are limited
        byte[] b1 = new byte[512];
        int r1 = in.read(b1);
        assertEquals("raw reads are limited",   250, r1);
        assertEquals("read first byte",         0, b1[0]);
        assertEquals("read last byte",          121, b1[249]);
        assertEquals("did not over-read",       0, b1[250]);
        assertFalse("stream was closed",        in.isClosed());

        // then assert that readFully() does its thing
        byte[] b2 = new byte[514];
        int r2 = IOUtil.readFully(in, b2);
        assertEquals("read desired length",     b2.length, r2);
        assertEquals("read first byte",         122, b2[0]);
        assertEquals("read last byte",          123, b2[513]);
        assertFalse("stream was closed",        in.isClosed());

        // assert that we return at end-of-file
        byte[] b3 = new byte[512];
        int r3 = IOUtil.readFully(in, b3);
        assertEquals("read to EOF",             260, r3);
        assertEquals("read first byte",         124, b3[0]);
        assertEquals("read last byte",          127, b3[259]);
        assertFalse("stream was closed",        in.isClosed());

        // and attempting to read past EOF doesn't return anything
        byte[] b4 = new byte[512];
        int r4 = IOUtil.readFully(in, b4);
        assertEquals("read past EOF",           0, r4);
        assertFalse("stream was closed",        in.isClosed());
    }


    public void testSkipFully() throws Exception
    {
        ReadLimitedInputStream in = new ReadLimitedInputStream(250, 1024);

        // as above, check built-in function
        long s1 = in.skip(256);
        assertEquals("bytes skipped by stream", 250, s1);
        assertEquals("next byte",               122, in.read());    // 250 % 128

        // then a complete skip
        long s2 = IOUtil.skipFully(in, 700);
        assertEquals("bytes skipped",           700, s2);
        assertEquals("next byte",               55, in.read());     // 951 % 128

        // then skip to end
        long s3 = IOUtil.skipFully(in, 700);
        assertEquals("bytes skipped",           72, s3);
        assertEquals("next byte",               -1, in.read());

        // skipping past end should do nothing
        long s4 = IOUtil.skipFully(in, 700);
        assertEquals("bytes skipped",           0, s4);
    }
}
