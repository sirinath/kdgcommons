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

package net.sf.kdgcommons.codec;

import net.sf.kdgcommons.lang.CharSequenceUtil;
import net.sf.kdgcommons.lang.StringUtil;


/**
 *  Converts binary data to/from a string of hexadecimal digits (eg, an array
 *  containing the bytes <code>[0x01, 0x23, 0xEF]</code> is encoded as the
 *  string <code>"0123EF"</code>).
 *  <p>
 *  When converting to a string, you may add an optional separator every N
 *  characters (which must be even). This is normally used to create line
 *  breaks, but may be anything: you can create a comma-delimited list with
 *  the separator <code>"\",\""</code> (although you'll have to prepend and
 *  append the opening and closing quotes).
 *  <p>
 *  Conversion to byte arrays always ignores whitespace, and also ignores the
 *  specified separator (if one exists). Any other characters throw an
 *  <code>IllegalArgumentException</code>.
 *  <p>
 *  Conversion to byte arrays also ignores any trailing nibble: only an even
 *  number of characters will be processed.
 *
 *  @since 1.0.14
 */
public class HexCodec implements StringCodec
{
    private final static byte[] EMPTY_ARRAY = new byte[0];

    private int _lineLength;
    private String _separator;

    /**
     *  Creates an instance that produces unbroken strings of hex digits.
     */
    public HexCodec()
    {
        _lineLength = Integer.MAX_VALUE;
    }


    /**
     *  Creates an instance that produces strings with separators inserted every
     *  <code>lineLength</code> characters, and ignores the specified separator
     *  when reading strings.
     */
    public HexCodec(int lineLength, String separator)
    {
        _lineLength = lineLength;
        _separator = separator;
    }

//----------------------------------------------------------------------------
//  Implementation of StringCodec
//----------------------------------------------------------------------------

    public String toString(byte[] data)
    {
        if (data == null) return "";

        StringBuilder sb = bytesToChars(data);
        sb = insertSeparators(sb);

        return sb.toString();
    }


    public byte[] toBytes(String str)
    {
        if (StringUtil.isEmpty(str)) return EMPTY_ARRAY;

        return charsToBytes(cleanString(new StringBuilder(str)));
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static char[] byteLookup =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };


    private StringBuilder bytesToChars(byte[] data)
    {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int ii = 0 ; ii < data.length ; ii++)
        {
            int val = data[ii] & 0xFF;
            sb.append(byteLookup[val >> 4]);
            sb.append(byteLookup[val & 0xF]);
        }
        return sb;
    }


    private StringBuilder insertSeparators(StringBuilder sb)
    {
        if (_separator == null) return sb;

        int remain = sb.length();
        if (remain < _lineLength) return sb;

        int cap = remain + (remain / _lineLength + 1) * _separator.length();
        StringBuilder sb2 = new StringBuilder(cap);

        int off = 0;
        do
        {
            int len = Math.min(remain, _lineLength);
            sb2.append(sb.subSequence(off, off + len));
            off += len;
            remain -= len;
            if (remain > 0) sb2.append(_separator);
        }
        while (remain > 0);

        return sb2;
    }


    private StringBuilder cleanString(StringBuilder str)
    {
        StringBuilder res = new StringBuilder(str.length());
        for (int off = 0 ; off < str.length() ; )
        {
            if (Character.isWhitespace(str.charAt(off)))
                off++;
            else if (CharSequenceUtil.containsAt(str, _separator, off))
                off += _separator.length();
            else
                res.append(str.charAt(off++));
        }
        return res;
    }


    private static byte[] charsToBytes(CharSequence str)
    {
        byte[] bytes = new byte[str.length() / 2];
        for (int ii = 0 ; ii < bytes.length ; ii++)
        {
            int n1 = toNibble(str.charAt(ii * 2)) << 4;
            int n2 = toNibble(str.charAt(ii * 2 + 1));
            bytes[ii] = (byte)(n1 | n2);
        }
        return bytes;
    }


    private static int toNibble(char c)
    {
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        else if ((c >= 'A') && (c <= 'F'))
            return c - 'A' + 10;
        else if ((c >= 'a') && (c <= 'f'))
            return c - 'a' + 10;
        else
            throw new IllegalArgumentException("invalid hex character: '" + c + "' (" + (int)c + ")");
    }
}
