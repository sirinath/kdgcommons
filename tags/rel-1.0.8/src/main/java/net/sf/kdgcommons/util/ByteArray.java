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

import java.io.UnsupportedEncodingException;


/**
 * This class manages a variable-length array of bytes. It's primary use is as
 * a replacement for <code>StringBuffer</code> for applications that need to
 * deal with 8-bit character strings (eg, those that exchange data with legacy
 * C programs).
 */

public class ByteArray
{
//----------------------------------------------------------------------------
//  Instance data and constructors
//----------------------------------------------------------------------------

    protected byte[]    _data;
    protected int       _size;          // current bytes in array
    protected int       _expandBy;      // percent to expand when needed


    /**
     *  Constructs a new <code>ByteArray</code>, with specified initial
     *  capacity and expansion factor. This is the basic constructor.
     *
     *  @param  capacity    The initial size of the underlying array.
     *  @param  factor      The exansion factor. This is the percentage by
     *                      which the array should be expanded, whenever
     *                      additions run out of space.
     */
    public ByteArray(int capacity, int factor)
    {
        _data = new byte[capacity];
        _size = 0;
        _expandBy = factor;
    }


    /**
     *  Constructs a new <code>ByteArray</code> from a <code>byte[]</code>.
     *  The source array is copied into the new object, with some room to
     *  grow, and it is given a default expansion factor.
     */
    public ByteArray(byte[] src)
    {
        this((src.length * 5)/4, 25);
        add(src);
    }


    /**
     *  Constructs a new <code>ByteArray</code> from a <code>String</code>, using
     *  ISO-8859-1 encoding. The array is given the default expansion factor. Null
     *  strings will be converted to a zero-length array.
     *
     *  @throws IllegalArgumentException if the passed string contains characters
     *          outside the 8-bit range.
     */
    public ByteArray(String src)
    {
        this(convertToISO8859(src));
    }


    /**
     *  Constructs a new <code>ByteArray</code> from a <code>String</code>, using
     *  the specified encoding. The array is given the default expansion factor.
     * @throws UnsupportedEncodingException
     */
    public ByteArray(String src, String encoding)
    throws UnsupportedEncodingException
    {
        this(src.getBytes(encoding));
    }


