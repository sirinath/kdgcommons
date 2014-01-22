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
import java.util.HashMap;

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
public class Base64Codec
extends Codec
{
    /**
     *  Different standard construction options: each value specifies a combination of
     *  maximum line length and separator characters.
     */
    public enum Option
    {
        /** Produces an unbroken string of base-64 characters. */
        UNBROKEN(Integer.MAX_VALUE, null, defaultCharLookup, defaultValueLookup, '='),

        /** RFC-1421: 64 characters, CR+LF separator */
        RFC1421(64, new byte[] { 13, 10 }, defaultCharLookup, defaultValueLookup, '='),

        /**
         *  RFC4648 <a href="http://tools.ietf.org/html/rfc4648#section-5">filename</a> format:
         *  an unbroken string using filename-safe symbolic encoding, without pad characters.
         */
        FILENAME(Integer.MAX_VALUE, null, filenameCharLookup, filenameValueLookup, '\0');


        private final int _lineLength;
        private final byte[] _separator;
        private final char[] _charLookup;
        private final HashMap<Character,Integer> _valueLookup;
        private final char _padChar;

        private Option(int lineLength, byte[] separator, char[] charLookup, HashMap<Character,Integer> valueLookup, char padChar)
        {
            _lineLength = lineLength;
            _separator = separator;
            _charLookup = charLookup;
            _valueLookup = valueLookup;
            _padChar = padChar;
        }
    }

//----------------------------------------------------------------------------
//  Encoding Tables
//----------------------------------------------------------------------------

    private static char[] defaultCharLookup =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static HashMap<Character,Integer> defaultValueLookup = new HashMap<Character,Integer>(64);
    static
    {
        defaultValueLookup.put('=', -1);
        for (int ii = 0 ; ii < defaultCharLookup.length ; ii++)
            defaultValueLookup.put(Character.valueOf(defaultCharLookup[ii]), Integer.valueOf(ii));
    }

    private static char[] filenameCharLookup =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    private static HashMap<Character,Integer> filenameValueLookup = new HashMap<Character,Integer>(64);
    static
    {
        filenameValueLookup.put('=', -1);
        for (int ii = 0 ; ii < filenameCharLookup.length ; ii++)
            filenameValueLookup.put(Character.valueOf(filenameCharLookup[ii]), Integer.valueOf(ii));
    }

//----------------------------------------------------------------------------
//  Instance variables and constructor
//----------------------------------------------------------------------------

    private int _lineLength;
    private byte[] _separator;
    private char[] _charLookup;
    private HashMap<Character,Integer> _valueLookup;
    private char _padChar;
    private boolean _paddingRequired;


    /**
     *  Default constructor; equivalent to using {@link #Option.UNBROKEN}.
     */
    public Base64Codec()
    {
        this(Option.UNBROKEN);
    }


    /**
     *  Constructs an instance using standard parameters.
     */
    public Base64Codec(Option option)
    {
        this(option._lineLength, option._separator, option._charLookup, option._valueLookup, option._padChar);
    }


    /**
     *  Creates an instance with custom line length and separator, where
     *  the separator is specified as a string (and converted via UTF-8).
     */
    public Base64Codec(int lineLength, String separator)
    {
        this(lineLength, StringUtil.toUTF8(separator));
    }


    /**
     *  Creates an instance that uses standard encoding, but custom line
     *  length and separator.
     */
    public Base64Codec(int lineLength, byte[] separator)
    {
        this(lineLength, separator, defaultCharLookup, defaultValueLookup, '=');
    }


    /**
     *  Internal constructor that specifies all values, including lookup tables.
     */
    private Base64Codec(int lineLength, byte[] separator, char[] charLookup, HashMap<Character,Integer> valueLookup, char padChar)
    {
        _lineLength = lineLength;
        _separator = separator;
        _charLookup = charLookup;
        _valueLookup = valueLookup;
        _padChar = padChar;
        _paddingRequired = (_padChar != '\0');
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
                while (true)
                {
                    int b1 = _in.read();
                    int b2 = _in.read();
                    int b3 = _in.read();
                    if (b1 < 0) return;

                    insertBreakIfNeeded();
                    encodeGroup(b1, b2, b3);
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

        private boolean encodeGroup(int b1, int b2, int b3)
        throws IOException
        {
            int e1 = b1 >>> 2;

            int e2 = (b1 & 0x03) << 4;
            if (b2 >= 0)
                e2 |= b2 >> 4;

            int e3 = (b2 & 0xF) << 2;
            if (b3 >= 0)
                e3 |= b3 >> 6;

            int e4 = b3 & 0x3F;

            _out.write(_charLookup[e1]);
            _out.write(_charLookup[e2]);
            writeOrPad(b2, e3);
            writeOrPad(b3, e4);

            _breakCount += 4;
            return true;
        }


        private void writeOrPad(int byteVal, int encVal)
        throws IOException
        {
            if (byteVal >= 0)
                _out.write(_charLookup[encVal]);
            else if (_padChar != '\0')
                _out.write(_padChar);
        }

    }


    private class Decoder
    {
        private InputStream _in;
        private OutputStream _out;

        public Decoder(InputStream in, OutputStream out)
        {
            _in = in;
            _out = out;
        }

        public void decode()
        {
            try
            {
                do
                {
                    skipIfSeparator(_in, _separator);
                }
                while (decodeGroup());
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

        private boolean decodeGroup()
        throws IOException
        {
            int e1 = next(true);
            if (e1 < 0) return false;

            int e2 = next(! _paddingRequired);
            int e3 = next(! _paddingRequired);
            int e4 = next(! _paddingRequired);

            _out.write((e1 << 2) | ((e2 & 0x30) >> 4));
            if (e3 < 0) return false;

            _out.write(((e2 & 0x0F) << 4) | ((e3 & 0x3C) >> 2));
            if (e4 < 0) return false;

            _out.write(((e3 & 0x03) << 6) | e4);
            return true;
        }

        private int next(boolean eofAllowed)
        throws IOException
        {
            int b = nextNonWhitespace(_in);
            if (b < 0)
            {
                if (eofAllowed) return -1;
                else throw new CodecException("unexpected EOF");
            }

            if (b == _padChar) return -1;

            Integer val = _valueLookup.get(Character.valueOf((char)b));
            if (val == null) throw new InvalidSourceByteException(b);

            return val.intValue();
        }
    }

}
