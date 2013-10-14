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
     *  Returns true of the passed sequence contains the specified sequence,
     *  starting at the given location.
     */
    public static boolean containsAt(CharSequence sb, CharSequence str, int loc)
    {
        if (sb == null) return false;
        if (str == null) return false;

        if (loc + str.length() > sb.length()) return false;

        for (int ii = 0 ; ii < str.length() ; ii++)
        {
            if (sb.charAt(loc + ii) != str.charAt(ii)) return false;
        }

        return true;
    }
}
