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
 *  A wrapper for byte-oriented buffers ({@link MappedFileBuffer} and
 *  <code>java.nio.ByteBuffer</code>), intended to provide a common API:
 *  one that uses absolute addressing with <code>long</code> offsets. In
 *  addition, this class supports a "relocation base" that is added to
 *  each offset.
 *  <p>
 *  The primary use case for this class is to allow an application to be
 *  tested using manually-constructed <code>ByteBuffer</code>s, but to be
 *  run with a memory-mapped file (or, given relocation, some subset of a
 *  memory-mapped file).
 *  <p>
 *  <em>Warnings and Caveats:</em>
 *  <ul>
 *  <li> This class is not thread-safe. However, it supports the thread-safe
 *       wrapper classes defined in this package.
 *  <li> Byte order must be set on the underlying buffer.
 *  <li> Methods in this class may change the position of the underlying buffer.
 *  </ul>
 */
public class BufferFacade
{
    private Accessor _accessor;


    /**
     *  Creates an instance that accesses a standard <code>ByteBuffer</code>.
     *  All indexes are limited to <code>Integer.MAX_VALUE</code>.
     */
    public BufferFacade(ByteBuffer buf)
    {
        _accessor = new ByteBufferAccessor(buf);
    }


    /**
     *  Creates an instance that accesses a standard <code>ByteBuffer</code>,
     *  with offsets relative to the specified base value (which must be less
     *  than <code>Integer.MAX_VALUE</code>. All indexes are limited to
     *  <code>Integer.MAX_VALUE - base</code>.
     */
    public BufferFacade(ByteBuffer buf, long base)
    {
        _accessor = new ByteBufferAccessor(buf, (int)base);
    }


    /**
     *  Creates an instance that accesses a thread-local instance of a standard
     *  <code>ByteBuffer</code>.
     */
    public BufferFacade(ByteBufferThreadLocal tl)
    {
        _accessor = new ByteBufferTLAccessor(tl);
    }


    /**
     *  Creates an instance that accesses a thread-local instance of a standard
     *  <code>ByteBuffer</code>, with offsets relative to a specified base
     *  value.
     */
    public BufferFacade(ByteBufferThreadLocal tl, long base)
    {
        _accessor = new ByteBufferTLAccessor(tl, (int)base);
    }


    /**
     *  Creates an instance that accesses a {@link MappedFileBuffer}.
     */
    public BufferFacade(MappedFileBuffer buf)
    {
        _accessor = new MappedFileBufferAccessor(buf);
    }


    /**
     *  Creates an instance that accesses a {@link MappedFileBuffer}, with
     *  offsets relative to a specified base value.
     */
    public BufferFacade(MappedFileBuffer buf, long base)
    {
        _accessor = new MappedFileBufferAccessor(buf, base);
    }


    /**
     *  Creates an instance that accesses a thread-local instance of a
     *  {@link MappedFileBuffer}.
     */
    public BufferFacade(MappedFileBufferThreadLocal tl)
    {
        _accessor = new MappedFileBufferTLAccessor(tl);
    }


