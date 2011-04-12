// Copyright (c) Keith D Gregory, all rights reserved
package net.sf.kdgcommons.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import net.sf.kdgcommons.lang.StringUtil;


/**
 *  Contains static utility methods for working with NIO buffers.
 *  <p>
 *  These methods are not thread-safe unless explicityly marked as such.
 */
public class BufferUtil
{
    /**
     *  Memory maps a segment of a file.
     *  <p>
     *  This method is thread-safe.
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
            IOUtil.closeQuietly(raf);
        }
    }


    /**
     *  Extracts a specified sequence of bytes from a buffer and converts it
     *  to a Java <code>String</code> using UTF-8 encoding.
     *
     *  @param  buf     The buffer
     *  @param  off     Offset within the buffer where the string starts
     *  @param  len     Number of bytes to be converted; caller is responsible
     *                  for ensuring that the buffer has those bytes available
     */
    public static String getUTF8String(ByteBuffer buf, int off, int len)
    {
        byte[] bytes = new byte[len];
        buf.position(off);
        buf.get(bytes, 0, len);
        return StringUtil.fromUTF8(bytes);
    }


    /**
     *  Returns a character array from the buffer. This method is equivalent to
     *  repeatedly calling <code>getChar()</code>.
     *
     *  @param  buf     The buffer
     *  @param  off     Offset within the buffer where conversion will start
     *  @param  count   The number of <em>characters</em> to retrieve (unlike
     *                  other methods, which specify the number of bytes)
     */
    public static char[] getChars(ByteBuffer buf, int off, int count)
    {
        char[] chars = new char[count];
        buf.position(off);
        for (int ii = 0 ; ii < count ; ii++)
            chars[ii] = buf.getChar();
        return chars;
    }
}
