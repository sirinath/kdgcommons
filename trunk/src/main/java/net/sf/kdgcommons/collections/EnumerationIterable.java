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

import java.util.Enumeration;
import java.util.Iterator;

/**
 *  Wrapper for an <code>Enumeration</code> that allows it to be used in for-each
 *  loops. This is useful when working with objects that return an iterator rather
 *  than a collection in order to protect access to their state.
 *  <p>
 *  Note: this class does not allow re-iteration of the enumeration. Calling {@link
 *  #iterator} multiple times will return iterators that refer to the same source
 *  enumeration.
 *
 *  @since 1.0.9
 */
public class EnumerationIterable<T>
implements Iterable<T>
{
    private Enumeration<T> _enumeration;


    public EnumerationIterable(Enumeration<T> enumeration)
    {
        _enumeration = enumeration;
    }


    public Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            public boolean hasNext()
            {
                return _enumeration.hasMoreElements();
            }

            public T next()
            {
                return _enumeration.nextElement();
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Enumeration does not support removal");
            }
        };
    }
}
