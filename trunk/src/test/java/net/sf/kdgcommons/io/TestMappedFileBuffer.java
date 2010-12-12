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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

import junit.framework.TestCase;


public class TestMappedFileBuffer
extends TestCase
{
    private File _testFile;


    @Override
    protected void setUp()
    throws IOException
    {

        _testFile = File.createTempFile("TestMappedFileBuffer", ".tmp");
        _testFile.deleteOnExit();
    }



    @Override
    protected void tearDown()
    throws IOException
    {
        // yes, we've set delete-on-exit, but let's be explicit
        _testFile.delete();
    }


//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    private void writeContent(long offset, int... bytes)
    throws Exception
    {
        RandomAccessFile out = new RandomAccessFile(_testFile, "rwd");
        try
        {
            out.seek(offset);
            for (int b : bytes)
                out.write(b);
        }
        finally
        {
            // exception here will mask anything thrown from the try block
            if (out != null)
                out.close();
        }
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testSmallFileSingleSegmentReadWrite() throws Exception
    {
        writeContent(255L, 0x00);
        assertEquals(256L, _testFile.length());     // test the test!

        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);
        assertEquals(256L, buf.capacity());

        buf.put(0L, (byte)0x12);
        assertEquals(0x12, buf.get(0L));

        buf.putShort(16L, (short)0x1234);
        assertEquals(0x1234, buf.getShort(16L));

        buf.putInt(32L, 0x12345678);
        assertEquals(0x12345678, buf.getInt(32L));

        buf.putLong(48L, 0x8765432112345678L);
        assertEquals(0x8765432112345678L, buf.getLong(48L));

        buf.putFloat(64L, 1234.5f);
        assertEquals(1234.5f, buf.getFloat(64L), .01f);

        buf.putDouble(96L, 1234567890.125);
        assertEquals(1234567890.125, buf.getDouble(96L), .0001f);

        buf.putDouble(96L, 1234567890.125);
        assertEquals(1234567890.125, buf.getDouble(96L), .0001f);

        buf.putChar(112L, '\u0123');
        assertEquals('\u0123', buf.getChar(112L));

        byte[] b1 = buf.getBytes(48L, 4);
        assertEquals(4, b1.length);
        assertEquals((byte)0x87, b1[0]);      // full check after put

        buf.putBytes(128L, b1);
        assertEquals(0x87654321, buf.getInt(128L));
    }


    // this test can't verify that we're using multiple segments: we'd
    // need to override and mock out the buffer selection code to do that
    public void testMediumFileMultipleSegments() throws Exception
    {
        writeContent(8191, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        for (int ii = 0 ; ii < 8 ; ii++)
        {
            // we can, however, test that we're not getting the same buffer
            // and same offset within that buffer
            long offset = ii * 1024 + 257;
            assertEquals(0, buf.getInt(offset));
            buf.putInt(offset, 0x12345678);
            assertEquals(0x12345678, buf.getInt(offset));
        }
    }


    // this test requires a 64-bit machine, so is commented out
    // it truly tests whether we're properly managing segments
//    public void testLargeFileMultipleSegments() throws Exception
//    {
//        final int oneGig = 1000000000;
//
//        // hope that you're running on a system that support sparse
//        // files, otherwise this is going to take a long time
//        writeContent(5L * oneGig, 0x00);
//        MappedFileBuffer buf = new MappedFileBuffer(_testFile, oneGig, true);
//
//        long offset = 2123456789L;
//        assertEquals(0, buf.getInt(offset));
//        buf.putInt(offset, 0x12345678);
//        assertEquals(0x12345678, buf.getInt(offset));
//    }


    public void testSlice() throws Exception
    {
        writeContent(8191, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        // whitebox test: an offset just below a segment boundary, which will
        // ensure that we have properly overlapping segments
        ByteBuffer slice = buf.slice(4094L);

        // first test is for the correct offset
        buf.putInt(4096L, 0x12345678);
        assertEquals(0x00001234, slice.getInt(0));

        // second test is for correct size
        slice.put(1023, (byte)0x01);
        assertEquals(0x01, slice.get(1023));

        // whitebox test: we know that the actual segment is twice the requested
        // segment, so will try indexing off the end
        slice.put(1025, (byte)0x01);
        assertEquals(0x01, slice.get(1025));

        try
        {
            slice.get(1026);
            fail("able to access outside file bounds");
        }
        catch (IndexOutOfBoundsException ee)
        {
            // success
        }
    }


    public void testSetOrder() throws Exception
    {
        writeContent(255, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        buf.setByteOrder(ByteOrder.BIG_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, buf.getByteOrder());
        buf.putInt(0, 0x12345678);

        buf.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.LITTLE_ENDIAN, buf.getByteOrder());
        assertEquals(0x78563412, buf.getInt(0));
    }


    public void testCanCloseMultipleTimes() throws Exception
    {
        writeContent(255, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);
        buf.close();
        buf.close();
    }


    public void testFailWriteToReadOnlyBuffer() throws Exception
    {
        writeContent(8191, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, false);

        buf.getInt(8000L);  // verifies that the file was created correctly

        try
        {
            buf.putInt(8000L, 0x12345678);
            fail("able to write to read-only buffer");
        }
        catch (ReadOnlyBufferException ee)
        {
            // success
        }
    }


    public void testFailAccessAfterClose() throws Exception
    {
        writeContent(8191, 0x00);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, false);

        buf.getInt(8000L);  // verifies that the file was created correctly

        buf.close();
        try
        {
            buf.getInt(8000L);
            fail("able to access buffer after closed");
        }
        catch (IllegalStateException ee)
        {
            // success
        }
    }
}
