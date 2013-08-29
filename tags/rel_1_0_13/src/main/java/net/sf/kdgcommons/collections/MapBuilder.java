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

import java.util.Map;


/**
 *  A simple utility class that allows maps to be created and populated with a
 *  single expression. It takes the base map as a constructor parameter, and
 *  provides a {@link #put} method that can be chained. When you've added all
 *  that you want, call {@link #toMap} to return the original map.
 *
 *  @since 1.0.5
 */
public class MapBuilder<K,V>
{
    private Map<K,V> _map;


    public MapBuilder(Map<K,V> map)
    {
        _map = map;
    }


    public MapBuilder<K,V> put(K key, V value)
    {
        _map.put(key, value);
        return this;
    }


    public Map<K,V> toMap()
    {
        return _map;
    }
}
