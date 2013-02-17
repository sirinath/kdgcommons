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

package net.sf.kdgcommons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;


/**
 *  A decorator stream that translates bytes from one character-set encoding to
 *  another.
 */
public class TranslatingInputStream
extends InputStream
{
    private InputStreamReader _delegate;
    private CharsetEncoder _encoder;
    private CharBuffer _charBuf;
    private ByteBuffer _byteBuf;


    /**
     *  Creates an instance that reads bytes from <code>delegate</code>  that
     *  represent characters in the "from" character set, and returning them
     *  to the caller as characters in the "to" character set. Any characters
     *  that cannot be represented in the "to" character set will be ignored.
     */
    public TranslatingInputStream(InputStream delegate, Charset from, Charset to)
    {
        _delegate = new InputStreamReader(delegate, from);
        _encoder = to.newEncoder();
        _charBuf = CharBuffer.allocate(2);
        _byteBuf = ByteBuffer.allocate(4);

        // this will force first call to read() to pull a byte from source
        _byteBuf.limit(0);
    }


    /**
     *  Creates an instance that reads bytes from <code>delegate</code>  that
     *  represent characters in the "from" character set, and returning them
     *  to the caller as characters in the "to" character set. Any characters
     *  that cannot be represented in the "to" character set will be replaced
     *  by <code>replacement</code>
     */
    public TranslatingInputStream(InputStream delegate, Charset from, Charset to, char replacement)
    {
        this(delegate, from, to);
        _encoder.replaceWith(encodeReplacement(to, replacement));
        _encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }


//----------------------------------------------------------------------------
//  InputStream
//----------------------------------------------------------------------------

    @Override
    public int read() throws IOException
    {
        if (_byteBuf.remaining() == 0)
            fillBuffer();

        if (_byteBuf.remaining() == 0)
            return -1;

        return _byteBuf.get() & 0xFF;
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    // creates the byte array representing the replacement character in the
    // "to" character set; we create a new encoder to do this, so that we
    // don't muck with the state of the "real" encoder (in particular, whether
    // it will handle a BOM)
    private static byte[] encodeReplacement(Charset to, char replacement)
    {
        try
        {
            CharsetEncoder tempEncoder = to.newEncoder();
            CharBuffer src = CharBuffer.wrap(new char[] { replacement });
            ByteBuffer dst = tempEncoder.encode(src);
            dst.position(0);
            byte[] result = new byte[dst.remaining()];
            dst.get(result);
            return result;
        }
        catch (CharacterCodingException e)
        {
            throw new IllegalArgumentException("illegal replacement character: " + (int)replacement);
        }
    }


    private void fillBuffer()
    throws IOException
    {
        _byteBuf.clear();
        _charBuf.clear();

        while (_byteBuf.position() == 0)
        {
            fillCharBuf();
            if (_charBuf.limit() == 0)
            {
                _byteBuf.limit(0);
                return;
            }

            _encoder.reset();
            CoderResult rslt = _encoder.encode(_charBuf, _byteBuf, true);
            // FIXME - optionally throw on malformed input
            _encoder.flush(_byteBuf);
        }

        _byteBuf.limit(_byteBuf.position());
        _byteBuf.position(0);
    }


    private void fillCharBuf()
    throws IOException
    {
        int limit = 0;
        int c = 0;
        do
        {
            c = _delegate.read();
            if (c >= 0)
            {
                _charBuf.put((char)c);
                limit++;
            }
        }
        while ((c >= 0xD800) && (c <= 0xDBFF));

        _charBuf.position(0);
        _charBuf.limit(limit);
    }
}
