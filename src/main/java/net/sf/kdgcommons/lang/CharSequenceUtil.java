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


/**
 *  Static utility methods for working with arbitrary character sequences.
 */
public class CharSequenceUtil
{
    /**
     *  Returns true of the passed source sequence contains the specified search
     *  sequence starting at the given location. Will return false if either the
     *  source or search sequences are null, or the specified location is outside
     *  of the source sequence (either high or low).
     */
    public static boolean containsAt(CharSequence source, CharSequence search, int loc)
    {
        if (source == null) return false;
        if (search == null) return false;

        if (loc < 0) return false;
        if (loc + search.length() > source.length()) return false;

        for (int ii = 0 ; ii < search.length() ; ii++)
        {
            if (source.charAt(loc + ii) != search.charAt(ii)) return false;
        }

        return true;
    }


    /**
     *  Returns true if the source sequence starts with the search sequence. This is
     *  simply a call to {@link #containsAt} with location 0.
     */
    public static boolean startsWith(CharSequence source, CharSequence search)
    {
        return containsAt(source, search, 0);
    }


    /**
     *  Returns true if the source sequence ends with the search sequence. This is a
     *  call to {@link #containsAt} with calculated location.
     */
    public static boolean endsWith(CharSequence source, CharSequence search)
    {
        if (source == null) return false;
        if (search == null) return false;

        int loc = source.length() - search.length();
        return containsAt(source, search, loc);
    }
}
