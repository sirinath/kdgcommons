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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel.MapMode;

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


    // setup is relatively expenseive for this test, so it will do many
    // different things ... although, unfortunately, we're not able to
    // validate that we (1) open the underlying RandomAccessFile read-only,
    // or (2) that we close it after creating the mapping
    public void testMapFile() throws Exception
    {
        // we'll compare the data from the mapped file with a local buffer
        byte[] testData = new byte[1024];
        for (int ii = 0 ; ii < testData.length ; ii++)
            testData[ii] = (byte)(ii % 256);
        ByteBuffer localBuf = ByteBuffer.wrap(testData);

        File file = File.createTempFile("TestIOUtil", ".tmp");
        file.deleteOnExit();
        FileOutputStream out = new FileOutputStream(file);
        out.write(testData);
        out.close();

        // test 1: verify that a whole-file mapping is equivalent to our test data

        ByteBuffer fileBuf1 = IOUtil.map(file, 0, file.length(), MapMode.READ_ONLY);
        assertEquals(localBuf.limit(), fileBuf1.limit());
        assertEquals(localBuf.capacity(), fileBuf1.capacity());
        for (int ii = 0 ; ii < testData.length ; ii++)
            assertEquals("byte " + ii, localBuf.get(ii), fileBuf1.get(ii));

        // test 2: verify that we can't write to the read-only mapping

        try
        {
            fileBuf1.putInt(0, 0xA5A55A5A);
            fail("able to write to read-only mapping");
        }
        catch (ReadOnlyBufferException ex)
        {
            // success
        }

        // test 3: offset mapping

        ByteBuffer fileBuf2 = IOUtil.map(file, 128, 16, MapMode.READ_WRITE);
        assertEquals(localBuf.getLong(128), fileBuf2.getLong(0));

        // test 4: a read-write mapping ... note assertion via read-only buffer

        ByteBuffer fileBuf3 = IOUtil.map(file, 0, file.length(), MapMode.READ_WRITE);
        fileBuf3.putInt(16, 0xA5A55A5A);
        assertEquals(0xA5A55A5A, fileBuf1.getInt(16));


    }

}
