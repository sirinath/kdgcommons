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
import java.io.OutputStream;

/**
 *  An <code>OutputStream</code> decorator that silently ignores the {@link #close}
 *  operation, but passes all other operations to its delegate. Useful when you're
 *  producing a stream that is broken into discrete messages: you can produce each
 *  message as if it were a standalone stream.
 *
 *  @since 1.0.14
 */
public class UnclosingOutputStream
extends OutputStream
{
    private OutputStream _delegate;

    public UnclosingOutputStream(OutputStream delegate)
    {
        _delegate = delegate;
    }


    @Override
    public void write(int b) throws IOException
    {
        _delegate.write(b);
    }


    @Override
    public void write(byte[] b) throws IOException
    {
        _delegate.write(b);
    }


    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        _delegate.write(b, off, len);
    }


    @Override
    public void flush() throws IOException
    {
        _delegate.flush();
    }


    @Override
    public void close() throws IOException
    {
        // silently ignore
    }
}
