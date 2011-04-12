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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;


/**
 *  Static utility methods for working with classes in the <code>java.io</code>
 *  and <code>java.nio</code> packages.
 */
public class IOUtil
{
    /**
     *  Closes any <code>Closeable</code> object, swallowing any exception
     *  that it might throw. This is used in a <code>finally</code> block,
     *  where such exceptions would supercede any thrown from code in the
     *  <code>try</code> block.
     *
     *  @param  closable    The object to close. May be null, in which
     *                      case this method does nothing.
     */
    public static void closeQuietly(Closeable closable)
    {
        if (closable == null)
            return;

        try
        {
            closable.close();
        }
        catch (Exception ex)
        {
            // ignore it
        }
    }


    /**
     *  Memory maps a segment of a file.
     *
     *  @param  file    The file to be mapped.
     *  @param  offset  The starting location of the mapping within the file.
     *  @param  length  The number of bytes to be mapped. This is limited to
     *                  <code>Integer.MAX_VALUE</code>, but is a <code>long</code>
     *                  so that you can pass <code>File.length()</code>.
     *  @param  mode    The mapping mode. The underlying channel will be opened
     *                  using the same mode (so that you can map a read-only file).
     */
    public static MappedByteBuffer map(File file, long offset, long length, MapMode mode)
    throws IOException
    {
        String rafMode = mode.equals(MapMode.READ_ONLY) ? "r" : "rw";
        RandomAccessFile raf = new RandomAccessFile(file, rafMode);
        try
        {
            return raf.getChannel().map(mode, offset, length);
        }
        finally
        {
            closeQuietly(raf);
        }
    }
}
