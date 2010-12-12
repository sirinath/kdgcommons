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

package net.sf.kdgcommons.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import junit.framework.TestCase;


public class TestHexDump extends TestCase
{
//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testEmptyIterator() throws Exception
    {
        HexDump dumper = new HexDump();
        byte[] data = new byte[0];

        Iterator<String> itx = dumper.iterator(data);
        assertFalse(itx.hasNext());
    }


    public void testEmptyString() throws Exception
    {
        HexDump dumper = new HexDump();
        byte[] data = new byte[0];

        assertEquals("", dumper.stringValue(data));
    }


    public void testEmptyWriter() throws Exception
    {
        HexDump dumper = new HexDump();
        byte[] data = new byte[0];

        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        dumper.write(out, data);
        assertEquals("", sw.toString());
    }


    public void testBytesOnlySingleByteIterator() throws Exception
    {
        HexDump dumper = new HexDump(8, false, 0, 0, false, 0, false, '\0');
        byte[] data = new byte[] { 0x41 };

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 ", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testBytesOnlySingleByteString() throws Exception
    {
        HexDump dumper = new HexDump(8, false, 0, 0, false, 0, false, '\0');
        byte[] data = new byte[] { 0x41 };

        assertEquals("41 ", dumper.stringValue(data));
    }


    public void testBytesOnlySingleByteWriter() throws Exception
    {
        HexDump dumper = new HexDump(8, false, 0, 0, false, 0, false, '\0');
        byte[] data = new byte[] { 0x41 };

        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        dumper.write(out, data);
        assertEquals("41 \n", sw.toString());
    }


    public void testBytesOnlyMultiByteIterator() throws Exception
    {
        HexDump dumper = new HexDump(8, false, 0, 0, false, 0, false, '\0');
        byte[] data = new byte[] { 0x41, 0x42, 0x43 };

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 42 43 ", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testBytesOnlyMultiLineIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, false, 0, false, '\0');
        byte[] data = "ABCDEFGHIJK".getBytes("UTF-8");

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 42 43 44 ", itx.next());
        assertEquals("45 46 47 48 ", itx.next());
        assertEquals("49 4A 4B ", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testBytesOnlyMultiLineString() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, false, 0, false, '\0');
        byte[] data = "ABCDEFGHIJK".getBytes("UTF-8");

        assertEquals("41 42 43 44 \n45 46 47 48 \n49 4A 4B ",
                     dumper.stringValue(data));
    }


    public void testBytesOnlyMultiLineWriter() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, false, 0, false, '\0');
        byte[] data = "ABCDEFGHIJK".getBytes("UTF-8");

        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        dumper.write(out, data);
        assertEquals("41 42 43 44 \n45 46 47 48 \n49 4A 4B \n",
                     sw.toString());
    }


    public void testBytesAndCharsSingleLineIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, true, 4, false, '\0');
        byte[] data = "ABC".getBytes("UTF-8");

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 42 43        ABC", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testBytesAndCharsMultiLineIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, true, 4, false, '\0');
        byte[] data = "ABCDEF".getBytes("UTF-8");

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 42 43 44     ABCD", itx.next());
        assertEquals("45 46           EF", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testCharReplacementSingleLineIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, false, 0, 0, true, 4, true, '.');
        byte[] data = new byte[] { (byte)0x41, (byte)0x91, (byte)0x92 };

        Iterator<String> itx = dumper.iterator(data);
        assertEquals("41 91 92        A..", itx.next());
        assertFalse(itx.hasNext());
    }


    public void testOffsetAndBytesSingleDumpIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, true, 4, 2, false, 0, false, '\0');

        Iterator<String> itx1 = dumper.iterator(new byte[] { 0x41 });
        assertEquals("0000  41 ", itx1.next());
        assertFalse(itx1.hasNext());
    }


    public void testOffsetAndBytesMultiDumpIterator() throws Exception
    {
        HexDump dumper = new HexDump(4, true, 4, 2, false, 0, false, '\0');

        Iterator<String> itx1 = dumper.iterator(new byte[] { 0x41 });
        assertEquals("0000  41 ", itx1.next());
        assertFalse(itx1.hasNext());
        assertFalse(itx1.hasNext());

        Iterator<String> itx2 = dumper.iterator(new byte[] { 0x42, 0x43 });
        assertEquals("0001  42 43 ", itx2.next());
        assertFalse(itx2.hasNext());

        Iterator<String> itx3 = dumper.iterator("123456".getBytes("UTF-8"));
        assertEquals("0003  31 32 33 34 ", itx3.next());
        assertEquals("0007  35 36 ", itx3.next());
        assertFalse(itx3.hasNext());
    }
}
