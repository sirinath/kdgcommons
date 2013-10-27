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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *  Implementations translate an input stream to an output stream according to
 *  specific rules. As a convenience, instances also provide a conversion between
 *  byte arrays, and may provide conversions to/from strings.
 *  <p>
 *  Callers should not make any assumptions about stream buffering, and must close
 *  the streams (if appropriate) after encoding/decoding.
 *  <p>
 *  Any method may throw {@link CodecException}.
 *  <p>
 *  Instances are thread-safe and reentrant.
 *
 *  @since 1.0.14
 */
public abstract class Codec
{
    /**
     *  Encodes a stream according to the rules of the codec.
     */
    public abstract void encode(InputStream in, OutputStream out);


    /**
     *  Decodes a stream according to the rules of the codec.
     *
     *  @throws IllegalArgumentException if the input stream is not encoded correctly.
     */
    public abstract void decode(InputStream in, OutputStream out);


    /**
     *  Convenience method that encodes the passed array.
     */
    public byte[] encode(byte[] src)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(src);
        ByteArrayOutputStream out = new ByteArrayOutputStream(src.length);
        encode(in, out);
        return out.toByteArray();
    }


    /**
     *  Convenience method that decodes the passed array.
     */
    public byte[] decode(byte[] src)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(src);
        ByteArrayOutputStream out = new ByteArrayOutputStream(src.length);
        decode(in, out);
        return out.toByteArray();
    }


//----------------------------------------------------------------------------
//  Helper methods for subclasses
//----------------------------------------------------------------------------

    /**
     *  Optionally wraps the passed input stream to allow backtracking with
     *  the passed separator.
     */
    protected static InputStream wrapIfNeeded(InputStream in, byte[] separator)
    {
        return ((separator == null) || (separator.length == 0))
             ? in
             : new BufferedInputStream(in, separator.length + 2);
    }


    /**
     *  Checks the content of the stream against the separator, and skips
     *  the separator if they match. Note: requires a wrapped stream.
     */
    protected static void skipIfSeparator(InputStream in, byte[] separator)
    throws IOException
    {
        if (separator == null) return;

        in.mark(separator.length + 1);
        for (int ii = 0 ; ii < separator.length ; ii++)
        {
            int c = in.read();
            if (c != separator[ii])
            {
                in.reset();
                return;
            }
        }
    }


    /**
     *  Returns the next non-whitespace character from the input stream, -1 if at EOF.
     */
    protected static int nextNonWhitespace(InputStream in)
    throws IOException
    {
        do
        {
            int c = in.read();
            if (c < 0) return c;
            if (! Character.isWhitespace(c)) return c;
        }
        while (true);
    }
}
