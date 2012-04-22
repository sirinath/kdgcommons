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

package net.sf.kdgcommons.buffer;

import java.nio.ByteBuffer;


/**
 *  Holds a source <code>ByteBuffer</code> and makes thread-local copies of it.
 *  <p>
 *  Note that this class only works with <code>ByteBuffer</code>, not the related
 *  primitive buffers. This is because the <code>duplicte()</code> method is
 *  defined on the concrete classes, not the <code>Buffer</code> interface.
 *  <p>
 *  <em>Warning</em>:
 *  Because each thread will have its own <code>ByteBuffer</code> instance, the
 *  mark, position and limit of those buffers will vary independently. Application
 *  code should never assume that one thread's buffer is in the same state as
 *  another (except for contents).
 */
public class ByteBufferThreadLocal
extends ThreadLocal<ByteBuffer>
{
    private ByteBuffer _src;

    public ByteBufferThreadLocal(ByteBuffer src)
    {
        _src = src;
    }

    @Override
    protected synchronized ByteBuffer initialValue()
    {
        return _src.duplicate();
    }
}
