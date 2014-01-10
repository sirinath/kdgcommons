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


/**
 *  An <code>InputStream</code> decorator that silently ignores the {@link #close}
 *  operation, but passes all other operations to its delegate. Useful when you're
 *  processing a stream that is broken into discrete messages: you can process
 *  each message as if it were a standalone stream.
 *
 *  @since 1.0.14
 */
public class UnclosingInputStream
extends InputStream
{
    private InputStream _delegate;

    public UnclosingInputStream(InputStream delegate)
    {
        _delegate = delegate;
    }


    @Override
    public int read() throws IOException
    {
        return _delegate.read();
    }


    @Override
    public int read(byte[] b) throws IOException
    {
        return _delegate.read(b);
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return _delegate.read(b, off, len);
    }


    @Override
    public long skip(long n) throws IOException
    {
        return _delegate.skip(n);
    }


    @Override
    public int available() throws IOException
    {
        return _delegate.available();
    }

    @Override
    public void close() throws IOException
    {
        // silently ignore
    }


    @Override
    public synchronized void mark(int readlimit)
    {
        _delegate.mark(readlimit);
    }


    @Override
    public synchronized void reset() throws IOException
    {
        _delegate.reset();
    }


    @Override
    public boolean markSupported()
    {
        return _delegate.markSupported();
    }

}
