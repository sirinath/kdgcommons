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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


/**
 *  A wrapper for memory-mapped files that generally preserves the semantics of
 *  <code>ByteBuffer</code>, while supporing files larger than 2 GB. Unlike
 *  normal byte buffers, all access via absolute index, and indexes are
 *  <code>long</code> values.
 *  <p>
 *  This is achieved using a set of overlapping buffers, based on the "segment
 *  size" passed during construction. Segment size is the largest contiguous
 *  sub-buffer that may be accessed (via {@link getBytes} and {@link #putBytes}),
 *  and may be no larger than 1 GB.
 *  <p>
 *  <em>Warning:</em> this class is not thread-safe; caller must expliitly
 *  synchronize access.
 */
public class MappedFileBuffer
implements Closeable
{
    private final static int MAX_SEGMENT_SIZE = Integer.MAX_VALUE / 2;

    private RandomAccessFile _mappedFile;
    private long _fileSize;
    private long _segmentSize;              // long because it's used long expressions
    private MappedByteBuffer[] _buffers;

    /**
     *  Opens and memory-maps the specified file for read-only access, using
     *  the maximum segment size.
     *
     *  @param  file        The file to open; must be accessible to user.
     *
     *  @throws IllegalArgumentException if <code>segmentSize</code> is > 1GB.
     */
    public MappedFileBuffer(File file)
    throws IOException
    {
        this(file, MAX_SEGMENT_SIZE, false);
    }


    /**
     *  Opens and memory-maps the specified file for read-only or read-write
     *  access, using the maximum segment size.
     *
     *  @param  file        The file to open; must be accessible to user.
     *  @param  readWrite   Pass <code>true</code> to open the file with
     *                      read-write access, <code>false</code> to open
     *                      with read-only access.
     *
     *  @throws IllegalArgumentException if <code>segmentSize</code> is > 1GB.
     */
    public MappedFileBuffer(File file, boolean readWrite)
    throws IOException
    {
        this(file, MAX_SEGMENT_SIZE, readWrite);
    }


    /**
     *  Opens and memory-maps the specified file, for read-only or read-write
     *  access, with a specified segment size.
     *
     *  @param  file        The file to open; must be accessible to user.
     *  @param  segmentSize The largest contiguous sub-buffer that can be
     *                      created using {@link #slice}. The maximum size
     *                      is 2^30 - 1.
     *  @param  readWrite   Pass <code>true</code> to open the file with
     *                      read-write access, <code>false</code> to open
     *                      with read-only access.
     *
     *  @throws IllegalArgumentException if <code>segmentSize</code> is > 1GB.
     */
    public MappedFileBuffer(File file, int segmentSize, boolean readWrite)
    throws IOException
    {
        if (segmentSize > MAX_SEGMENT_SIZE)
            throw new IllegalArgumentException(
                    "segment size too large (max " + MAX_SEGMENT_SIZE + "): " + segmentSize);
        _segmentSize = segmentSize;

        String mode = readWrite ? "rw" : "r";
        _mappedFile = new RandomAccessFile(file, mode);
        FileChannel channel = _mappedFile.getChannel();

        MapMode mapMode = readWrite ? MapMode.READ_WRITE : MapMode.READ_ONLY;
        _fileSize = file.length();
        _buffers = new MappedByteBuffer[(int)(_fileSize / segmentSize) + 1];
        int bufIdx = 0;
        for (long offset = 0 ; offset < _fileSize ; offset += segmentSize)
        {
            long remainingFileSize = _fileSize - offset;
            long thisSegmentSize = Math.min(2L * segmentSize, remainingFileSize);
            _buffers[bufIdx++] = channel.map(mapMode, offset, thisSegmentSize);
        }
    }


    /**
     *  Returns the buffer's capacity -- the size of the mapped file.
     */
    public long capacity()
    {
        return _fileSize;
    }


    /**
     *  Returns the byte-order of this buffer (actually, the order of the first
     *  child buffer; they should all be the same).
     */
    public ByteOrder getByteOrder()
    {
        return _buffers[0].order();
    }


    /**
     *  Sets the order of this buffer (propagated to all child buffers).
     */
    public void setByteOrder(ByteOrder order)
    {
        for (ByteBuffer child : _buffers)
            child.order(order);
    }


    public byte get(long index)
    {
        return buffer(index).get();
    }


    public void put(long index, byte value)
    {
        buffer(index).put(value);
    }


    public int getInt(long index)
    {
        return buffer(index).getInt();
    }


    public void putInt(long index, int value)
    {
        buffer(index).putInt(value);
    }


    public long getLong(long index)
    {
        return buffer(index).getLong();
    }


    public void putLong(long index, long value)
    {
        buffer(index).putLong(value);
    }


    public short getShort(long index)
    {
        return buffer(index).getShort();
    }


    public void putShort(long index, short value)
    {
        buffer(index).putShort(value);
    }


    public float getFloat(long index)
    {
        return buffer(index).getFloat();
    }


    public void putFloat(long index, float value)
    {
        buffer(index).putFloat(value);
    }


    public double getDouble(long index)
    {
        return buffer(index).getDouble();
    }


    public void putDouble(long index, double value)
    {
        buffer(index).putDouble(value);
    }


    public char getChar(long index)
    {
        return buffer(index).getChar();
    }


    public void putChar(long index, char value)
    {
        buffer(index).putChar(value);
    }


    public byte[] getBytes(long index, int len)
    {
        byte[] ret = new byte[len];
        buffer(index).get(ret);
        return ret;
    }


    public void putBytes(long index, byte[] value)
    {
        buffer(index).put(value);
    }


    /**
     *  Creates a new buffer, whose size will be >= segment size, starting at
     *  the specified offset.
     */
    public ByteBuffer slice(long index)
    {
        return buffer(index).slice();
    }


    /**
     *  Iterates through the underlying buffers, calling <code>force()</code>
     *  on each; this will cause the buffers' contents to be written to disk.
     */
    public void force()
    {
        checkState();
        for (MappedByteBuffer buf : _buffers)
            buf.force();
    }


    /**
     *  Closes the underlying file, and invalidates all buffers. Subsequent
     *  calls will return silently. Attempts to use the buffer after calling
     *  <code>close()</code> will throw <code>IllegalStateException</code>
     */
    public void close()
    throws IOException
    {
        if (_mappedFile == null)
            return;

        _mappedFile.close();
        _mappedFile = null;
        _buffers = null;
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    @Override
    protected void finalize()
    throws Throwable
    {
        close();
    }


    private void checkState()
    {
        if (_mappedFile == null)
            throw new IllegalStateException("buffer has been closed");
    }


    private ByteBuffer buffer(long index)
    {
        checkState();
        ByteBuffer buf = _buffers[(int)(index / _segmentSize)];
        buf.position((int)(index % _segmentSize));
        return buf;
    }
}
