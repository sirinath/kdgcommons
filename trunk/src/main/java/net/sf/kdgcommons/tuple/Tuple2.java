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

package net.sf.kdgcommons.tuple;

import net.sf.kdgcommons.lang.ObjectUtil;


/**
 *  An immutable parameterized container for two values.
 *
 *  @since 1.0.14
 */
public class Tuple2<A,B>
{
    private A  _val0;
    private B  _val1;

    public Tuple2(A val0, B val1)
    {
        _val0 = val0;
        _val1 = val1;
    }


//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    public A get0()
    {
        return _val0;
    }


    public B get1()
    {
        return _val1;
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
        if (obj instanceof Tuple2)
        {
            Tuple2<A,B> that = (Tuple2<A,B>)obj;
            return ObjectUtil.equals(this._val0, that._val0)
                   && ObjectUtil.equals(this._val1, that._val1);
        }
        return false;
    }


    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCode(_val0) * 31 + ObjectUtil.hashCode(_val1);
    }


    /**
     *  Returns the string representation of this object, in the form
     *  <code>(A, B)</code>.
     */
    @Override
    public String toString()
    {
        return "(" + _val0 + "," + _val1 + ")";
    }
}
