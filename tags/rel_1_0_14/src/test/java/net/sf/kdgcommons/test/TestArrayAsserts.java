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


public class TestArrayAsserts extends TestCase
{
    public void testByteArraysSuccess() throws Exception
    {
        byte[] a1 = new byte[] { 1, 2, 3, 4 };
        byte[] a2 = new byte[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testByteArraysWithNull() throws Exception
    {
        byte[] a1 = new byte[] { 1, 2, 3 };
        byte[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testByteArraysDifferentLengths() throws Exception
    {
        byte[] a1 = new byte[] { 1, 2, 3 };
        byte[] a2 = new byte[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testByteArraysDifferentContent() throws Exception
    {
        byte[] a1 = new byte[] { 9, 8, 7, 6 };
        byte[] a2 = new byte[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testShortArraysSuccess() throws Exception
    {
        short[] a1 = new short[] { 1, 2, 3, 4 };
        short[] a2 = new short[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testShortArraysWithNull() throws Exception
    {
        short[] a1 = new short[] { 1, 2, 3 };
        short[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testShortArraysDifferentLengths() throws Exception
    {
        short[] a1 = new short[] { 1, 2, 3 };
        short[] a2 = new short[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testShortArraysDifferentContent() throws Exception
    {
        short[] a1 = new short[] { 9, 8, 7, 6 };
        short[] a2 = new short[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testIntArraysSuccess() throws Exception
    {
        int[] a1 = new int[] { 1, 2, 3, 4 };
        int[] a2 = new int[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testIntArraysWithNull() throws Exception
    {
        int[] a1 = new int[] { 1, 2, 3 };
        int[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testIntArraysDifferentLengths() throws Exception
    {
        int[] a1 = new int[] { 1, 2, 3 };
        int[] a2 = new int[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testIntArraysDifferentContent() throws Exception
    {
        int[] a1 = new int[] { 9, 8, 7, 6 };
        int[] a2 = new int[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testLongArraysSuccess() throws Exception
    {
        long[] a1 = new long[] { 1, 2, 3, 4 };
        long[] a2 = new long[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testLongArraysWithNull() throws Exception
    {
        long[] a1 = new long[] { 1, 2, 3 };
        long[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testLongArraysDifferentLengths() throws Exception
    {
        long[] a1 = new long[] { 1, 2, 3 };
        long[] a2 = new long[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testLongArraysDifferentContent() throws Exception
    {
        long[] a1 = new long[] { 9, 8, 7, 6 };
        long[] a2 = new long[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testFloatArraysSuccess() throws Exception
    {
        float[] a1 = new float[] { 1, 2, 3, 4 };
        float[] a2 = new float[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testFloatArraysWithNull() throws Exception
    {
        float[] a1 = new float[] { 1, 2, 3 };
        float[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testFloatArraysDifferentLengths() throws Exception
    {
        float[] a1 = new float[] { 1, 2, 3 };
        float[] a2 = new float[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testFloatArraysDifferentContent() throws Exception
    {
        float[] a1 = new float[] { 9, 8, 7, 6 };
        float[] a2 = new float[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testDoubleArraysSuccess() throws Exception
    {
        double[] a1 = new double[] { 1, 2, 3, 4 };
        double[] a2 = new double[] { 1, 2, 3, 4 };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testDoubleArraysWithNull() throws Exception
    {
        double[] a1 = new double[] { 1, 2, 3 };
        double[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testDoubleArraysDifferentLengths() throws Exception
    {
        double[] a1 = new double[] { 1, 2, 3 };
        double[] a2 = new double[] { 1, 2, 3, 4 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testDoubleArraysDifferentContent() throws Exception
    {
        double[] a1 = new double[] { 9, 8, 7, 6 };
        double[] a2 = new double[] { 9, 8, 6, 7 };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*7.*was.*6", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testCharArraysSuccess() throws Exception
    {
        char[] a1 = new char[] { 'A', 'B', 'C', 'D' };
        char[] a2 = new char[] { 'A', 'B', 'C', 'D' };

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testCharArraysWithNull() throws Exception
    {
        char[] a1 = new char[] { 'A', 'B', 'C', 'D' };
        char[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testCharArraysDifferentLengths() throws Exception
    {
        char[] a1 = new char[] { 'A', 'B', 'C' };
        char[] a2 = new char[] { 'A', 'B', 'C', 'D' };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testCharArraysDifferentContent() throws Exception
    {
        char[] a1 = new char[] { 'A', 'B', 'C', 'D' };
        char[] a2 = new char[] { 'A', 'B', 'D', 'C' };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*C.*was.*D", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testBooleanArraysSuccess() throws Exception
    {
        boolean[] a1 = new boolean[] { true, true, false, true };
        boolean[] a2 = new boolean[] { true, true, false, true};

        ArrayAsserts.assertEquals(a1, a2);
    }


    public void testBooleanArraysWithNull() throws Exception
    {
        boolean[] a1 = new boolean[] { true, true, false, true };
        boolean[] a2 = null;

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("is null"));
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testBooleanArraysDifferentLengths() throws Exception
    {
        boolean[] a1 = new boolean[] { true, true, false };
        boolean[] a2 = new boolean[] { true, true, false, true };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            assertTrue("assertion message described problem", msg.contains("arrays have different size"));
            StringAsserts.assertContainsRegex("assertion message gave expected and actual sizes",
                                              "expected.*3.*was.*4", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }


    public void testBooleanArraysDifferentContent() throws Exception
    {
        boolean[] a1 = new boolean[] { true, true, false, true };
        boolean[] a2 = new boolean[] { true, true, true, false };

        try
        {
            ArrayAsserts.assertEquals("message", a1, a2);
        }
        catch (AssertionFailedError e)
        {
            String msg = e.getMessage();
            assertTrue("assertion message contained user message", msg.contains("message:"));
            StringAsserts.assertContainsRegex("assertion message described problem",
                                              "differ.*element.*2", msg);
            StringAsserts.assertContainsRegex("assertion message gave expected and actual content",
                                              "expected.*false.*was.*true", msg);
            return;
        }

        fail("assertion passed when it shouldn't");
    }
}
