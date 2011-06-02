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

package net.sf.kdgcommons.buffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import net.sf.kdgcommons.io.IOUtil;


/**
 *  A wrapper for memory-mapped files that generally preserves the semantics of
 *  <code>ByteBuffer</code>, while supporing files larger than 2 GB. Unlike
 *  normal byte buffers, all access via absolute index, and indexes are
 *  <code>long</code> values.
 *  <p>
 *  This is achieved using a set of overlapping buffers, based on the "segment
 *  size" passed during construction. Segment size is the largest contiguous
 *  sub-buffer that may be accessed (via {@link #getBytes} and {@link #putBytes}),
 *  and may be no larger than 1 GB.
 *  <p>
 *  <strong>Warning:</strong>
 *  This class is not thread-safe. Caller must explicitly synchronize access,
 *  or call {@link #clone} to create a distinct buffer for each thread.
 */
public class MappedFileBuffer
implements BufferFacade, Cloneable
{
    private final static int MAX_SEGMENT_SIZE = 0x8000000; // 1 GB, assures alignment

    private File _file;
    private boolean _isWritable;
    private long _segmentSize;              // long because it's used in long expressions
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
                    "segment size too large (max is " + MAX_SEGMENT_SIZE + "): " + segmentSize);

        _file = file;
        _isWritable = readWrite;
        _segmentSize = segmentSize;

        RandomAccessFile mappedFile = null;
        try
        {
            String mode = readWrite ? "rw" : "r";
            MapMode mapMode = readWrite ? MapMode.READ_WRITE : MapMode.READ_ONLY;

            mappedFile = new RandomAccessFile(file, mode);
            FileChannel channel = mappedFile.getChannel();

            long fileSize = file.length();

            _buffers = new MappedByteBuffer[(int)(fileSize / segmentSize) + 1];
            int bufIdx = 0;
            for (long offset = 0 ; offset < fileSize ; offset += segmentSize)
            {
                long remainingFileSize = fileSize - offset;
                long thisSegmentSize = Math.min(2L * segmentSize, remainingFileSize);
                _buffers[bufIdx++] = channel.map(mapMode, offset, thisSegmentSize);
            }
        }
        finally
        {
            IOUtil.closeQuietly(mappedFile);
        }
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Returns the buffer's capacity -- the size of the mapped file.
     */
    public long capacity()
    {
        return _file.length();
    }


    /**
     *  Returns the file that is mapped by this buffer.
     */
    public File file()
    {
        return _file;
    }


    /**
     *  Indicates whether this buffer is read-write or read-only.
     */
    public boolean isWritable()
    {
        return _isWritable;
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
     *  Creates a new buffer referencing the same file, but with
     */
    @Override
    public MappedFileBuffer clone()
    {
        try
        {
            MappedFileBuffer that = (MappedFileBuffer)super.clone();
            that._buffers = new MappedByteBuffer[_buffers.length];
            for (int ii = 0 ; ii < _buffers.length ; ii++)
            {
                // if the file is a multiple of the segment size, we
                // can end up with an empty slot in the buffer array
                if (_buffers[ii] != null)
                    that._buffers[ii] = (MappedByteBuffer)_buffers[ii].duplicate();
            }
            return that;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException("unreachable code", ex);
        }
    }



//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void checkState()
    {
        if (_buffers == null)
            throw new IllegalStateException("buffer has been closed");
    }


    // this is exposed for a white-box test of cloning
    protected ByteBuffer buffer(long index)
    {
        checkState();
        ByteBuffer buf = _buffers[(int)(index / _segmentSize)];
        buf.position((int)(index % _segmentSize));
        return buf;
    }
}
