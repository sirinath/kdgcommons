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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.sf.kdgcommons.util.HexDump;


/**
 *  An <code>OutputStream</code> that logs all writes to a specified <code>
 *  Writer</code>, in a hexdump format. Each write operation creates one or
 *  more lines of output, formatted as below:
 *  <ul>
 *  <li> 8 hex digits showing offset, followed by 2 spaces
 *  <li> up to 16 two-character hex values, separated by spaces
 *  <li> padding that brings the hex characters to 48 columns,
 *       followed by 4 spaces, followed by up to 16 characters, with
 *       non-printing characters replaced by a specified character
 *  </ul>
 */
public class HexDumpOutputStream
extends OutputStream
{
    private HexDump _dumper;
    private PrintWriter _out;


    /**
     *  The base constructor.
     *
     *  @param out          All output goes here. Typically, you will want to set
     *                      auto-flush on.
     *  @param replacement  The character used to replace non-printing and non-
     *                      ASCII characters.
     */
    public HexDumpOutputStream(PrintWriter out, char replacement)
    {
        _dumper = new HexDump(16, true, 8, 2, true, 4, true, replacement);
        _out = out;
    }


    /**
     *  Convenience constructor, which shows offset and character output,
     *  with non-printing and non-ASCII characters replaced by spaces.
     */
    public HexDumpOutputStream(PrintWriter out)
    {
        this(out, ' ');
    }

//----------------------------------------------------------------------------
//  Implementation of OutputStream
//----------------------------------------------------------------------------

    /**
     *  Closes the underlying Writer.
     */
    @Override
    public void close() throws IOException
    {
        _out.close();
    }


    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        _dumper.write(_out, b, off, len);
    }


    @Override
    public void write(byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }


    @Override
    public void write(int b) throws IOException
    {
        byte[] buf = new byte[] {(byte)b};
        write(buf);
    }
}
