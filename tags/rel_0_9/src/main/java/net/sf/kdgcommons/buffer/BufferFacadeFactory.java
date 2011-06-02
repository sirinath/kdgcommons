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

import java.nio.ByteBuffer;


/**
 *  Creates {@link BufferFacade} instances to wrap variety of buffer-like objects.
 *  In addition to simple wrappers, this class will also create "offset" wrappers:
 *  all requested locations are offset a fixed distance from the start of the
 *  underlying buffer.
 *  <p>
 *  There are two factory methods (which are overloaded by buffer type and whether
 *  the facade is offset):
 *  <ul>
 *  <li> <code>create()</code> creates a facade for single-threaded access
 *  <li> <code>createThreadsafe</code> creates a facade that will support concurrent
 *       access
 *  </ul>
 *  <p>
 *  If you don't like the factory method, the implementation classes are exposed.
 */
public class BufferFacadeFactory
{
    /**
     *  Creates an instance that accesses a standard <code>ByteBuffer</code>.
     *  All indexes are limited to <code>Integer.MAX_VALUE</code>.
     */
    public static BufferFacade create(ByteBuffer buf)
    {
        return new ByteBufferFacade(buf);
    }


    /**
     *  Creates an instance that accesses a standard <code>ByteBuffer</code>,
     *  with offsets relative to the specified base value. Although the base
     *  value is specified as a <code>long</code> (for consistency with other
     *  methods), it is limited to <code>Integer.MAX_VALUE</code> and all
     *  indexes are limited to <code>Integer.MAX_VALUE - base</code>.
     */
    public static BufferFacade create(ByteBuffer buf, long base)
    {
        return new ByteBufferFacade(buf, (int)base);
    }


    /**
     *  Creates a thread-safe instance that accesses a standard
     *  <code>ByteBuffer</code>. All indexes are limited to
     *  <code>Integer.MAX_VALUE</code>.
     */
    public static BufferFacade createThreadsafe(ByteBuffer buf)
    {
        return new ByteBufferTLFacade(buf);
    }


    /**
     *  Creates a thread-safe instance that accesses a standard
     *  <code>ByteBuffer</code>, with offsets relative to the specified base
     *  value. Although the base value is specified as a <code>long</code>
     *  (for consistency with other methods), it is limited to
     *  <code>Integer.MAX_VALUE</code> and all indexes are limited to
     *  <code>Integer.MAX_VALUE - base</code>.
     */
    public static BufferFacade createThreadsafe(ByteBuffer buf, long base)
    {
        return new ByteBufferTLFacade(buf, (int)base);
    }


    /**
     *  Creates an instance that accesses a {@link MappedFileBuffer}.
     */
    public static BufferFacade create(MappedFileBuffer buf)
    {
        // no need to wrap this
        return buf;
    }


    /**
     *  Creates an instance that accesses a {@link MappedFileBuffer}, with
     *  with offsets relative to the specified base value.
     */
    public static BufferFacade create(MappedFileBuffer buf, long base)
    {
        return new MappedFileBufferFacade(buf, base);
    }


    /**
     *  Creates a thread-safe instance that accesses a {@link MappedFileBuffer}.
     */
    public static BufferFacade createThreadsafe(MappedFileBuffer buf)
    {
        return new MappedFileBufferTLFacade(buf);
    }


    /**
     *  Creates a thread-safe instance that accesses a {@link MappedFileBuffer},
     *  with offsets relative to the specified base  value.
     */
    public static BufferFacade createThreadsafe(MappedFileBuffer buf, long base)
    {
        return new MappedFileBufferTLFacade(buf, (int)base);
    }


//----------------------------------------------------------------------------
//  Facade Implementation Classes
//----------------------------------------------------------------------------

