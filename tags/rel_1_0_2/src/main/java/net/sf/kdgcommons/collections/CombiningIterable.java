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
import java.util.LinkedList;
import java.util.NoSuchElementException;


/**
 *  Produces an iterator that will iterate over an array of <code>Iterable</code>s.
 *  <p>
 *  The concurrent modification behavior of the iterators produced by this class is
 *  undefined. You should not rely on being able to modify a collection prior to
 *  its place in the sequence of iterators.
 *  <p>
 *  Removal via the produced iterators is dependent on the underlying collection's
 *  iterator. It is possible to mix collections that support removal with those
 *  that do not; the combined iterator will throw for part of the iteration, and
 *  not throw for another part. It's best not to use <code>remove()</code> unless
 *  you know that all underlying collections support it.
 */
public class CombiningIterable<T>
implements Iterable<T>
{
    private Iterable<T>[] _iterables;

    public CombiningIterable(Iterable<T> ... iterables)
    {
        _iterables = iterables;
    }

    public Iterator<T> iterator()
    {
        LinkedList<Iterator<T>> iterators = new LinkedList<Iterator<T>>();
        for (Iterable<T> iterable : _iterables)
            iterators.add(iterable.iterator());
        return new CombiningIterator<T>(iterators);
    }


    /**
     *  Combines a list of iterators into a single iterator. Exposed for those
     *  callers that don't want to stick to <code>Iterable</code>s.
     */
    public static class CombiningIterator<E>
    implements Iterator<E>
    {
        private LinkedList<Iterator<E>> _iterators;
        private Iterator<E> _curItx;

        public CombiningIterator(Iterator<E>... iterators)
        {
            _iterators = new LinkedList<Iterator<E>>();
            for (Iterator<E> itx : iterators)
                _iterators.add(itx);
        }

        public CombiningIterator(LinkedList<Iterator<E>> iterators)
        {
            _iterators = iterators;
        }

        public boolean hasNext()
        {
            if ((_curItx != null) && _curItx.hasNext())
                return true;

            if (_iterators.size() == 0)
                return false;

            _curItx = _iterators.removeFirst();
            return hasNext();

        }

        public E next()
        {
            if (hasNext())
                return _curItx.next();
            else
                throw new NoSuchElementException();
        }

        public void remove()
        {
            if (_curItx != null)
                _curItx.remove();
        }
    }
}
