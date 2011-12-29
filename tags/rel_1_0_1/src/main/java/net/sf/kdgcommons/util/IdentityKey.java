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

package net.sf.kdgcommons.util;


/**
 *  A wrapper for a hashed key that uses identity semantics -- because
 *  <code>IdentityHashMap</code> sometimes isn't enough.
 */
public final class IdentityKey
{
    Object _realKey;

    public IdentityKey(Object key)
    {
        _realKey = key;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IdentityKey)
        {
            return _realKey == ((IdentityKey)obj)._realKey;
        }
        return false;

    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(_realKey);
    }
}
