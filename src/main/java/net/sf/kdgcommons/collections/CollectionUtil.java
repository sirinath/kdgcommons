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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;

/**
 *  Static utility methods for working with collections -- particularly
 *  parameterized collections.
 */
public class CollectionUtil
{
    /**
     *  Returns a set containing the passed elements.
     */
    public static <T> Set<T> asSet(T... elems)
    {
        Set<T> result = new HashSet<T>();
        for (T elem : elems)
            result.add(elem);
        return result;
    }


    /**
     *  Appends an arbitrary number of explicit elements to an existing collection.
     *  Primarily useful when writing testcases.
     */
    public static <T> void addAll(Collection<T> coll, T... elems)
    {
        for (T elem : elems)
            coll.add(elem);
    }


    /**
     *  Appends the values returned by an iterator to the passed collection.
     */
    public static <T> void addAll(Collection<T> coll, Iterator<T> src)
    {
        while (src.hasNext())
            coll.add(src.next());
    }


    /**
     *  Appends the contents of an iterable object to the passed collection.
     */
    public static <T> void addAll(Collection<T> coll, Iterable<T> src)
    {
        addAll(coll, src.iterator());
    }


    /**
     *  Verifies that the passed list contains only elements of the given
     *  type, and returns it as a parameterized type. Throws if any element
     *  is a different type.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<?> list, Class<T> klass)
    {
        for (Object obj : list)
        {
            klass.cast(obj);
        }
        return (List<T>)list;
    }


    /**
     *  Verifies that the passed set contains only elements of the given
     *  type, and returns it as a parameterized type. Throws if any element
     *  is a different type.
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> cast(Set<?> set, Class<T> klass)
    {
        for (Object obj : set)
        {
            klass.cast(obj);
        }
        return (Set<T>)set;
    }


    /**
     *  Resizes the passed list to N entries. Will add the specified object if
     *  the list is smaller than the desired size, discard trailing entries if
     *  larger. This is primarily used to presize a list that will be accessed
     *  by index, but it may also be used to truncate a list for display.
     *
     *  @return The list, as a convenience for callers
     *
     *  @throws UnsupportedOperationException if the list does not implement
     *          <code>RandomAccess</code> and its list iterator does not
     *          support the <code>remove()</code> operation
     */
    public static <T> List<T> resize(List<T> list, int newSize, T obj)
    {
        if (list instanceof ArrayList)
            ((ArrayList<T>)list).ensureCapacity(newSize);

        if (list.size() < newSize)
        {
            for (int ii = list.size() ; ii < newSize ; ii++)
                list.add(obj);
        }
        else if (list.size() > newSize)
        {
            if (list instanceof RandomAccess)
            {
                for (int ii = list.size() - 1 ; ii >= newSize ; ii--)
                    list.remove(ii);
            }
            else
            {
                ListIterator<T> itx = list.listIterator(newSize);
                while (itx.hasNext())
                {
                    itx.next();
                    itx.remove();
                }
            }
        }

        return list;
    }


    /**
     *  Resizes the passed list to N entries. Will add nulls if the list is
     *  smaller than the desired size, discard trailing entries if larger.
     *  This is primarily used to presize a list that will be accessed by
     *  index, but it may also be used to truncate a list for display.
     *
     *  @return The list, as a convenience for callers
     *  @throws UnsupportedOperationException if the list does not implement
     *          <code>RandomAccess</code> and its list iterator does not
     *          support the <code>remove()</code> operation
     */
    public static <T> List<T> resize(List<T> list, int newSize)
    {
        return resize(list, newSize, null);
    }


    /**
     *  Iterates the passed collection, converts its elements to strings, then
     *  concatenates those strings with the specified delimiter between them.
     *  Nulls are converted to empty strings.
     */
    public static <T> String join(Iterable<T> coll, String delim)
    {
        if (coll == null)
            return "";

        boolean isFirst = true;
        StringBuilder buf = new StringBuilder(1024);
        for (T item : coll)
        {
            if (isFirst)
                isFirst = false;
            else
                buf.append(delim);

            if (item != null)
                buf.append(String.valueOf(item));
        }
        return buf.toString();
    }
}
