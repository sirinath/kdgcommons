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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
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
     *  <p>
     *  This method also exists in Apache Commons IO. It exists here because
     *  older versions of Commons IO took explicit stream types, rather than
     *  <code>Closeable</code>.
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
     *  Copies the input stream to the output stream, leaving both open.
     *  Either parameter may be null, in which case this method does nothing.
     *  <p>
     *  This method also exists (with many variants) in Jakarta Commons IO.
     *  It's here so that this library can be self-contained.
     *
     *  @since 1.0.2
     *
     *  @return The number of bytes copied.
     */
    public static long copy(InputStream in, OutputStream out)
    throws IOException
    {
        if ((in == null) || (out == null))
            return 0;

        long total = 0;
        int read = 0;
        byte[] data = new byte[8192];   // common disk IO size
        while ((read = in.read(data)) >= 0)
        {
            out.write(data, 0, read);
            total += read;

        }
        return total;
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


    /**
     *  Creates a temporary file in the default temporary-file directory. Unlike the
     *  similarly-named method in <code>File</code>, this method adds a shutdown hook
     *  to delete the file, always uses the suffix ".tmp", and will create files of
     *  arbitrary size. The content of the file will be undefined.
     *
     *  @param  prefix  The prefix used to construct the file's name. Must be at least
     *                  three characters long (per <code>File</code> API).
     *  @param  size    The size of the file, in bytes.
     */
    public static File createTempFile(String prefix, long size)
    throws IOException
    {
        File file = File.createTempFile(prefix, null);
        file.deleteOnExit();

        // there's no need to do anything else for an empty file
        if (size == 0L)
            return file;

        RandomAccessFile raf = null;
        try
        {
            raf = new RandomAccessFile(file, "rw");
            raf.setLength(size);
        }
        finally
        {
            closeQuietly(raf);
        }
        return file;
    }


    /**
     *  Creates a temporary file via {@link #createTempFile}, then copies the
     *  contents of the passed stream to it. The input stream will be closed,
     *  whether or not the method completed normally (including cases where the
     *  file could not be created).
     *
     *  @since 1.0.2
     */
    public static File createTempFile(InputStream in, String prefix)
    throws IOException
    {
        File file = null;
        FileOutputStream fos = null;
        try
        {
            file = createTempFile(prefix, 0);
            fos = new FileOutputStream(file);
            copy(in, fos);
        }
        finally
        {
            closeQuietly(in);
            closeQuietly(fos);
        }
        return file;
    }


    /**
     *  Repeatedly reads the passed stream, until either the buffer is full or EOF
     *  is reached. Returns the number of bytes actually read.
     *  <p>
     *  The stream will not be closed by this method, even if EOF is reached.
     *
     *  @since 1.0.4
     */
    public static int readFully(InputStream in, byte[] dest)
    throws IOException
    {
        int off = 0;
        int len = dest.length;
        int cc = 0;
        while ((len > 0) && ((cc = in.read(dest, off, len)) >= 0))
        {
            off += cc;
            len -= cc;
        }
        return off;
    }


    /**
     *  Repeatedly calls <code>skip()/code> on the underlying stream, until either the
     *  desired number of bytes have been read or EOF is reached. Returns the number
     *  of bytes actually skipped.
     *
     *  @since  1.0.8
     */
    public static long skipFully(InputStream in, long bytesToSkip)
    throws IOException
    {
        // implementation note: Inputstream.skip() won't tell us when we've reached EOF,
        // so we have to resort to repeated reads ... this buffer should fit in Eden
        byte[] buf = new byte[8192];
        long bytesSkipped = 0;
        while (bytesToSkip > 0)
        {
            int len = (int)Math.min(buf.length, bytesToSkip);
            if (len == 0)
                break;
            int count = in.read(buf, 0, len);
            if (count < 0)
                return bytesSkipped;
            bytesSkipped += count;
            bytesToSkip -= count;
        }
        return bytesSkipped;
    }
}
