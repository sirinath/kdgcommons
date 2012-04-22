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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;


/**
 *  Maintains a canonical list of strings, similar to <code>String.intern()</code>
 *  but without the impact on permgen space. Uses weak references to ensure that
 *  strings will be purged when no longer in use, and ensures that strings have a
 *  minimal-size backing array (ie, avoids the "substring refers to huge backing
 *  array" memory leak).
 */
public class StringCanon
{
    private Map<String,WeakReference<String>> _map = new WeakHashMap<String,WeakReference<String>>();


    /**
     *  Interns the string, returning the canonical version. Will never return
     *  the passed string.
     */
    public synchronized String intern(String s)
    {
        WeakReference<String> ref = _map.get(s);
        String s2 = (ref != null) ? ref.get() : null;
        if (s2 == null)
        {
            s2 = new String(s);
            _map.put(s2, new WeakReference<String>(s2));
        }
        return s2;
    }


    /**
     *  Returns the size of the canonicalized string cache. Used for testing
     *  and monitoring.
     */
    public synchronized int size()
    {
        return _map.size();
    }
}
