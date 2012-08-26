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
        // to test this, I need an InputStream that won't read its entire data
        // in a single pass; since ByteArrayInputstream will let me manage the
        // data easily, it will be the base ... note that the bytes follow a
        // pattern (1..127), so we can easily verify reads

        byte[] orig = new byte[1024];
        for (int ii = 0 ; ii < orig.length ; ii++)
            orig[ii] = (byte)(ii % 127 + 1);

        final int readLimit = 256;

        InputStream in = new ByteArrayInputStream(orig)
        {
            @Override
            public synchronized int read(byte[] b, int off, int len)
            {
                len = Math.min(len, readLimit);
                return super.read(b, off, len);
            }

            @Override
            public int read(byte[] b) throws IOException
            {
                return read(b, 0, b.length);
            }
        };

        // first assert that raw reads reads are limited
        byte[] b0 = new byte[512];
        int r0 = in.read(b0);
        assertEquals("raw reads are limited",   readLimit, r0);
        assertEquals("read first byte",         orig[0], b0[0]);
        assertEquals("read last byte",          orig[readLimit - 1], b0[readLimit - 1]);
        assertEquals("did not over-read",       0, b0[readLimit]);

        // then assert that readFully() does its thing
        byte[] b1 = new byte[514];
        int r1 = IOUtil.readFully(in, b1);
        assertEquals("read desired length", b1.length, r1);
        assertEquals("read first byte",     orig[r0], b1[0]);
        assertEquals("read last byte",      orig[r0 + r1 - 1], b1[r1 - 1]);

        // finally assert that we return at end-of-file
        byte[] b2 = new byte[512];
        int r2 = IOUtil.readFully(in, b2);
        assertEquals("read to EOF",         (orig.length - (r0 + r1)), r2);
        assertEquals("read first byte",     orig[r0 + r1], b2[0]);
        assertEquals("read last byte",      orig[orig.length - 1], b2[r2 - 1]);
    }
}
