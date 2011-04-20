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
import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.TestCase;


public class TestBufferFacade extends TestCase
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
//  Test Cases
//----------------------------------------------------------------------------

    public void testByteBufferBasicOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        BufferFacade facade = new BufferFacade(buf);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(50));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(63, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(60));
        assertEquals(0x00, buf.get(61));
        assertEquals(0x5A, buf.get(62));
        assertEquals(0x01, buf.get(63));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(4096, facade.capacity());
    }


    public void testByteBufferOffsetOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        BufferFacade facade = new BufferFacade(buf, 1000);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(1050));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1063, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(1060));
        assertEquals(0x00, buf.get(1061));
        assertEquals(0x5A, buf.get(1062));
        assertEquals(0x01, buf.get(1063));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(3096, facade.capacity());
    }

    public void testByteBufferTLBasicOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        ByteBufferThreadLocal tl = new ByteBufferThreadLocal(buf);
        BufferFacade facade = new BufferFacade(tl);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(50));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(63, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(60));
        assertEquals(0x00, buf.get(61));
        assertEquals(0x5A, buf.get(62));
        assertEquals(0x01, buf.get(63));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(4096, facade.capacity());
    }


    public void testByteBufferTLOffsetOps() throws Exception
    {
        ByteBuffer buf = ByteBuffer.allocate(4096);
        ByteBufferThreadLocal tl = new ByteBufferThreadLocal(buf);
        BufferFacade facade = new BufferFacade(tl, 1000);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(1050));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1063, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(1060));
        assertEquals(0x00, buf.get(1061));
        assertEquals(0x5A, buf.get(1062));
        assertEquals(0x01, buf.get(1063));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(3096, facade.capacity());
    }


    public void testMappedFileBufferBasicOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferBasicOps", 4096);
        BufferFacade facade = new BufferFacade(buf);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(50));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(63, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(60));
        assertEquals(0x00, buf.get(61));
        assertEquals(0x5A, buf.get(62));
        assertEquals(0x01, buf.get(63));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(4096, facade.capacity());
    }


    public void testMappedFileBufferOffsetOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferOffsetOps", 4096);
        BufferFacade facade = new BufferFacade(buf, 1000);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(1050));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1063, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(1060));
        assertEquals(0x00, buf.get(1061));
        assertEquals(0x5A, buf.get(1062));
        assertEquals(0x01, buf.get(1063));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(3096, facade.capacity());
    }


    public void testMappedFileBufferTLBasicOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferBasicOps", 4096);
        MappedFileBufferThreadLocal tl = new MappedFileBufferThreadLocal(buf);
        BufferFacade facade = new BufferFacade(tl);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(50));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(63, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(60));
        assertEquals(0x00, buf.get(61));
        assertEquals(0x5A, buf.get(62));
        assertEquals(0x01, buf.get(63));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(4096, facade.capacity());
    }


    public void testMappedFileBufferTLOffsetOps() throws Exception
    {
        MappedFileBuffer buf = createMappedFile("testMappedFileBufferOffsetOps", 4096);
        MappedFileBufferThreadLocal tl = new MappedFileBufferThreadLocal(buf);
        BufferFacade facade = new BufferFacade(tl, 1000);

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

        facade.putChar(50, 'A');
        assertEquals('A', buf.getChar(1050));
        assertEquals('A', facade.getChar(50));

        byte[] bb = new byte[] { (byte)0x5A, (byte)0x00, (byte)0x5A };
        buf.put(1063, (byte)0x01);    // sentinel
        facade.putBytes(60, bb);
        assertEquals(0x5A, buf.get(1060));
        assertEquals(0x00, buf.get(1061));
        assertEquals(0x5A, buf.get(1062));
        assertEquals(0x01, buf.get(1063));
        assertTrue(Arrays.equals(bb, facade.getBytes(60, 3)));

        buf.putInt(1100, 0x12345678);
        ByteBuffer b2 = facade.slice(100);
        assertEquals(0x12345678, b2.getInt(0));

        assertEquals(3096, facade.capacity());
    }
}
