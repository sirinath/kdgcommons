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
 *  Static methods for asserting array equality. Useful for primitive arrays (where
 *  <code>Arrays.asList()</code> returns a list of arrays), and for large object
 *  arrays (where the output from <code>Arrays.asList</code> would be confusing).
 *  <p>
 *  Performs the following assertions:
 *  <ul>
 *  <li> actual array is not null
 *  <li> arrays have different sizes (with expected and actual)
 *  <li> arrays differ at element X (expected Y, was Z)
 *  </ul>
 */
public class ArrayAsserts
{
    /**
     *  Compares two byte arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, byte[] expected, byte[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two byte arrays, with default message.
     */
    public static void assertEquals(byte[] expected, byte[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two short arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, short[] expected, short[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two short arrays, with default message.
     */
    public static void assertEquals(short[] expected, short[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two int arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, int[] expected, int[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two int arrays, with default message.
     */
    public static void assertEquals(int[] expected, int[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two long arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, long[] expected, long[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two long arrays, with default message.
     */
    public static void assertEquals(long[] expected, long[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two float arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, float[] expected, float[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two float arrays, with default message.
     */
    public static void assertEquals(float[] expected, float[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two double arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, double[] expected, double[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two double arrays, with default message.
     */
    public static void assertEquals(double[] expected, double[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two char arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, char[] expected, char[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two char arrays, with default message.
     */
    public static void assertEquals(char[] expected, char[] actual)
    {
        assertEquals("", expected, actual);
    }


    /**
     *  Compares two boolean arrays, with user-defined message prefix.
     */
    public static void assertEquals(String message, boolean[] expected, boolean[] actual)
    {
        Assert.assertNotNull(message + ": actual array is null", actual);
        Assert.assertEquals(message + ": arrays have different size;", expected.length, actual.length);
        for (int ii = 0 ; ii < expected.length ; ii++)
        {
            Assert.assertEquals(message + ": arrays differ at element " + ii + ";", expected[ii], actual[ii]);
        }
    }


    /**
     *  Compares two boolean arrays, with default message.
     */
    public static void assertEquals(boolean[] expected, boolean[] actual)
    {
        assertEquals("", expected, actual);
    }
}
