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

import java.io.Serializable;

import net.sf.kdgcommons.lang.ObjectUtil;


/**
 *  An immutable 2-tuple that associates a name with a value. This is
 *  particularly useful for programs that perform database operations,
 *  as a way of managing the data coming back from JDBC.
 */
public class NameValue<T>
implements Comparable<NameValue<T>>, Serializable
{
    private static final long serialVersionUID = 1L;

    private String  _name;
    private T  _value;


    public NameValue(String name, T value)
    {
        _name = name;
        _value = value;
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    public String getName()
    {
        return _name;
    }


    public T getValue()
    {
        return _value;
    }


//----------------------------------------------------------------------------
//  Overrides of Object
//----------------------------------------------------------------------------

    /**
     *  Two <code>NameValue</code> instances are considered equal if both
     *  name and value components are equal.
     */
    @Override
    public final boolean equals(Object obj)
    {
        if (obj instanceof NameValue)
        {
            NameValue<T> that = (NameValue<T>)obj;
            return ObjectUtil.equals(_name, that._name)
                   && ObjectUtil.equals(_value, that._value);
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCode(_name) * 31 + ObjectUtil.hashCode(_value);
    }


    /**
     *  Returns the string representation of this object, in the form
     *  <code>[NAME=VALUE]</code>.
     */
    @Override
    public String toString()
    {
        return "[" + _name + "=" + String.valueOf(_value) + "]";
    }


//----------------------------------------------------------------------------
//  Implementation of Comparable
//----------------------------------------------------------------------------

    /**
     *  Compares two <CODE>NameValue</CODE> instances. Instances are ordered
     *  by name first. If two instances have the same name, then the value is
     *  examined. If the value implements Comparable, this is straightforward;
     *  if not, the values are converted to strings and then compared.
     */
    public int compareTo(NameValue<T> that)
    {
        int cmp = _name.compareTo(that._name);
        if (cmp != 0)
            return cmp;

        if (_value instanceof Comparable)
            return ((Comparable<T>)_value).compareTo(that._value);

        if (ObjectUtil.equals(_value, that._value))
            return 0;

        return (String.valueOf(_value).compareTo(String.valueOf(that._value)));
    }
}
