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

package net.sf.kdgcommons.lang;

import java.lang.reflect.Array;


/**
 *  Static utility methods for working with objects. Typically these use
 *  reflection to do their work.
 */
public class ObjectUtil
{
    /**
     *  Tests two objects for equality. Properly handles <code>null</code>,
     *  and also handles arrays (something that Jakarta <code>ObjectUtils.
     *  equals()</code> does not do).
     */
    public static boolean equals(Object o1, Object o2)
    {
        if (o1 == o2)
            return true;

        if ((o1 == null) && (o2 != null))
            return false;

        if ((o1 != null) && (o2 == null))
            return false;

        if (!o1.getClass().isArray() || !o2.getClass().isArray())
            return o1.equals(o2);

        if (o1.getClass() != o2.getClass())
            return false;

        int s1 = Array.getLength(o1);
        int s2 = Array.getLength(o2);
        if (s1 != s2)
            return false;

        for (int ii = 0 ; ii < s1 ; ii++)
        {
            if (!equals(Array.get(o1, ii), Array.get(o2, ii)))
                return false;
        }
        return true;
    }


    /**
     *  Returns the hashcode of the passed object, 0 if passed <code>null
     *  </code>. This is a direct replacement for Jakarta's <code>ObjectUtils.
     *  hashCode()</code> method, existing simply to eliminate a dependency.
     */
    public static int hashCode(Object obj)
    {
        return (obj == null) ? 0 : obj.hashCode();
    }


    /**
     *  Returns a string value of an object similar to that returned by <code>
     *  Object.toString()</code>. The primary difference is that the numeric
     *  part of the string is its identity hashcode, and it's in decimal (so as
     *  not to be confused with the default <code>toString()</code>).
     *  <p>
     *  This method is null-safe: if passed <code>null</code>, it returns
     *  "null".
     */
    public static String identityToString(Object obj)
    {
        return (obj == null)
               ? "null"
               : obj.getClass().getName() + "@" + System.identityHashCode(obj);
    }
}
