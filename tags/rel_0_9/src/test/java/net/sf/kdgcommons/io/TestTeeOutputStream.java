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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;


public class TestTeeOutputStream extends TestCase
{
    private byte[] _testData;
    private ByteArrayOutputStream _base;
    private ByteArrayOutputStream _tee;
    private TeeOutputStream _test;


    @Override
    protected void setUp() throws Exception
    {
        _testData = "Hello, World".getBytes("UTF-8");
        _base = new ByteArrayOutputStream();
        _tee = new ByteArrayOutputStream();
        _test = new TeeOutputStream(_base, _tee);
    }


    public void testWriteSingleByte() throws Exception
    {
        _test.write(65);
        assertEquals(1, _base.toByteArray().length);
        assertEquals(1, _tee.toByteArray().length);
        assertEquals(65, _base.toByteArray()[0]);
        assertEquals(65, _tee.toByteArray()[0]);
    }


    public void testWriteFromBuffer() throws Exception
    {
        _test.write(_testData);
        byte[] baseData = _base.toByteArray();
        byte[] teeData = _tee.toByteArray();

        assertEquals(_testData.length, baseData.length);
        assertEquals(_testData.length, teeData.length);
        for (int ii = 0 ; ii < _testData.length ; ii++)
        {
            assertEquals("byte " + ii, _testData[ii], baseData[ii]);
            assertEquals("byte " + ii, _testData[ii], teeData[ii]);
        }
    }


    public void testWriteFromBufferWithOffsetAndLength() throws Exception
    {
        _test.write(_testData, 2, 6);
        byte[] baseData = _base.toByteArray();
        byte[] teeData = _tee.toByteArray();

        assertEquals(6, baseData.length);
        assertEquals(6, teeData.length);
        for (int ii = 0 ; ii < 6 ; ii++)
        {
            assertEquals("byte " + ii, _testData[ii + 2], baseData[ii]);
            assertEquals("byte " + ii, _testData[ii + 2], teeData[ii]);
        }
    }



    public void testFlush() throws Exception
    {
        _test = new TeeOutputStream(new BufferedOutputStream(_base, 1024),
                                    new BufferedOutputStream(_tee, 1024));

        _test.write(_testData);
        assertEquals(0, _base.toByteArray().length);
        assertEquals(0, _tee.toByteArray().length);

        _test.flush();
        assertEquals(_testData.length, _base.toByteArray().length);
        assertEquals(_testData.length, _tee.toByteArray().length);
    }


    public void testAutoFlush() throws Exception
    {
        _test = new TeeOutputStream(new BufferedOutputStream(_base, 1024),
                                    new BufferedOutputStream(_tee, 1024),
                                    true);

        _test.write(_testData);
        assertEquals(0, _base.toByteArray().length);
        assertEquals(_testData.length, _tee.toByteArray().length);

        _test.flush();
        assertEquals(_testData.length, _base.toByteArray().length);
        assertEquals(_testData.length, _tee.toByteArray().length);
    }

}
