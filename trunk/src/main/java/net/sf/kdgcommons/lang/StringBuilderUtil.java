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
 *  A collection of static methods for manipulating StringBuilders. These
 *  methods all return their builder, but beware execution order if you
 *  try to chain operations.
 */
public class StringBuilderUtil
{
    /**
     *  Appends N copies of the same character to the end of a builder.
     */
    public static StringBuilder appendRepeat(StringBuilder buf, char c, int count)
    {
        buf.ensureCapacity(buf.length() + count);
        for (int ii = 0 ; ii < count ; ii++)
            buf.append(c);
        return buf;
    }


    /**
     *  Converts the passed value into hexadecimal representation, and appends
     *  that representation to the buffer, left-zero-padded.
     *
     *  @param buf      The buffer to be updated.
     * @param value    The value to be converted.
     * @param width    Size of the field that will hold the value. If the hex
     *                  representation of the value is smaller, it will be
     *                  left-zero-padded; if greater, the high-order bits will
     *                  be truncated.
     */
    public static StringBuilder appendHex(StringBuilder buf, int value, int width)
    {
        appendRepeat(buf, '0', width);
        int offset = buf.length();
        for (int ii = 1 ; ii <= width ; ii++)
        {
            int nibble = value & 0xF;
            buf.setCharAt(offset - ii, NIBBLES[nibble]);
            value >>>= 4;
        }

        return buf;
    }


    /**
     *  Returns the last character in the passed <code>StringBuilder</code>,
     *  '\0' if passed <code>null</code> or the builder's length is 0.
     */
    public static char lastChar(StringBuilder buf)
    {
        int index = (buf != null) ? buf.length() - 1 : -1;
        return (index < 0) ? '\0' : buf.charAt(index);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    // used by appendHex()
    private final static char[] NIBBLES
            = new char[]
            {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
            };

}
