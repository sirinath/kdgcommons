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

import java.math.BigDecimal;
import java.math.BigInteger;


import junit.framework.TestCase;

public class TestNumberUtil extends TestCase
{

    public void testParse() throws Exception
    {
        String str = "123";
        Number[] values = new Number[]
        {
            // failure == ClassCastException
            (Byte)NumberUtil.parse(str, Byte.class),
            (Short)NumberUtil.parse(str, Short.class),
            (Integer)NumberUtil.parse(str, Integer.class),
            (Long)NumberUtil.parse(str, Long.class),
            (Float)NumberUtil.parse(str, Float.class),
            (Double)NumberUtil.parse(str, Double.class),
            (BigInteger)NumberUtil.parse(str, BigInteger.class),
            (BigDecimal)NumberUtil.parse(str, BigDecimal.class),

            (Byte)NumberUtil.parse(str, Byte.TYPE),
            (Short)NumberUtil.parse(str, Short.TYPE),
            (Integer)NumberUtil.parse(str, Integer.TYPE),
            (Long)NumberUtil.parse(str, Long.TYPE),
            (Float)NumberUtil.parse(str, Float.TYPE),
            (Double)NumberUtil.parse(str, Double.TYPE)
        };

        for (Number value : values)
        {
            assertEquals(value.getClass().getName(), 123, value.intValue());
        }
    }


    public void testParseUnhandledType() throws Exception
    {
        try
        {
            // Number will stand in for some user-defined subclass
            NumberUtil.parse("123", Number.class);
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message: \"" + ex.getMessage() + "\"", ex.getMessage().contains("java.lang.Number"));
        }
    }


    public void testToHexString() throws Exception
    {
        assertEquals("11", NumberUtil.toHexString(17, 2));
        assertEquals("00000011", NumberUtil.toHexString(17, 8));

        assertEquals("EF", NumberUtil.toHexString(-17, 2));
        assertEquals("FFFFFFEF", NumberUtil.toHexString(-17, 8));

        assertEquals("000000000000000000000000", NumberUtil.toHexString(0, 24));
        assertEquals("FFFFFFFFFFFFFFFFFFFFFFFF", NumberUtil.toHexString(-1, 24));

        assertEquals("1", NumberUtil.toHexString(17, 1));
        assertEquals("F", NumberUtil.toHexString(-17, 1));
    }


    // FIXME - this should test all combinations, and needs to test wrapper types (which currently fail)
    public void testDynamicCast() throws Exception
    {
        Object src = new Integer(123);

        Long dst1 = NumberUtil.dynamicCast(src, Long.class);
        assertEquals(123, dst1.longValue());

        Integer dst2 = NumberUtil.dynamicCast(src, Integer.class);
        assertSame(src, dst2);

        Short dst3 = NumberUtil.dynamicCast(src, Short.class);
        assertEquals(123, dst3.shortValue());

        Byte dst4 = NumberUtil.dynamicCast(src, Byte.class);
        assertEquals(123, dst4.byteValue());

        Double dst5 = NumberUtil.dynamicCast(src, Double.class);
        assertEquals(123.0, dst5.doubleValue(), .00000000000001);

        Float dst6 = NumberUtil.dynamicCast(src, Float.class);
        assertEquals(123.0f, dst6.doubleValue(), .000001);

        try
        {
            NumberUtil.dynamicCast("123.45", Float.class);
            fail("able to cast a non-number");
        }
        catch (ClassCastException ee)
        {
            // success
        }

        try
        {
            NumberUtil.dynamicCast(src, BigInteger.class);
            fail("able to cast to an unsupported destination type");
        }
        catch (ClassCastException ee)
        {
            // success
        }
    }
}