    /**
     *  Creates a new, empty <code>ByteArray</code>, using a default capacity
     *  and expansion factor.
     */
    public ByteArray()
    {
        this(64, 25);
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Adds a single byte to the end of this array.
     */
    public void add(byte val)
    {
        ensureCapacity(1);
        _data[_size++] = val;
    }


    /**
     *  Adds a <code>byte[]</code> to the end of this array.
     */
    public void add(byte[] src)
    {
        add(src, 0, src.length);
    }


    /**
     *  Adds a segment of a <code>byte[]</code> to the end of this array.
     */
    public void add(byte[] src, int off, int len)
    {
        ensureCapacity(len);
        for (int ii = 0 ; ii < len ; ii++)
            _data[_size++] = src[off + ii];
    }


    /**
     *  Adds the low-order 8 bits of the passed character to this array.
     *  Only useful with ASCII or 8-bit character sets.
     */
    public void add(char src)
    {
        add((byte)(src & 0xFF));
    }


    /**
     *  Adds a <code>String</code> to the end of this array, converting it
     *  with ISO-8859-1 encoding.
     */
    public void add(String src)
    {
        add(convertToISO8859(src));
    }


    /**
     *  Adds a <code>String</code> to the end of this array, using the specified
     *  encoding. Note that this may result in multi-byte characters.
     */
    public void add(String src, String encoding)
    throws UnsupportedEncodingException
    {
        add(src.getBytes(encoding));
    }


    /**
     *  Adds another <code>ByteArray</code> to the end of this array.
     */
    public void add(ByteArray src)
    {
        ensureCapacity(src.size());
        for (int i = 0 ; i < src._size ; i++)
            _data[_size++] = src._data[i];
    }


    /**
     *  Returns a single byte from the array.
     *
     *  @param  idx     The index of the byte to be removed.
     *
     *  @throws ArrayIndexOutOfBoundsException if <code>idx</code> is outside
     *          the current bounds of the array.
     */
    public byte get(int idx)
    {
        if ((idx < 0) || (idx >= _size))
            throw new ArrayIndexOutOfBoundsException(idx);

        return _data[idx];
    }


    /**
     *  Returns the underlying array. This method exists for efficiency; most
     *  callers should use {@link #getBytes} instead.
     *  <p>
     *  Note that the returned array may be significantly larger than what is
     *  reported by {@link #size}.
     */
    public byte[] getArray()
    {
        return _data;
    }


    /**
     *  Returns a specified sub-section of the array. The returned bytes are
     *  copied from the actual array, and will not reflect subsequent changes.
     *
     *  @param  off     The starting byte of the subarray.
     *  @param  len     The length of the subarray.
     *
     *  @throws IllegalArgumentException if <code>off</code> and/or <code>
     *          len</code> specify indexes that are outside the bounds of
     *          the array.
     */
    public byte[] getBytes(int off, int len)
    {
        if ((off < 0) || (off > _size))
            throw new IllegalArgumentException("invalid offset: " + off);
        if (off + len > _size)
            throw new IllegalArgumentException("invalid length: " + len);

        byte[] result = new byte[len];
        for (int i = 0 ; i < len ; i++)
            result[i] = _data[off + i];

        return result;
    }


    /**
     *  Returns a <code>byte[]</code> containing the bytes from a specified
     *  offset to the end of the array. The returned array is a copy of the
     *  managed array, and will not reflect subsequent changes.
     *
     *  @param  off     The starting byte of the subarray.
     *  @throws ArrayIndexOutOfBoundsException if any of the bytes defined by
     *          <code>off</code> and <code>len</code> are outside the current
     *          bounds of the array.
     */
    public byte[] getBytes(int off)
    {
        return getBytes(off, (_size - off));
    }


    /**
     *  Returns a <code>byte[]</code> containing all bytes in this array. The
     *  returned array is a copy of this object's data, and will not reflect
     *  subsequent changes.
     */
    public byte[] getBytes()
    {
        return getBytes(0, _size);
    }


    /**
     *  Inserts the passed array at an arbitrary point in this array. All
     *  existing contents are moved up to make room.
     *
     *  @param  off     The position where the passed array is inserted.
     *  @param  src     The array to insert.
     */
    public void insert(int off, ByteArray src)
    {
        insert(off, src, 0, src.size());
    }


    /**
     *  Inserts a portion of the passed array at an arbitrary point in this
     *  array. All existing contents are moved up to make room.
     *
     *  @param  off     The position where the passed array is inserted.
     *  @param  src     The array to insert.
     *  @param  srcOff  The offset within the source array where the inserted
     *                  data begins.
     *  @param  srcLen  The number of bytes to transfer from the source array.
     *
     *  @throws IllegalArgumentException if <code>off</code> is larger than the
     *          current size of the array, or if <code>srcOff</code> or <code>
     *          srcOff + srcLen</code> is outside the bounds of the source
     *          array. These are checked prior to performing any moves, so this
     *          array will not be corrupted.
     */
    public void insert(int off, ByteArray src, int srcOff, int srcLen)
    {
        // have to check bounds before delegating, because offsets may be
        // outside controlled bounds, while inside physical bounds
        if ((srcOff < 0) || (srcOff > src.size()))
            throw new IllegalArgumentException("invalid src offset: " + srcOff);
        if (srcOff + srcLen > src.size())
            throw new IllegalArgumentException("invalid src length: " + srcLen);

        insert(off, src.getArray(), srcOff, srcLen);
    }


    /**
     *  Inserts the passed array at an arbitrary point in this array. All
     *  existing contents are moved up to make room.
     *
     *  @param  off     The position where the passed array is inserted.
     *  @param  src     The array to insert.
     */
    public void insert(int off, byte[] src)
    {
        insert(off, src, 0, src.length);
    }


    /**
     *  Inserts the passed array of bytes into the middle of this array.
     *
     *  @param  off     The position where the passed array is inserted.
     *  @param  src     The array to insert.
     *  @param  srcOff  The offset within the source array where the inserted
     *                  data begins.
     *  @param  srcLen  The number of bytes to transfer from the source array.
     *
     *  @throws IllegalArgumentException if <code>off</code> is larger than the
     *          current size of the array, or if <code>srcOff</code> or <code>
     *          srcOff + srcLen</code> is outside the bounds of the source
     *          array. These are checked prior to performing any moves, so this
     *          array will not be corrupted.
     */
    public void insert(int off, byte[] src, int srcOff, int srcLen)
    {
        if ((off < 0) || (off > _size))
            throw new IllegalArgumentException("invalid dst offset: " + off);
        if ((srcOff < 0) || (srcOff > src.length))
            throw new IllegalArgumentException("invalid src offset: " + srcOff);
        if (srcOff + srcLen > src.length)
            throw new IllegalArgumentException("invalid src length: " + srcLen);

        ensureCapacity(srcLen);
        System.arraycopy(_data, off, _data, off + srcLen, _size - off);
        System.arraycopy(src, srcOff, _data, off, srcLen);
        _size += srcLen;
    }


    /**
     *  Removes a specified byte from this array, shifting subsequent bytes
     *  down and reducing the size of the array.
     *
     *  @param  idx     The index of the byte to be removed.
     *
     *  @throws ArrayIndexOutOfBoundsException if <code>idx</code> is outside
     *  the current bounds of the array.
     */
    public void remove(int idx)
    {
        remove(idx, 1);
    }


    /**
     *  Removes a subset of the bytes in this array, shifting subsequent bytes
     *  down and reducing the size of the array.
     *
     *  @param  off     The starting byte to be removed.
     *  @param  len     The number of bytes to be removed.
     *  @throws ArrayIndexOutOfBoundsException if any of the bytes defined by
     *          <code>off</code> and <code>len</code> are outside the current
     *          bounds of the array.
     */
    public void remove(int off, int len)
    {
        if ((off < 0) || (off + len >= _size))
            throw new IllegalArgumentException("invalid offset/length: " + off + "/" + len);

        int srcPos = off + len;
        int count = _size - srcPos;
        System.arraycopy(_data, srcPos, _data, off, count);
        _size -= len;
    }


    /**
     *  Removes the last byte in the array.
     */
    public void removeLast()
    {
        if (_size > 0)
            _size--;
    }


    /**
     *  Returns the current size of this array.
     */
    public int size()
    {
        return _size;
    }


    /**
     *  Resizes the array. If the specified size is less than the current
     *  size, the array is truncated. If it is greater than the current size,
     *  the array is expanded and the new space is filled with zero-bytes.
     *
     *  @param  size    The desired size of the array.
     */
    public void setSize(int size)
    {
        setCapacity(size);
        for (int ii = _size ; ii < size ; ii++)
            _data[ii] = (byte)0;
        _size = size;
    }


//----------------------------------------------------------------------------
//  Overrides of Object
//----------------------------------------------------------------------------


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  Converts a string using ISO-8859-1 encoding, throwing if the string
     *  cannot be translated.
     */
    private static byte[] convertToISO8859(String src)
    {
        // String.getBytes() doesn't throw if the encoding is invalid, so we'd
        // have to check every character anyway; given that, there's no reason
        // not to just copy manually
        byte[] result = new byte[src.length()];
        for (int ii = 0 ; ii < result.length ; ii++)
        {
            char c = src.charAt(ii);
            if (c > 255)
                throw new IllegalArgumentException("invalid character at position " + ii);
            result[ii] = (byte)c;
        }
        return result;
    }


    /**
     *  Verifies that the array can accept an insert of the specified size,
     *  and expands it if it can't. This should be called before every add.
     */
    private void ensureCapacity(int bytes)
    {
        if ((_size + bytes) < _data.length)
            return;

        int newSize = _data.length * _expandBy / 100;
        setCapacity(Math.max(newSize, (_size + bytes)));
    }


    /**
     *  Expands or contracts the underlying array to a specified size. If
     *  the requested size is less than the current size of the array's
     *  data, this request is ignored.
     */
    private void setCapacity(int size)
    {
        if (size < _size)
            return;

        byte[] newData = new byte[size];
        for (int ii = 0 ; ii < _size ; ii++)
            newData[ii] = _data[ii];

        _data = newData;
    }
}
