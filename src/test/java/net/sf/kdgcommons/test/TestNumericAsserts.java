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

package net.sf.kdgcommons.test;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


public class TestNumericAsserts extends TestCase
{
    public void testAssertApproximateInt() throws Exception
    {
        NumericAsserts.assertApproximate(100, 100, 0);
        NumericAsserts.assertApproximate(100, 101, 1);
        NumericAsserts.assertApproximate(100,  99, 1);

        AssertionFailedError last = null;
        try
        {
            NumericAsserts.assertApproximate(100, 98, 1);
        }
        catch (AssertionFailedError ee)
        {
            last = ee;
        }
        assertNotNull("did not assert for < delta %", last);

        try
        {
            NumericAsserts.assertApproximate(100, 102, 1);
        }
        catch (AssertionFailedError ee)
        {
            last = ee;
        }
        assertNotNull("did not assert for > delta %", last);
    }

}