    /**
     *  A facade for a standard Java <code>ByteBuffer</code>.
     */
    public static class ByteBufferFacade
    implements BufferFacade
    {
        private ByteBuffer _buf;
        private int _base;

        public ByteBufferFacade(ByteBuffer buf)
        {
            _buf = buf;
        }

        public ByteBufferFacade(ByteBuffer buf, int base)
        {
            this(buf);
            _base = base;
        }

        public byte get(long index)
        {
            return _buf.get((int)index + _base);
        }

        public void put(long index, byte value)
        {
            _buf.put((int)index + _base, value);
        }

        public short getShort(long index)
        {
            return _buf.getShort((int)index + _base);
        }

        public void putShort(long index, short value)
        {
            _buf.putShort((int)index + _base, value);
        }

        public int getInt(long index)
        {
            return _buf.getInt((int)index + _base);
        }

        public void putInt(long index, int value)
        {
            _buf.putInt((int)index + _base, value);
        }

        public long getLong(long index)
        {
            return _buf.getLong((int)index + _base);
        }

        public void putLong(long index, long value)
        {
            _buf.putLong((int)index + _base, value);
        }

        public float getFloat(long index)
        {
            return _buf.getFloat((int)index + _base);
        }

        public void putFloat(long index, float value)
        {
            _buf.putFloat((int)index + _base, value);
        }

        public double getDouble(long index)
        {
            return _buf.getDouble((int)index + _base);
        }

        public void putDouble(long index, double value)
        {
            _buf.putDouble((int)index + _base, value);
        }

        public char getChar(long index)
        {
            return _buf.getChar((int)index + _base);
        }

        public void putChar(long index, char value)
        {
            _buf.putChar((int)index + _base, value);
        }

        public byte[] getBytes(long index, int len)
        {
            _buf.position((int)index + _base);

            byte[] ret = new byte[len];
            _buf.get(ret);
            return ret;
        }

        public void putBytes(long index, byte[] value)
        {
            _buf.position((int)index + _base);
            _buf.put(value);
        }

        public ByteBuffer slice(long index)
        {
            _buf.position((int)index + _base);
            return _buf.slice();
        }

        public long capacity()
        {
            return _buf.capacity() - _base;
        }
    }


    /**
     *  A facade for a standard Java <code>ByteBuffer</code> that uses a
     *  thread-local to allow concurrent access.
     */
    public static class ByteBufferTLFacade
    implements BufferFacade
    {
        private ByteBufferThreadLocal _tl;
        private int _base;

        public ByteBufferTLFacade(ByteBuffer buf)
        {
            _tl = new ByteBufferThreadLocal(buf);
        }

        public ByteBufferTLFacade(ByteBuffer buf, int base)
        {
            this(buf);
            _base = base;
        }

        public byte get(long index)
        {
            return _tl.get().get((int)index + _base);
        }

        public void put(long index, byte value)
        {
            _tl.get().put((int)index + _base, value);
        }

        public short getShort(long index)
        {
            return _tl.get().getShort((int)index + _base);
        }

        public void putShort(long index, short value)
        {
            _tl.get().putShort((int)index + _base, value);
        }

        public int getInt(long index)
        {
            return _tl.get().getInt((int)index + _base);
        }

        public void putInt(long index, int value)
        {
            _tl.get().putInt((int)index + _base, value);
        }

        public long getLong(long index)
        {
            return _tl.get().getLong((int)index + _base);
        }

        public void putLong(long index, long value)
        {
            _tl.get().putLong((int)index + _base, value);
        }

        public float getFloat(long index)
        {
            return _tl.get().getFloat((int)index + _base);
        }

        public void putFloat(long index, float value)
        {
            _tl.get().putFloat((int)index + _base, value);
        }

        public double getDouble(long index)
        {
            return _tl.get().getDouble((int)index + _base);
        }

        public void putDouble(long index, double value)
        {
            _tl.get().putDouble((int)index + _base, value);
        }

        public char getChar(long index)
        {
            return _tl.get().getChar((int)index + _base);
        }

        public void putChar(long index, char value)
        {
            _tl.get().putChar((int)index + _base, value);
        }

        public byte[] getBytes(long index, int len)
        {
            ByteBuffer buf = _tl.get();
            buf.position((int)index + _base);

            byte[] ret = new byte[len];
            buf.get(ret);
            return ret;
        }

        public void putBytes(long index, byte[] value)
        {
            ByteBuffer buf = _tl.get();
            buf.position((int)index + _base);
            buf.put(value);
        }

        public ByteBuffer slice(long index)
        {
            ByteBuffer buf = _tl.get();
            buf.position((int)index + _base);
            return buf.slice();
        }

        public long capacity()
        {
            return _tl.get().capacity() - _base;
        }
    }


