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

import net.sf.kdgcommons.codec.Base64Codec;


/**
 *  Static utility methods to convert to/from Base64, as described by RFC 2045.
 *  <p>
 *  The basic encode and decode methods read and write from arbitrary segments
 *  of existing <code>byte[]</code>s. These are meant to be used with buffers
 *  created and filled by the calling code.
 *
 *  @deprecated Replaced by {@link net.sf.kdgcommons.codec.Base64Codec}
 */
@Deprecated
public class Base64Converter
{
    /**
     *  Determines whether the passed character is a valid Base64 encoding
     *  character. This is used when filling a buffer from a source that may
     *  have line breaks or other delimiters.
     */
    public static boolean isBase64Char(char c)
    {
        return (c == '=') || (DECODE_TABLE[(c & 0xFF)] >= 0);
    }


    /**
     *  Encodes the specified segment of a source <code>byte[]</code> as Base64
     *  characters at the specified offset of a destination array. Caller is
     *  responsible for ensuring that the destination array is large enough to
     *  hold all encoded bytes.
     *
     *  @param  src     Source data.
     *  @param  off     Offset within the source data to start encoding.
     *  @param  len     Number of bytes to encode from source data.
     *  @param  dst     Destination for encoded bytes. Since Base64 produces
     *                  only ASCII data, which is typically written directly
     *                  to a stream, this is a <code>byte[]</code> rather
     *                  than a <code>char[]</code>.
     *  @param  doff    Offset within destination where encoded bytes are
     *                  written.
     *
     *  @return The number of bytes added to the destination array by this
     *          operation. Will always be a multiple of 4.
     */
    public static int encode(byte[] src, int off, int len, byte[] dst, int doff)
    {
        byte[] out = (new Base64Codec()).encode(src,doff, len);
        System.arraycopy(out, 0, dst, doff, out.length);
        return out.length;
    }


    /**
     *  Convenience method that creates a correctly-sized output array, and
     *  Base64 encodes the source array into it.
     */
    public static byte[] encode(byte[] src)
    {
        return (new Base64Codec()).encode(src);
    }


    /**
     *  Decodes the specified segment of a source <code>byte[]</code> as Base64
     *  characters, inserting the decoded bytes at the specified offset of a
     *  destination array. Caller is responsible for ensuring that this array
     *  can hold all decoded bytes.
     *  <p>
     *  The source array <em>may not</em> contain non-Base64 characters. The
     *  segment length <em>must</em> be a multiple of 4, padded if necessary
     *  with '=' characters.
     *
     *  @param  src     Source data, containing Base64 encoded characters.
     *  @param  off     Offset within the source data to start decoding.
     *  @param  len     Number of bytes to decode from source data. This must
     *                  be a multiple of 4.
     *  @param  dst     Destination for decoded bytes.
     *  @param  doff    Offset within destination where dencoded bytes are
     *                  written.
     *
     *  @return The number of bytes decoded.
     */
    public static int decode(byte[] src, int off, int len, byte[] dst, int doff)
    {
        byte[] out = (new Base64Codec()).decode(src, off, len);
        System.arraycopy(out, 0, dst, doff, out.length);
        return out.length;
    }


    /**
     *  Convenience method to decode an entire array of Base64 characters into
     *  its corresponding <code>byte[]</code>.
     */
    public static byte[] decode(byte[] src)
    {
        return (new Base64Codec()).decode(src);
    }


    /**
     *  Convenience method to decode a string of Base64 characters into its
     *  corresponding <code>byte[]</code>, ignoring any illegal characters.
     */
    public static byte[] decode(String src)
    {
        return (new Base64Codec()).toBytes(src);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static char[] ENCODE_TABLE = new char[]
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',     // 0x00 .. 0x07
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',     // 0x08 .. 0x0F
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',     // 0x10 .. 0x17
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',     // 0x18 .. 0x1F
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',     // 0x20 .. 0x27
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',     // 0x28 .. 0x2F
        'w', 'x', 'y', 'z', '0', '1', '2', '3',     // 0x30 .. 0x37
        '4', '5', '6', '7', '8', '9', '+', '/'      // 0x38 .. 0x3F

    };


    private static int[] DECODE_TABLE = new int[]
    {
        -1, -1, -1, -1, -1, -1, -1, -1,             // NUL .. DEL
        -1, -1, -1, -1, -1, -1, -1, -1,             // BS .. SI
        -1, -1, -1, -1, -1, -1, -1, -1,             // DLE .. ETB
        -1, -1, -1, -1, -1, -1, -1, -1,             // CAN .. US
        -1, -1, -1, -1, -1, -1, -1, -1,             // ' ' .. '''
        -1, -1, -1, 62, -1, -1, -1, 63,             // '(' .. '/''
        52, 53, 54, 55, 56, 57, 58, 59,             // '0 ' .. '7'
        60, 61, -1, -1, -1, -1, -1, -1,             // '8' .. '?'
        -1,  0,  1,  2,  3,  4,  5,  6,             // '@' .. 'G'
         7,  8,  9, 10, 11, 12, 13, 14,             // 'H' .. 'O'
        15, 16, 17, 18, 19, 20, 21, 22,             // 'P' .. 'W'
        23, 24, 25, -1, -1, -1, -1, -1,             // 'X' .. '_'
        -1, 26, 27, 28, 29, 30, 31, 32,             // '`' .. 'g'
        33, 34, 35, 36, 37, 38, 39, 40,             // 'h' .. 'o'
        41, 42, 43, 44, 45, 46, 47, 48,             // 'p' .. 'w'
        49, 50, 51, -1, -1, -1, -1, -1,             // 'x' .. DEL
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0x80 .. 0x87
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0x88 .. 0x8F
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0x90 .. 0x97
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0x98 .. 0x9F
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xA0 .. 0xA7
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xA8 .. 0xAF
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xB0 .. 0xB7
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xB8 .. 0xBF
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xC0 .. 0xC7
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xC8 .. 0xCF
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xD0 .. 0xD7
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xD8 .. 0xDF
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xE0 .. 0xE7
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xE8 .. 0xEF
        -1, -1, -1, -1, -1, -1, -1, -1,             // 0xF0 .. 0xF7
        -1, -1, -1, -1, -1, -1, -1, -1              // 0xF8 .. 0xFF
    };
}