    /**
     *  Creates an instance that accesses a thread-local instance of a
     *  {@link MappedFileBuffer}, with  offsets relative to a specified
     *  base value.
     */
    public BufferFacade(MappedFileBufferThreadLocal tl, long base)
    {
        _accessor = new MappedFileBufferTLAccessor(tl, base);
    }


//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Returns the single byte at the specified index (relative to the
     *  relocation base).
     */
    public byte get(long index)
    {
        return _accessor.get(index);
    }


    /**
     *  Updates the single byte at the specified index (relative to the
     *  relocation base).
     */
    public void put(long index, byte value)
    {
        _accessor.put(index, value);
    }


    /**
     *  Returns the 2-byte <code>short</code> value at the specified index
     *  (relative to the relocation base).
     */
    public short getShort(long index)
    {
        return _accessor.getShort(index);
    }


    /**
     *  Sets the 2-byte <code>short</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putShort(long index, short value)
    {
        _accessor.putShort(index, value);
    }


    /**
     *  Returns the 4-byte <code>int</code> value at the specified index
     *  (relative to the relocation base).
     */
    public int getInt(long index)
    {
        return _accessor.getInt(index);
    }


    /**
     *  Sets the 4-byte <code>int</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putInt(long index, int value)
    {
        _accessor.putInt(index, value);
    }


    /**
     *  Returns the 8-byte <code>long</code> value at the specified index
     *  (relative to the relocation base).
     */
    public long getLong(long index)
    {
        return _accessor.getLong(index);
    }


    /**
     *  Sets the 8-byte <code>long</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putLong(long index, long value)
    {
        _accessor.putLong(index, value);
    }


    /**
     *  Returns the 4-byte <code>float</code> value at the specified index
     *  (relative to the relocation base).
     */
    public float getFloat(long index)
    {
        return _accessor.getFloat(index);
    }


    /**
     *  Sets the 4-byte <code>float</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putFloat(long index, float value)
    {
        _accessor.putFloat(index, value);
    }


    /**
     *  Returns the 8-byte <code>double</code> value at the specified index
     *  (relative to the relocation base).
     */
    public double getDouble(long index)
    {
        return _accessor.getDouble(index);
    }


    /**
     *  Sets the 8-byte <code>double</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putDouble(long index, double value)
    {
        _accessor.putDouble(index, value);
    }


    /**
     *  Returns the 2-byte <code>char</code> value at the specified index
     *  (relative to the relocation base).
     */
    public char getChar(long index)
    {
        return _accessor.getChar(index);
    }


    /**
     *  Sets the 2-byte <code>char</code> value at the specified index
     *  (relative to the relocation base).
     */
    public void putChar(long index, char value)
    {
        _accessor.putChar(index, value);
    }


    /**
     *  Returns an array containing the <code>len</code> bytes starting
     *  at the specified index (relative to the relocation base).
     */
    public byte[] getBytes(long index, int len)
    {
        return _accessor.getBytes(index, len);
    }


    /**
     *  Inserts the specified array into the buffer, starting at the given
     *  index (relative to the relocation base).
     */
    public void putBytes(long index, byte[] value)
    {
        _accessor.putBytes(index, value);
    }


    /**
     *  Returns a <code>ByteBuffer</code> that represents a slice of the
     *  underlying buffer (ie, shares the same backing store), starting at
     *  the given index (relative to the relocation base) and extending to
     *  the end of the underlying buffer.
     *  <p>
     *  The semantics of this method depend on the underlying buffer. For a
     *  normal <code>ByteBuffer</code>, the limit will be determined by the
     *  size of the original buffer. For a <code>MappedFileBuffer</code>,
     *  the limit will depend on the particular segment containing the offset.
     */
    public ByteBuffer slice(long index)
    {
        return _accessor.slice(index);
    }


    /**
     *  Returns the capacity of the wrapped buffer.
     */
    public long capacity()
    {
        return _accessor.capacity();
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  There is one implementation of <code>Accessor</code> for each
     *  supported buffer type, and each implementation provides all of
     *  the methods in the public API.
     */
    private interface Accessor
    {
        public byte get(long index);
        public void put(long index, byte value);

        public short getShort(long index);
        public void putShort(long index, short value);

        public int getInt(long index);
        public void putInt(long index, int value);

        public long getLong(long index);
        public void putLong(long index, long value);

        public float getFloat(long index);
        public void putFloat(long index, float value);

        public double getDouble(long index);
        public void putDouble(long index, double value);

        public char getChar(long index);
        public void putChar(long index, char value);

        public byte[] getBytes(long index, int len);
        public void putBytes(long index, byte[] value);

        public ByteBuffer slice(long index);

        public long capacity();
    }


    private static class ByteBufferAccessor
    implements Accessor
    {
        private ByteBuffer _buf;
        private int _base;

        public ByteBufferAccessor(ByteBuffer buf)
        {
            _buf = buf;
        }

        public ByteBufferAccessor(ByteBuffer buf, int base)
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


    private static class ByteBufferTLAccessor
    implements Accessor
    {
        private ByteBufferThreadLocal _tl;
        private int _base;

        public ByteBufferTLAccessor(ByteBufferThreadLocal buf)
        {
            _tl = buf;
        }

        public ByteBufferTLAccessor(ByteBufferThreadLocal buf, int base)
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


    private static class MappedFileBufferAccessor
    implements Accessor
    {
        private MappedFileBuffer _buf;
        private long _base;

        public MappedFileBufferAccessor(MappedFileBuffer buf)
        {
            _buf = buf;
        }

        public MappedFileBufferAccessor(MappedFileBuffer buf, long base)
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


    private static class MappedFileBufferTLAccessor
    implements Accessor
    {
        private MappedFileBufferThreadLocal _tl;
        private long _base;

        public MappedFileBufferTLAccessor(MappedFileBufferThreadLocal tl)
        {
            _tl = tl;
        }

        public MappedFileBufferTLAccessor(MappedFileBufferThreadLocal tl, long base)
        {
            this(tl);
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
