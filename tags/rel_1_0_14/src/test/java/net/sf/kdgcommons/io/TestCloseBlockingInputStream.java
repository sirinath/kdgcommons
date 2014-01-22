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

import junit.framework.TestCase;

import net.sf.kdgcommons.test.ArrayAsserts;


public class TestCloseBlockingInputStream extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    /**
     *  A stream for testing the flush and close operations.
     */
    private static class MyMockInputStream
    extends InputStream
    {
        @Override
        public int read() throws IOException
        {
            fail("read() should not be called by this test");
            return -1; // fail() isn't considered an exception
        }

        @Override
        public void close() throws IOException
        {
            fail("close called on underlying stream");
        }
    }

//----------------------------------------------------------------------------
//  Test cases
//----------------------------------------------------------------------------

    @SuppressWarnings("resource")
    public void testReading() throws Exception
    {
        byte[] data = new byte[] { 64, 65, 66, 67, 68, 69, 70 };
        ByteArrayInputStream base = new ByteArrayInputStream(data);
        CloseBlockingInputStream test = new CloseBlockingInputStream(base);

        byte[] r0 = new byte[4];
        assertEquals("read of full byte array, return", 4, test.read(r0));
        ArrayAsserts.assertEquals("read of full byte array, data", new byte[] { 64, 65, 66, 67 }, r0);

        byte[] r1 = new byte[4];
        assertEquals("read of partial byte array, return", 2, test.read(r1, 1, 2));
        ArrayAsserts.assertEquals("read of partial byte array, data", new byte[] { 0, 68, 69, 0 }, r1);

        int r2 = test.read();
        assertEquals("single byte read", 70, r2);

        int r3 = test.read();
        assertEquals("single byte read, eof", -1, r3);
    }


    @SuppressWarnings("resource")
    public void testMarkAndReset() throws Exception
    {
        byte[] data = new byte[] { 64, 65, 66, 67, 68, 69, 70 };
        ByteArrayInputStream base = new ByteArrayInputStream(data);
        CloseBlockingInputStream test = new CloseBlockingInputStream(base);

        assertTrue(test.markSupported());

        // mark after start of stream, then read entire stream
        test.read(new byte[3]);
        test.mark(1024);
        test.read(new byte[10]);
        test.reset();

        assertEquals("mark/reset worked", 67, test.read());
    }


    @SuppressWarnings("resource")
    public void testSkip() throws Exception
    {
        byte[] data = new byte[] { 64, 65, 66, 67, 68, 69, 70 };
        ByteArrayInputStream base = new ByteArrayInputStream(data);
        CloseBlockingInputStream test = new CloseBlockingInputStream(base);

        // ByteArrayInputStream never skips less than the request amount (unless you're at EOF)
        assertEquals("bytes skipped", 3, test.skip(3));
        assertEquals("4th byte", 67, test.read());
    }


    @SuppressWarnings("resource")
    public void testAvailable() throws Exception
    {
        byte[] data = new byte[] { 64, 65, 66, 67, 68, 69, 70 };
        ByteArrayInputStream base = new ByteArrayInputStream(data);
        CloseBlockingInputStream test = new CloseBlockingInputStream(base);

        // ByteArrayInputStream returns remaining bytes from available()
        assertEquals("byte available", base.available(), test.available());
    }


    public void testClose() throws Exception
    {
        MyMockInputStream base = new MyMockInputStream();
        CloseBlockingInputStream test = new CloseBlockingInputStream(base);

        // mock will throw if this gets through
        test.close();
    }
}
