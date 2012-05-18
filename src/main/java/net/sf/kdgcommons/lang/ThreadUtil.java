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


/**
 *  Static utility methods for working with threads.
 *
 *  @since 1.0.5
 */
public class ThreadUtil
{
    /**
     *  Sleeps for the specified number of milliseconds, catching and ignoring
     *  any <code>InterruptedException</code>. Returns the number of milliseconds
     *  actually slept.
     *
     *  @since 1.0.5
     */
    public static long sleepQuietly(long millis)
    {
        long start = System.currentTimeMillis();
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException ignored)
        {
            // ignored
        }
        return System.currentTimeMillis() - start;
    }
}
