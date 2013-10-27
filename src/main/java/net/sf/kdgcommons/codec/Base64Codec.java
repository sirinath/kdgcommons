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
    public enum Option
    {
        /** Produces an unbroken string of base-64 characters. */
        UNBROKEN
    }


    private final static byte[] EMPTY_ARRAY = new byte[0];

    private int _lineLength;
    private String _separator;


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
                do
                {
//                    insertBreakIfNeeded();
                }
                while (encodeGroup());
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

//        private void insertBreakIfNeeded()
//        throws IOException
//        {
//            if (_breakCount >= _lineLength)
//            {
//                _out.write(_separator);
//                _breakCount = 0;
//            }
//        }

        private boolean encodeGroup()
        throws IOException
        {
            int b1 = _in.read();
            int b2 = _in.read();
            int b3 = _in.read();

            if (b1 < 0) return false;

            int e1 = b1 >>> 2;

            int e2 = (b1 & 0x03) << 4;
            if (b2 >= 0)
                e2 |= b2 >> 4;

            int e3 = (b2 & 0xF) << 2;
            if (b3 >= 0)
                e3 |= b3 >> 6;

            int e4 = b3 & 0x3F;

            _out.write(charLookup[e1]);
            _out.write(charLookup[e2]);
            _out.write(b2 < 0 ? '=' : charLookup[e3]);
            _out.write(b3 < 0 ? '=' : charLookup[e4]);

            _breakCount += 4;
            return true;
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
//                    skipIfSeparator();
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
            int e1 = next();
            int e2 = next();
            int e3 = next();
            int e4 = next();

            if ((e1 < 0) || (e2 < 0)) return false;

            _out.write((e1 << 2) | ((e2 & 0x30) >> 4));
            if (e3 < 0) return false;

            _out.write(((e2 & 0x0F) << 4) | ((e3 & 0x3C) >> 2));
            if (e4 < 0) return false;

            _out.write(((e3 & 0x03) << 6) | e4);
            return true;
        }

        private int next()
        throws IOException
        {
            int b = nextNonWhitespace(_in);
            if (b < 0) return b;

            if (b == '=') return -1;

            Integer val = valueLookup.get(Character.valueOf((char)b));
            if (val == null) throw new InvalidSourceByteException(b);

            return val.intValue();
        }
    }

}
