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

import junit.framework.TestCase;


public class TestDummyDataGenerator extends TestCase
{
    public void testRandomAlpha() throws Exception
    {
        final int reps = 1000;
        final int minLength = 3;
        final int maxLength = 6;

        String[] strings = new String[reps];
        for (int ii = 0 ; ii < strings.length ; ii++)
            strings[ii] = DummyDataGenerator.randomAlpha(minLength, maxLength);

        int[] lengthCounts = new int[maxLength + 1];
        int[] charCounts = new int[256];
        int totChars = 0;
        for (int ii = 0 ; ii < strings.length ; ii++)
        {
            int len = strings[ii].length();
            lengthCounts[len]++;
            for (int jj = 0 ; jj < len ; jj++)
            {
                charCounts[strings[ii].charAt(jj)]++;
                totChars++;
            }
        }

        int distByLength = reps / (maxLength - minLength + 1);
        for (int len = minLength ; len < maxLength ; len++)
        {
            NumericAsserts.assertApproximate(distByLength, lengthCounts[len], 30);
        }

        for (int c = 0 ; c < 256 ; c++)
        {
            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')))
            {
                assertTrue("alpha char with 0 count", charCounts[c] > 0);
            }
            else
            {
                assertEquals("non-alpha char with + count", 0, charCounts[c]);
            }
        }
    }

}
