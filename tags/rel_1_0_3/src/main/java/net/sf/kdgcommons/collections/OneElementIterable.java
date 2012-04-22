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

package net.sf.kdgcommons.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 *  Produces an <code>Iterator</code> for a single value. This is an alternative
 *  to creating a <code>List</code> and adding the value to it.
 */
public class OneElementIterable<T>
implements Iterable<T>
{
    private T _value;


    public OneElementIterable(T value)
    {
        _value = value;
    }


    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            private boolean hasNext = true;

            public boolean hasNext()
            {
                return hasNext;
            }

            public T next()
            {
                if (hasNext)
                {
                    hasNext = false;
                    return _value;
                }
                else
                    throw new NoSuchElementException();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("read-only collection");
            }
        };
    }
}
