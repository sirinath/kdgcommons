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

import java.util.HashMap;

import net.sf.kdgcommons.lang.CharSequenceUtil;
import net.sf.kdgcommons.lang.StringUtil;


/**
 *  Converts binary data to/from a string consisting of base-64 characters.
 *  <p>
 *  There are several standard options for string conversion line breaks, defined
 *  by the {@link #Option} enum. You can also specify your own breaks and maximum
 *  line length.  
 *  <p>
 *  Conversion to byte arrays always ignores whitespace, and will also ignore
 *  any specified start/end/separation strings (the separation string may appear
 *  any where in the source string, the start/end strings must appear in their
 *  respective locations).
 *  
 *  @since 1.0.14
 */
public class Base64Codec implements StringCodec
{
    public enum Option
    {
        /** Produces an unbroken string of base-64 characters. */
        UNBROKEN
    }
    
    
    private final static byte[] EMPTY_ARRAY = new byte[0];

    private Option _option;
    private int _lineLength;
    private String _start;
    private String _separator;
    private String _end;


    /**
     *  Default constructor; equivalent to using {@link #Option.UNBROKEN}.
     */
    public Base64Codec()
    {
        this(Option.UNBROKEN);
    }
      
    
    /**
     *  Constructs an instance that generates strings according to a standard option.
     */
    public Base64Codec(Option option)
    {
        _option = option;
        _lineLength = Integer.MAX_VALUE;
    }


    /**
     *  Creates an instance that produces strings with separators inserted every
     *  <code>lineLength</code> characters, and ignores the specified separator
     *  when reading strings.
     */
    public Base64Codec(int lineLength, String separator)
    {
        _lineLength = lineLength;
        _separator = separator;
    }


    /**
     *  Creates an instance that produces strings with an initial and terminal sequence,
     *  and separators inserted every <code>lineLength</code> characters, and which
     *  ignores these values when reading strings.
     */
    public Base64Codec(int lineLength, String start, String separator, String end)
    {
        this(lineLength, separator);
        _start = start;
        _end = end;
    }

//----------------------------------------------------------------------------
//  Implementation of StringCodec
//----------------------------------------------------------------------------

    public String toString(byte[] data)
    {
        if (data == null) return "";

        StringBuilder sb = bytesToChars(data);
        sb = insertSeparators(sb);

        if (_start != null) sb.insert(0, _start);
        if (_end != null) sb.append(_end);

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

    private static char[] charLookup =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    
    private static HashMap<Character,Integer> valueLookup = new HashMap<Character,Integer>(64);
    static
    {
        valueLookup.put('=', -1);
        for (int ii = 0 ; ii < charLookup.length ; ii++)
            valueLookup.put(Character.valueOf(charLookup[ii]), Integer.valueOf(ii));
    }


    private StringBuilder bytesToChars(byte[] data)
    {
        StringBuilder sb = new StringBuilder(data.length * 4 / 3 + 4);
        for (int ii = 0 ; ii < data.length ; ii += 3)
        {
            encodeGroup(data, ii, sb);
        }
        return sb;
    }
    
    
    private void encodeGroup(byte[] data, int off, StringBuilder out)
    {
        int b1 = data[off++] & 0xFF;
        int b2 = (off < data.length) ? data[off++] & 0xFF : -1;
        int b3 = (off < data.length) ? data[off++] & 0xFF : -1;
        
        int e1 = b1 >>> 2;
        
        int e2 = (b1 & 0x03) << 4;
        if (b2 >= 0)
            e2 |= b2 >> 4;
        
        int e3 = (b2 & 0xF) << 2;
        if (b3 >= 0)
            e3 |= b3 >> 6;
        
        int e4 = b3 & 0x3F;
        
        out.append(charLookup[e1]);
        out.append(charLookup[e2]);
        out.append(b2 < 0 ? '=' : charLookup[e3]);
        out.append(b3 < 0 ? '=' : charLookup[e4]);
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
        if (_start != null)
        {
            if (! CharSequenceUtil.startsWith(str, _start))
                throw new IllegalArgumentException("expected string beginning with \"" + _start + "\"");
            else
                str.delete(0, _start.length());
        }

        if (_end != null)
        {
            if (! CharSequenceUtil.endsWith(str, _end))
                throw new IllegalArgumentException("expected string beginning with \"" + _start + "\"");
            else
                str.delete(str.length() - _end.length(), str.length());
        }

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
        int resultLength = str.length() / 4 * 3;
        if (str.charAt(str.length() - 1) == '=')
            resultLength--;
        if (str.charAt(str.length() - 2) == '=')
            resultLength--;
        
        byte[] bytes = new byte[resultLength];
        int pos = 0;
        for (int off = 0 ; off < str.length() ; off += 4)
        {
            pos += decodeGroup(str, off, bytes, pos);
        }
        return bytes;
    }
    
    
    private static int decodeGroup(CharSequence in, int off, byte[] out, int pos)
    {
        int e1 = valueLookup.get(in.charAt(off));
        int e2 = valueLookup.get(in.charAt(off + 1));
        out[pos] = (byte)((e1 << 2) | ((e2 & 0x30) >> 4));
        
        if (in.charAt(off + 2) == '=') return 1;
        
        int e3 = valueLookup.get(in.charAt(off + 2));
        out[pos + 1] = (byte)(((e2 & 0x0F) << 4) | ((e3 & 0x3C) >> 2));
        
        if (in.charAt(off + 3) == '=') return 2;                
                
        int e4 = valueLookup.get(in.charAt(off + 3));
        out[pos + 2] = (byte)(((e3 & 0x03) << 6) | e4);
        
        return 3;
    }
}
