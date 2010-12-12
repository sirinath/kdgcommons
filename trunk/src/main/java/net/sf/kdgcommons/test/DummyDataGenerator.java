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

import java.util.Random;


/**
 *  Static utility methods for generating test data.
 */
public class DummyDataGenerator
{
    private static Random _RNG = new Random(System.currentTimeMillis());


    /**
     *  Generates a random alpha string (mixed case) that is at least
     *  <code>minLength</code> and no more than <code>maxLength</code>
     *  characters long.
     */
    public static String randomAlpha(int minLength, int maxLength)
    {
        StringBuilder buf = new StringBuilder(maxLength + 1);
        int len = minLength + _RNG.nextInt(maxLength - minLength + 1);
        for (int ii = 0 ; ii < len ; ii++)
        {
            int c = _RNG.nextInt(52);
            buf.append((c < 26) ? (char)(c + 'A') : (char)(c - 26 + 'a'));
        }
        return buf.toString();
    }
}
