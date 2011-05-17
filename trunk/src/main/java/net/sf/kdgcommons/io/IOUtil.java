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

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


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
     *  Opens a file. The returned stream will be buffered, and if the file's name
     *  ends in ".gz" will be wrapped with a <codeGZIPInputStream></code>. If any
     *  step fails, the underlying file will be closed (preventing leakage of file
     *  descriptors).
     */
    public static InputStream openFile(File file)
    throws IOException
    {
        InputStream stream = null;
        try
        {
            stream = new FileInputStream(file);
            stream = new BufferedInputStream(stream);
            if (file.getName().endsWith(".gz"))
                stream = new GZIPInputStream(stream);
            return stream;
        }
        catch (IOException ex)
        {
            closeQuietly(stream);
            throw ex;
        }
    }


    /**
     *  Opens a file. The returned stream will be buffered, and if the file's name
     *  ends in ".gz" will be wrapped with a <code></code>. If any step fails, the
     *  underlying file will be closed (preventing leakage of file descriptors).
     */
    public static InputStream openFile(String fileName)
    throws IOException
    {
        return openFile(new File(fileName));
    }
}
