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
 *  An immutable parameterized container for two comparable values, which
 *  is itself comparable.
 *
 *  @since 1.0.14
 */
public class ComparableTuple2<A extends Comparable<A>,B extends Comparable<B>>
extends Tuple2<A,B>
implements Comparable<Tuple2<A,B>>
{
    public ComparableTuple2(A val0, B val1)
    {
        super(val0, val1);
    }


//----------------------------------------------------------------------------
//  Implementation of Comparable
//----------------------------------------------------------------------------

    public int compareTo(Tuple2<A,B> that)
    {
        int cmp = ObjectUtil.compare(this.get0(), that.get0());
        if (cmp == 0)
            cmp = ObjectUtil.compare(this.get1(), that.get1());
        return cmp;
    }
}
