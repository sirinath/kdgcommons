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


/**
 *  A collection of static methods for working with subclasses of
 *  <code>Number</code>.
 */
public class NumberUtil
{
    /**
     *  Parses the passed string into an instance of the specified class.
     *  Typically used when converting strings for bean fields.
     *
     *  @throws NumberFormatException if the passed string cannot be parsed
     *          by the specified class.
     */
    public static Number parse(String str, Class<? extends Number> klass)
    {
        if ((klass == Byte.class) || (klass == Byte.TYPE))
            return Byte.valueOf(str);
        else if ((klass == Short.class) || (klass == Short.TYPE))
            return Short.valueOf(str);
        else if ((klass == Integer.class) || (klass == Integer.TYPE))
            return Integer.valueOf(str);
        else if ((klass == Long.class) || (klass == Long.TYPE))
            return Long.valueOf(str);
        else if ((klass == Float.class) || (klass == Float.TYPE))
            return Float.valueOf(str);
        else if ((klass == Double.class) || (klass == Double.TYPE))
            return Double.valueOf(str);
        else if (klass == BigInteger.class)
            return new BigInteger(str);
        else if (klass == BigDecimal.class)
            return new BigDecimal(str);
        else
            throw new IllegalArgumentException("unknown class: " + klass.getName());
    }


    /**
     *  Returns a hexadecimal string representing the passed value, padded
     *  as necessary to the specified number of digits. Padding uses zero for
     *  positive numbers, "F" for negative numbers. For most uses, this is
     *  nicer than the unpadded format of <code>Integer.toHexString()</code>.
     *  <p>
     *  Example: <code>toHex(17, 4)</code> returns "0011", while <code>
     *  toHex(-17, 4)</code> returns "FFEF".
     *  <p>
     *  Hex digits are uppercase; call <code>toLowerCase()</code> on the
     *  returned string if you don't like that.
     *  <p>
     *  The returned value can have as many digits as you like -- you're
     *  not limited to the 16 digits that a long can hold.
     */
    public static String toHexString(long value, int digits)
    {
        StringBuilder sb = new StringBuilder(digits);
        while (digits > 0)
        {
            int nibble = (int)(value & 0xF);
            sb.insert(0, "0123456789ABCDEF".charAt(nibble));
            value >>= 4;
            digits--;
        }
        return sb.toString();
    }


    /**
     *  Attempts to convert the first argument (which must be a subclass of
     *  <code>Number</code>) into an instance of the second (which must be a
     *  primitive wrapper type). If passed <code>null</code>, will return
     *  <code>null</code>. If passed an instance of the desired type, will
     *  return it unchanged.
     *
     *  @throws ClassCastException if the first argument is not a subclass
     *          of <code>Number</code>, or the second is not a supported type.
     */
    public static <T extends Number> T dynamicCast(Object obj, Class<T> klass)
    {
        if (obj == null)
            return null;

        Number src = (Number)obj;   // may throw

        if (obj.getClass() == klass)
            return klass.cast(obj);

        if ((klass == Byte.class) || (klass == Byte.TYPE))
            return klass.cast(Byte.valueOf(src.byteValue()));
        else if ((klass == Short.class) || (klass == Short.TYPE))
            return klass.cast(Short.valueOf(src.byteValue()));
        else if ((klass == Integer.class) || (klass == Integer.TYPE))
            return klass.cast(Integer.valueOf(src.byteValue()));
        else if ((klass == Long.class) || (klass == Long.TYPE))
            return klass.cast(Long.valueOf(src.byteValue()));
        else if ((klass == Float.class) || (klass == Float.TYPE))
            return klass.cast(Float.valueOf(src.byteValue()));
        else if ((klass == Double.class) || (klass == Double.TYPE))
            return klass.cast(Double.valueOf(src.byteValue()));

        throw new ClassCastException("unsupported destination type: " + klass.getName());
    }
}
