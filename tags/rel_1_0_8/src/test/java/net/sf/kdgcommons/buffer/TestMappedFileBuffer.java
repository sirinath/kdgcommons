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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.util.Random;

import junit.framework.TestCase;

import net.sf.kdgcommons.io.IOUtil;


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

    /**
     *  Writes a "walking byte" pattern into the test file. This will cause
     *  problems with any retrievals that don't look at the correct location.
     */
    private void writeDefaultContent(int length)
    throws Exception
    {
        FileOutputStream fos = new FileOutputStream(_testFile);
        try
        {
            BufferedOutputStream out = new BufferedOutputStream(fos);
            for (int ii = 0 ; ii < length ; ii++)
                out.write(ii % 256);
            out.flush();
            out.close();
        }
        catch (Exception ex)
        {
            IOUtil.closeQuietly(fos);
            throw ex;
        }
    }


    private void writeExplicitContent(long offset, int... bytes)
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
            IOUtil.closeQuietly(out);
        }
    }


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testSmallFileSingleSegmentReadWrite() throws Exception
    {
        writeDefaultContent(256);
        assertEquals(256L, _testFile.length());     // test the test!

        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);
        assertEquals(256L, buf.capacity());

        // check for default byte pattern
        assertEquals(0x01020304, buf.getInt(1));

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
    }


    // this test can't verify that we're using multiple segments: we'd
    // need to override and mock out the buffer selection code to do that
    public void testMediumFileMultipleSegments() throws Exception
    {
        writeExplicitContent(8192, 0x00);
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
        // we want zeros in the buffer so that we can verify our offsets
        writeExplicitContent(8191, 0x00);
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
        writeDefaultContent(8192);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        buf.setByteOrder(ByteOrder.BIG_ENDIAN);
        assertEquals(ByteOrder.BIG_ENDIAN, buf.getByteOrder());
        buf.putInt(0, 0x12345678);

        buf.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        assertEquals(ByteOrder.LITTLE_ENDIAN, buf.getByteOrder());
        assertEquals(0x78563412, buf.getInt(0));
    }


    public void testFailWriteToReadOnlyBuffer() throws Exception
    {
        writeDefaultContent(8192);
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


    public void testClone() throws Exception
    {
        writeDefaultContent(8192);
        MappedFileBuffer buf1 = new MappedFileBuffer(_testFile, 1024, true);
        MappedFileBuffer buf2 = buf1.clone();

        // this doesn't really test the documented behavior of clone()
        buf1.putInt(3172, 0x12345678);
        assertEquals(0x12345678, buf2.getInt(3172));

        // this is a little better, but it could use different mappings
        // ... and the test is dependent on the implementation
        assertNotSame(buf1.buffer(1234), buf2.buffer(1234));
    }


    public void testGetFile() throws Exception
    {
        writeDefaultContent(8192);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        assertSame(_testFile, buf.file());
    }


    public void testIsWritable() throws Exception
    {
        writeDefaultContent(8192);
        MappedFileBuffer buf1 = new MappedFileBuffer(_testFile, 1024, true);
        MappedFileBuffer buf2 = new MappedFileBuffer(_testFile, 1024, false);

        assertTrue(buf1.isWritable());
        assertFalse(buf2.isWritable());
    }


    public void testBulkOperations() throws Exception
    {
        writeDefaultContent(8192);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        // whitebox test: the variant of getBytes() that creates an array will
        // delegate to the variant that uses an existing array; no need to
        // write separate tests

        // getBytes() that doesn't span segments

        byte[] a1 = buf.getBytes(1, 256);

        assertEquals(256, a1.length);
        assertEquals(1, a1[0]);
        assertEquals(100, a1[99]);
        assertEquals(254, a1[253] & 0xFF);

        // and one that does

        byte[] a2 = buf.getBytes(1, 4099);
        assertEquals(4099, a2.length);

        // these assertions verify that we got everything
        assertEquals(1, a2[0]);
        assertEquals(100, a2[99]);
        assertEquals(254, a2[253] & 0xFF);
        assertEquals(1, a2[4096]);

        // these assertions check the segment boundary
        assertEquals(0, a2[1023]);
        assertEquals(1, a2[1024]);
        assertEquals(2, a2[1025]);
        assertEquals(0, a2[2047]);
        assertEquals(1, a2[2048]);
        assertEquals(2, a2[2049]);

        // putBytes() that doesn't span segments

        byte[] a3 = new byte[256];
        (new Random()).nextBytes(a3);
        buf.putBytes(256, a3);

        // nothing should be changed before or after
        assertEquals(0xFF, buf.get(255) & 0xFF);
        assertEquals(0x00, buf.get(512) & 0xFF);

        // and the random bytes should be in place
        for (int ii = 0 ; ii < a3.length ; ii++)
            assertEquals("byte " + (256 + ii), a3[ii], buf.get(256 + ii));

        // and finally, a segment-spanning putBytes()

        byte[] a4 = new byte[4096];
        (new Random()).nextBytes(a4);
        buf.putBytes(256, a4);

        // nothing should be changed before or after
        assertEquals(0xFF, buf.get(255) & 0xFF);
        assertEquals(0x00, buf.get(4352) & 0xFF);

        // and the random bytes should be in place
        for (int ii = 0 ; ii < a4.length ; ii++)
            assertEquals("byte " + (256 + ii), a4[ii], buf.get(256 + ii));
    }


    public void testBulkOperationFailureAtEndOfFile() throws Exception
    {
        writeDefaultContent(8192);
        MappedFileBuffer buf = new MappedFileBuffer(_testFile, 1024, true);

        try
        {
            buf.getBytes(8000, 256);
            fail("able to retrieve past end of file");
        }
        catch (IndexOutOfBoundsException ex)
        {
            // success
        }

        try
        {
            buf.putBytes(8000, new byte[256]);
            fail("able to write past end of file");
        }
        catch (IndexOutOfBoundsException ex)
        {
            // success
        }
    }
}
