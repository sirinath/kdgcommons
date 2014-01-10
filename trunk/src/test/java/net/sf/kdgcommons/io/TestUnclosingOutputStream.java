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

import junit.framework.TestCase;

import net.sf.kdgcommons.test.ArrayAsserts;

public class TestUnclosingOutputStream extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    /**
     *  A stream for testing the flush and close operations.
     */
    private static class MyMockOutputStream
    extends OutputStream
    {
        public boolean wasFlushed;

        @Override
        public void write(int b) throws IOException
        {
            fail("write() should not be called by this test");
        }

        @Override
        public void flush() throws IOException
        {
            wasFlushed = true;
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
    public void testWriting() throws Exception
    {
        ByteArrayOutputStream base = new ByteArrayOutputStream();
        UnclosingOutputStream test = new UnclosingOutputStream(base);

        test.write(64);
        test.write(new byte[] { 65, 66, 67 });
        test.write(new byte[] { 68, 69, 70 }, 1, 1);

        byte[] written = base.toByteArray();
        ArrayAsserts.assertEquals(new byte[] { 64,  65, 66, 67, 69 }, written);
    }


    @SuppressWarnings("resource")
    public void testFlush() throws Exception
    {
        MyMockOutputStream base = new MyMockOutputStream();
        UnclosingOutputStream test = new UnclosingOutputStream(base);

        assertFalse("mock already flushed", base.wasFlushed);
        test.flush();
        assertTrue("mock was not flushed", base.wasFlushed);
    }


    public void testClose() throws Exception
    {
        MyMockOutputStream base = new MyMockOutputStream();
        UnclosingOutputStream test = new UnclosingOutputStream(base);

        // mock will throw if this gets through
        test.close();
    }


}
