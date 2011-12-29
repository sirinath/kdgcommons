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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.TestCase;


public class TestBufferFacadeFactory
extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    private static MappedFileBuffer createMappedFile(String name, int size)
    throws IOException
    {
        File mappedFile = File.createTempFile(name, ".tmp");
        mappedFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(mappedFile);
        fos.write(new byte[size]);
        fos.close();

        return new MappedFileBuffer(mappedFile, true);
    }


//----------------------------------------------------------------------------
//  Test Cases -- all tests compare access via the buffer to access via the
//                facade ... which means a lot of duplicated code, and no way
//                to refactor
//----------------------------------------------------------------------------

    public void testByteBufferBasicOps() throws Exception
    {
        // newly created buffers have limit == capacity, so we must set explicitly
        ByteBuffer buf = ByteBuffer.allocate(4096);
        buf.limit(2048);

        BufferFacade facade = BufferFacadeFactory.create(buf);

        assertEquals(4096, facade.capacity());
        assertEquals(2048, facade.limit());

        // all writes should leave a gap, to catch any use of relative positioning
        // for sub-int values, leave high byte clear to prevent sign-extension

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(10));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(20));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(30));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(40));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(50), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(60), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(70));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(83, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(80));
        assertEquals(0x00, buf.get(81));
        assertEquals(0x5A, buf.get(82));
        assertEquals(0x01, buf.get(83));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testByteBufferOffsetOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        buf.limit(2048);

        BufferFacade facade = BufferFacadeFactory.create(buf, 1000);

        assertEquals(3096, facade.capacity());
        assertEquals(1048, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(1010));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(1020));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(1030));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(1040));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(1050), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(1060), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(1070));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1083, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(1080));
        assertEquals(0x00, buf.get(1081));
        assertEquals(0x5A, buf.get(1082));
        assertEquals(0x01, buf.get(1083));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }

    public void testByteBufferTLBasicOps() throws Exception
    {
        // threadsafe facades REQUIRE us to set limit before the first access
        ByteBuffer buf = ByteBuffer.allocate(4096);
        buf.limit(2048);

        BufferFacade facade = BufferFacadeFactory.create(buf);

        assertEquals(4096, facade.capacity());
        assertEquals(2048, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(10));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(20));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(30));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(40));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(50), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(60), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(70));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(83, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(80));
        assertEquals(0x00, buf.get(81));
        assertEquals(0x5A, buf.get(82));
        assertEquals(0x01, buf.get(83));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testByteBufferTLOffsetOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        buf.limit(2048);

        BufferFacade facade = BufferFacadeFactory.createThreadsafe(buf, 1000);

        assertEquals(3096, facade.capacity());
        assertEquals(1048, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(1010));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(1020));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(1030));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(1040));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(1050), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(1060), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(1070));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1083, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(1080));
        assertEquals(0x00, buf.get(1081));
        assertEquals(0x5A, buf.get(1082));
        assertEquals(0x01, buf.get(1083));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testMappedFileBufferBasicOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferBasicOps", 4096);
        BufferFacade facade = BufferFacadeFactory.create(buf);

        assertEquals(4096, facade.capacity());
        assertEquals(4096, facade.limit());

        // all writes should leave a gap, to catch any use of relative positioning
        // for sub-int values, leave high byte clear to prevent sign-extension

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(10));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(20));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(30));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(40));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(50), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(60), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(70));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(83, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(80));
        assertEquals(0x00, buf.get(81));
        assertEquals(0x5A, buf.get(82));
        assertEquals(0x01, buf.get(83));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testMappedFileBufferOffsetOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferOffsetOps", 4096);
        BufferFacade facade = BufferFacadeFactory.create(buf, 1000);

        assertEquals(3096, facade.capacity());
        assertEquals(3096, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(1010));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(1020));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(1030));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(1040));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(1050), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(1060), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(1070));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1083, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(1080));
        assertEquals(0x00, buf.get(1081));
        assertEquals(0x5A, buf.get(1082));
        assertEquals(0x01, buf.get(1083));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testMappedFileBufferTLBasicOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferBasicOps", 4096);
        BufferFacade facade = BufferFacadeFactory.createThreadsafe(buf);

        assertEquals(4096, facade.capacity());
        assertEquals(4096, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(10));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(20));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(30));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(40));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(50), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(60), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(70));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(83, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(80));
        assertEquals(0x00, buf.get(81));
        assertEquals(0x5A, buf.get(82));
        assertEquals(0x01, buf.get(83));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }


    public void testMappedFileBufferTLOffsetOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferOffsetOps", 4096);
        BufferFacade facade = BufferFacadeFactory.createThreadsafe(buf, 1000);

        assertEquals(3096, facade.capacity());
        assertEquals(3096, facade.limit());

        facade.put(10, (byte)0x5A);
        assertEquals(0x5A, buf.get(1010));
        assertEquals(0x5A, facade.get(10));

        facade.putShort(20, (short)0x5AA5);
        assertEquals(0x5AA5, buf.getShort(1020));
        assertEquals(0x5AA5, facade.getShort(20));

        facade.putInt(30, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, buf.getInt(1030));
        assertEquals(0xA5A55A5A, facade.getInt(30));

        facade.putLong(40, 0x1234567890ABCDEFL);
        assertEquals(0x1234567890ABCDEFL, buf.getLong(1040));
        assertEquals(0x1234567890ABCDEFL, facade.getLong(40));

        facade.putFloat(50, 123456.5f);
        assertEquals(123456.5f, buf.getFloat(1050), .01);
        assertEquals(123456.5f, facade.getFloat(50), .01);

        facade.putDouble(60, 12345678901234.5);
        assertEquals(12345678901234.5, buf.getDouble(1060), .01);
        assertEquals(12345678901234.5, facade.getDouble(60), .01);

        facade.putChar(70, 'A');
        assertEquals('A', buf.getChar(1070));
        assertEquals('A', facade.getChar(70));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1083, (byte)0x01);    // sentinel
        facade.putBytes(80, bb);
        assertEquals(0x5A, buf.get(1080));
        assertEquals(0x00, buf.get(1081));
        assertEquals(0x5A, buf.get(1082));
        assertEquals(0x01, buf.get(1083));
        assertTrue(Arrays.equals(bb, facade.getBytes(80, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));
    }
}
