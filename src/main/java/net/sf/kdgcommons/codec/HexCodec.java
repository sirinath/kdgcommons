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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class HexCodec
extends Codec
{
    private final static byte[] EMPTY_ARRAY = new byte[0];

    private int _lineLength;
    private byte[] _separator;

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
     *  <p>
     *  Note: the separator will be encoded using UTF-8.
     */
    public HexCodec(int lineLength, String separator)
    {
        _lineLength = lineLength;
        _separator = StringUtil.toUTF8(separator);
    }

//----------------------------------------------------------------------------
//  Implementation of Codec
//----------------------------------------------------------------------------

    @Override
    public void encode(InputStream in, OutputStream out)
    {
        new Encoder(in, out).encode();
    }


    @Override
    public void decode(InputStream in, OutputStream out)
    {
        new Decoder(in, out).decode();
    }


//----------------------------------------------------------------------------
//  Convenience methods
//----------------------------------------------------------------------------

    /**
     *  Encodes the passed array and returns it as a string.
     */
    public String toString(byte[] data)
    {
        if ((data == null) || (data.length == 0)) return "";
        byte[] encoded = encode(data);
        return StringUtil.fromUTF8(encoded);
    }


    /**
     *  Decodes the passed string and returns it as a byte array.
     */
    public byte[] toBytes(String str)
    {
        if (StringUtil.isEmpty(str)) return EMPTY_ARRAY;

        byte[] bytes = StringUtil.toUTF8(str);
        return decode(bytes);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static char[] nibbleToChar =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };


    private static int charToNibble(int c)
    {
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        else if ((c >= 'A') && (c <= 'F'))
            return c - 'A' + 10;
        else if ((c >= 'a') && (c <= 'f'))
            return c - 'a' + 10;
        else
            throw new InvalidSourceByteException(c);
    }


    private class Encoder
    {
        private InputStream _in;
        private OutputStream _out;
        private int _breakCount;

        public Encoder(InputStream in, OutputStream out)
        {
            _in = in;
            _out = out;
        }

        public void encode()
        {
            try
            {
                int val = 0;
                while ((val = _in.read()) >= 0)
                {
                    insertBreakIfNeeded();
                    _out.write(nibbleToChar[val >> 4]);
                    _out.write(nibbleToChar[val & 0xF]);
                    _breakCount += 2;
                }
            }
            catch (CodecException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw new CodecException("unable to encode", ex);
            }
        }

        private void insertBreakIfNeeded()
        throws IOException
        {
            if ((_separator != null) && (_breakCount >= _lineLength))
            {
                _out.write(_separator);
                _breakCount = 0;
            }
        }
    }


    private class Decoder
    {
        private InputStream _in;
        private OutputStream _out;

        public Decoder(InputStream in, OutputStream out)
        {
            _in = wrapIfNeeded(in, _separator);
            _out = out;
        }

        public void decode()
        {
            try
            {
                while (true)
                {
                    skipIfSeparator(_in, _separator);

                    int v1 = nextNonWhitespace(_in);
                    int v2 = nextNonWhitespace(_in);
                    if ((v1 < 0) || (v2 < 0))
                        return;

                    int n1 = charToNibble(v1);
                    int n2 = charToNibble(v2);
                    _out.write(n1 << 4 | n2);
                }
            }
            catch (CodecException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw new CodecException("unable to decode", ex);
            }
        }
    }
}