    /**
     *  A facade for a {@link MappedFileBuffer}. This is only needed when creating
     *  an offset facade, as <code>MappedFileBuffer</code> already implements
     *  <code>BufferFacade</code>.
     */
    private static class MappedFileBufferFacade
    implements BufferFacade
    {
        private MappedFileBuffer _buf;
        private long _base;

        public MappedFileBufferFacade(MappedFileBuffer buf)
        {
            _buf = buf;
        }

        public MappedFileBufferFacade(MappedFileBuffer buf, long base)
        {
            this(buf);
            _base = base;
        }

        public byte get(long index)
        {
            return _buf.get(index + _base);
        }

        public void put(long index, byte value)
        {
            _buf.put(index + _base, value);
        }

        public short getShort(long index)
        {
            return _buf.getShort(index + _base);
        }

        public void putShort(long index, short value)
        {
            _buf.putShort(index + _base, value);
        }

        public int getInt(long index)
        {
            return _buf.getInt(index + _base);
        }

        public void putInt(long index, int value)
        {
            _buf.putInt(index + _base, value);
        }

        public long getLong(long index)
        {
            return _buf.getLong(index + _base);
        }

        public void putLong(long index, long value)
        {
            _buf.putLong(index + _base, value);
        }

        public float getFloat(long index)
        {
            return _buf.getFloat(index + _base);
        }

        public void putFloat(long index, float value)
        {
            _buf.putFloat(index + _base, value);
        }

        public double getDouble(long index)
        {
            return _buf.getDouble(index + _base);
        }

        public void putDouble(long index, double value)
        {
            _buf.putDouble(index + _base, value);
        }

        public char getChar(long index)
        {
            return _buf.getChar(index + _base);
        }

        public void putChar(long index, char value)
        {
            _buf.putChar(index + _base, value);
        }

        public byte[] getBytes(long index, int len)
        {
            return _buf.getBytes(index + _base, len);
        }

        public void putBytes(long index, byte[] value)
        {
            _buf.putBytes(index + _base, value);
        }

        public ByteBuffer slice(long index)
        {
            return _buf.slice(index + _base);
        }

        public long capacity()
        {
            return _buf.capacity() - _base;
        }
    }


    /**
     *  A facade for a {@link MappedFileBuffer} that uses a thread-local to
     *  allow concurrent access.
     */
    public static class MappedFileBufferTLFacade
    implements BufferFacade
    {
        private MappedFileBufferThreadLocal _tl;
        private long _base;

        public MappedFileBufferTLFacade(MappedFileBuffer buf)
        {
            _tl = new MappedFileBufferThreadLocal(buf);
        }

        public MappedFileBufferTLFacade(MappedFileBuffer buf, long base)
        {
            this(buf);
            _base = base;
        }

        public byte get(long index)
        {
            return _tl.get().get(index + _base);
        }

        public void put(long index, byte value)
        {
            _tl.get().put(index + _base, value);
        }

        public short getShort(long index)
        {
            return _tl.get().getShort(index + _base);
        }

        public void putShort(long index, short value)
        {
            _tl.get().putShort(index + _base, value);
        }

        public int getInt(long index)
        {
            return _tl.get().getInt(index + _base);
        }

        public void putInt(long index, int value)
        {
            _tl.get().putInt(index + _base, value);
        }

        public long getLong(long index)
        {
            return _tl.get().getLong(index + _base);
        }

        public void putLong(long index, long value)
        {
            _tl.get().putLong(index + _base, value);
        }

        public float getFloat(long index)
        {
            return _tl.get().getFloat(index + _base);
        }

        public void putFloat(long index, float value)
        {
            _tl.get().putFloat(index + _base, value);
        }

        public double getDouble(long index)
        {
            return _tl.get().getDouble(index + _base);
        }

        public void putDouble(long index, double value)
        {
            _tl.get().putDouble(index + _base, value);
        }

        public char getChar(long index)
        {
            return _tl.get().getChar(index + _base);
        }

        public void putChar(long index, char value)
        {
            _tl.get().putChar(index + _base, value);
        }

        public byte[] getBytes(long index, int len)
        {
            return _tl.get().getBytes(index + _base, len);
        }

        public void putBytes(long index, byte[] value)
        {
            _tl.get().putBytes(index + _base, value);
        }

        public ByteBuffer slice(long index)
        {
            return _tl.get().slice(index + _base);
        }

        public long capacity()
        {
            return _tl.get().capacity() - _base;
        }
    }
}
