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

import junit.framework.Assert;


/**
 *  Static assertion methods for numeric values (any data type).
 */
public class NumericAsserts
{
    /**
     *  Asserts that the actual value is within the expected, plus/minus
     *  the specified percentage.
     */
    public static void assertApproximate(int expected, int actual, int deltaPercent)
    {
        int delta = (int)(((long)expected * deltaPercent) / 100);
        int loBound = expected - delta;
        Assert.assertTrue("expected >= " + loBound + ", was " + actual, actual >= loBound);
        int hiBound = expected + delta;
        Assert.assertTrue("expected <= " + hiBound + ", was " + actual, actual <= hiBound);
    }
}
