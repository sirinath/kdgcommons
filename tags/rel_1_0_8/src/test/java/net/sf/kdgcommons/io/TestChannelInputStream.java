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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import junit.framework.TestCase;


public class TestChannelInputStream extends TestCase
{
    private final static int DEFAULT_FILESIZE = 8192;

    private File _file;
    private RandomAccessFile _raf;
    private FileChannel _channel;


    @Override
    protected void setUp() throws Exception
    {
        _file = IOUtil.createTempFile("TestChannelInputStream", DEFAULT_FILESIZE);

        _raf = new RandomAccessFile(_file, "rw");
        _raf.seek(0);
        _raf.write(createWalkingBytes(DEFAULT_FILESIZE));

        _raf.seek(0);
        _channel = _raf.getChannel();
    }


    @Override
    protected void tearDown() throws Exception
    {
        IOUtil.closeQuietly(_channel);
        IOUtil.closeQuietly(_raf);
        _file.delete();
    }


//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    private static byte[] createWalkingBytes(int size)
    {
        byte[] bytes = new byte[size];
        for (int ii = 0 ; ii < bytes.length ; ii++)
            bytes[ii] = (byte)(ii % 256);
        return bytes;
    }


    private static void assertWalkingBytes(byte[] bytes, int off, int len)
    {
        for (int ii = 0 ; ii < len ; ii++)
        {
            int idx = off + ii;
            int val = idx % 256;
            assertEquals("byte " + off + " (" + idx + ")", val, bytes[ii] & 0xFF);
        }
    }


    // this takes ints rather than bytes so that we don't need to cast
    private void appendToFile(int... bytes)
    throws IOException
    {
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(_file, true);
            for (int bb : bytes)
                out.write(bb);
            out.flush();
        }
        finally
        {
            IOUtil.closeQuietly(out);
        }
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testSingleByteRead() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);

        assertEquals(0, in.read());
        assertEquals(1, in.read());
        assertEquals(2, in.read());
    }


    // this test also verifies that bytes aren't sign-extended
    public void testSingleByteReadAfterChannelPositioned() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(127);

        assertEquals(127, in.read());
        assertEquals(128, in.read());
        assertEquals(129, in.read());
    }


    public void testSingleByteReadAtEOF() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(_file.length() - 2);

        assertEquals(254, in.read());
        assertEquals(255, in.read());
        assertEquals(-1, in.read());
    }


    public void testSingleByteReadSoftEOF() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(_file.length() - 2);

        assertEquals(254, in.read());
        assertEquals(255, in.read());
        assertEquals(-1, in.read());

        appendToFile(0x10, 0x20, 0x30, 0x40);

        assertEquals(0x10, in.read());
        assertEquals(0x20, in.read());
        assertEquals(0x30, in.read());
        assertEquals(0x40, in.read());
        assertEquals(-1, in.read());
    }


    public void testMultiByteRead() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);

        byte[] bytes = new byte[256];
        int bytesRead = in.read(bytes);

        // while the return value isn't guaranteed, there's no reason it should fail
        assertEquals("read size", bytes.length, bytesRead);

        assertEquals(0, bytes[0]);
        assertWalkingBytes(bytes, 0, bytesRead);
    }


    public void testMultiByteReadAfterPosition() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(96);

        byte[] bytes = new byte[256];
        int bytesRead = in.read(bytes);

        assertEquals("read size", bytes.length, bytesRead);

        assertEquals(96, bytes[0]);
        assertWalkingBytes(bytes, 96, bytesRead);
    }


    public void testMultiByteOffsetRead() throws Exception
    {
        // we'll position the channel so that we don't get any 0 bytes

        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(100);

        byte[] bytes = new byte[1024];
        int bytesRead = in.read(bytes, 100, 100);

        // again, no reason for this to fail
        assertEquals("read size", 100, bytesRead);

        // verify area outside read was untouched
        for (int ii = 0 ; ii < 100 ; ii++)
            assertEquals("byte updated outside boundary (" + ii + ")", 0, bytes[ii] & 0xFF);
        for (int ii = 200 ; ii < bytes.length ; ii++)
            assertEquals("byte updated outside boundary (" + ii + ")", 0, bytes[ii] & 0xFF);

        assertEquals(100, bytes[100] & 0xFF);
        assertEquals(199, bytes[199] & 0xFF);
    }


    public void testMultiByteReadAtEOF() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(DEFAULT_FILESIZE - 10);

        byte[] bytes = new byte[1024];

        assertEquals("read size", 10, in.read(bytes));
        assertEquals(-1, in.read(bytes));
    }


    public void testMultiByteReadSoftEOF() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        _channel.position(DEFAULT_FILESIZE - 10);

        byte[] bytes = new byte[1024];

        assertEquals("read size", 10, in.read(bytes));
        assertEquals(-1, in.read(bytes));

        appendToFile(0x10, 0x20, 0x30, 0x40);

        assertEquals(4, in.read(bytes));
        assertEquals(0x10, bytes[0]);
        assertEquals(0x20, bytes[1]);
        assertEquals(0x30, bytes[2]);
        assertEquals(0x40, bytes[3]);
        assertEquals(-1, in.read(bytes));
    }


    @SuppressWarnings("deprecation")
    public void testClose() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);
        assertTrue("channel not open at start of test", _channel.isOpen());

        in.close();
        assertFalse("channel is still open", _channel.isOpen());
    }


    @SuppressWarnings("deprecation")
    public void testDocumentedValuesForAvailableAndMarkSupported() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);

        assertEquals(0, in.available());
        assertFalse(in.markSupported());
    }


    @SuppressWarnings("deprecation")
    public void testSkip() throws Exception
    {
        ChannelInputStream in = new ChannelInputStream(_channel);

        // this is a whitebox test: we know that the default InputStream.skip()
        // will skip until EOF, so we'll make explicit assertions

        long bytesSkipped = in.skip(100);
        assertEquals(100, bytesSkipped);
        assertEquals(100, in.read());
    }
}
