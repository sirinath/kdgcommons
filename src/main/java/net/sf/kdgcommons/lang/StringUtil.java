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

import java.io.UnsupportedEncodingException;
import java.util.Random;


/**
 *  A collection of static utility methods for working with Strings. All
 *  methods are null-safe; in general they treat <code>null</code> as an
 *  empty string (but see JavaDoc).
 */
public class StringUtil
{
    /**
     *  Returns the length of the passed string, 0 if the string is null.
     *
     *  @since 1.0.12
     */
    public static int length(String str)
    {
        return (str == null) ? 0 : str.length();
    }


    /**
     *  Tests for equality, where null is equivalent to an empty string.
     */
    public static boolean equalOrEmpty(String s1, String s2)
    {
        s1 = (s1 == null) ? "" : s1;
        s2 = (s2 == null) ? "" : s2;
        return s1.equals(s2);
    }


    /**
     *  Returns the last character in the passed string, '\0' if passed
     *  null or an empty string.
     */
    public static char lastChar(String str)
    {
        int index = (str != null) ? str.length() - 1 : -1;
        return (index < 0) ? '\0' : str.charAt(index);
    }


    /**
     *  A null-safe check for empty strings, where <code>null</code> is
     *  considered an empty string.
     */
    public static boolean isEmpty(String str)
    {
        return length(str) == 0;
    }


    /**
     *  A null-safe check for strings that contain only whitespace (if
     *  anything).
     */
    public static boolean isBlank(String str)
    {
        if (str == null)
            str = "";
        for (int ii = 0 ; ii < str.length() ; ii++)
        {
            if (!Character.isWhitespace(str.charAt(ii)))
                return false;
        }
        return true;
    }


    /**
     *  Removes all whitespace characters (per <code>Character.isWhitespace()
     *  </code>) from either side of a string. If passed null, will return an
     *  empty string. Will return the original string if it doesn't need
     *  trimming.
     */
    public static String trim(String str)
    {
        if (str == null)
            return "";
        else if (str.length() == 0)
            return "";
        else if (!Character.isWhitespace(str.charAt(0))
                && !Character.isWhitespace(str.charAt(str.length() - 1)))
            return str;

        StringBuilder sb = new StringBuilder(str);
        while ((sb.length() > 0) && Character.isWhitespace(sb.charAt(0)))
            sb.deleteCharAt(0);
        while ((sb.length() > 0) && Character.isWhitespace(sb.charAt(sb.length()-1)))
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }


    /**
     *  Invokes {@link #trim}, and returns null if the result is an empty string.
     *  Otherwise returns the trimmed string. This is useful to create flag values.
     *
     *  @since 1.0.9
     */
    public static String trimToNull(String str)
    {
        str = trim(str);
        return (str.length() == 0) ? null : str;
    }


    /**
     *  Pads a string that is < N characters by adding the specified character
     *  on the left side. Treats <code>null</code> as a zero-length string (ie,
     *  returned string will just consist of padding).
     */
    public static String padLeft(String str, int len, char c)
    {
        if (str == null)
            str = "";

        if (str.length() >= len)
            return str;

        StringBuilder sb = new StringBuilder(len);
        for (int ii = (len - str.length()) ; ii > 0 ; ii--)
            sb.append(c);

        sb.append(str);
        return sb.toString();
    }


    /**
     *  Pads a string that is < N characters by adding the specified character
     *  on the right side. Treats <code>null</code> as a zero-length string (ie,
     *  returned string will just consist of padding).
     */
    public static String padRight(String str, int len, char c)
    {
        if (str == null)
            str = "";

        if (str.length() >= len)
            return str;

        StringBuilder sb = new StringBuilder(len);
        sb.append(str);
        while (sb.length() < len)
            sb.append(c);
        return sb.toString();
    }


    /**
     *  Determines whether the first string contains the second.
     *  <p>
     *  Returns <code>true</code> if the second string is an empty string
     *  or the two strings are equal. Returns <code>false</code> if either
     *  of the strings are <code>null</code>. Does not care how many times
     *  the second string appears in the first; only that it appears.
     */
    public static boolean contains(String str, String segment)
    {
        if ((str == null) || (segment == null))
            return false;
        if (segment.length() == 0)
            return true;
        return str.indexOf(segment) >= 0;
    }


    /**
     *  Determines whether the first string contains the second, ignoring
     *  case of individual letters.
     *  <p>
     *  Returns <code>true</code> if the second string is an empty string
     *  or the two strings are equal. Returns <code>false</code> if either
     *  of the strings are <code>null</code>. Does not care how many times
     *  the second string appears in the first; only that it appears.
     */
    public static boolean containsIgnoreCase(String str, String segment)
    {
        if ((str == null) || (segment == null))
            return false;
        return contains(str.toUpperCase(), segment.toUpperCase());
    }


    /**
     *  Creates a string that consists of a single character, repeated N times.
     */
    public static String repeat(char c, int count)
    {
        char[] chars = new char[count];
        for (int ii = 0 ; ii < count ; ii++)
            chars[ii] = c;
        return new String(chars);
    }


    /**
     *  Converts the string to a UTF-8 byte array, turning the checked exception
     *  (which should never happen) into a runtime exception.
     *  <p>
     *  If passed <code>null</code>, returns an empty array.
     */
    public static byte[] toUTF8(String str)
    {
        try
        {
            if (str == null)
                return new byte[0];

            return str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 not supported", e);
        }
    }


    /**
     *  Converts the passed byte array to a string, using UTF-8 encoding, and
     *  turning the checked exception (which should never happen) into a runtime
     *  exception.
     *  <p>
     *  If passed <code>null</code>, returns an empty string.
     */
    public static String fromUTF8(byte[] bytes)
    {
        try
        {
            if (bytes == null)
                return "";
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("UTF-8 not supported", e);
        }
    }


    /**
     *  Escapes the passed string, replacing all characters outside the range
     *  32..126, as well as quotes and backslashes, with unicode escapes. This
     *  is useful for strings written by a code generator.
     *  <p>
     *  If passed <code>null</code>, returns <code>null</code>.
     */
    public static String unicodeEscape(String src)
    {
        if (src == null)
            return null;

        StringBuilder sb = new StringBuilder(src);
        for (int ii = 0 ; ii < sb.length() ; ii++)
        {
            char c = sb.charAt(ii);
            if ((c < 32) || (c > 126) || (c == '\\') || (c == '\'') || (c == '\"'))
            {
                sb.deleteCharAt(ii);
                sb.insert(ii, NumberUtil.toHexString(c, 4));
                sb.insert(ii, "\\u");
                ii += 5;   // loop increment will add one more
            }
        }
        return sb.toString();
    }


    /**
     *  Un-escapes the passed string, replacing the standard slash escapes
     *  with their corresponding unicode character value.
     */
    public static String unescape(String src)
    {
        if (src == null)
            return null;

        StringBuilder sb = new StringBuilder(src);
        for (int ii = 0 ; ii < sb.length() ; ii++)
        {
            if (sb.charAt(ii) == '\\')
            {
                sb.deleteCharAt(ii);
                if ((sb.charAt(ii) == 'u') || (sb.charAt(ii) == 'U'))
                {
                    int c = (hex2dec(sb.charAt(ii + 1)) << 12)
                          + (hex2dec(sb.charAt(ii + 2)) << 8)
                          + (hex2dec(sb.charAt(ii + 3)) << 4)
                          + hex2dec(sb.charAt(ii + 4));
                    sb.setCharAt(ii, (char)c);
                    sb.delete(ii + 1, ii + 5);
                }
                else if (sb.charAt(ii) == 'b')
                    sb.setCharAt(ii, '\b');
                else if (sb.charAt(ii) == 't')
                    sb.setCharAt(ii, '\t');
                else if (sb.charAt(ii) == 'n')
                    sb.setCharAt(ii, '\n');
                else if (sb.charAt(ii) == 'f')
                    sb.setCharAt(ii, '\f');
                else if (sb.charAt(ii) == 'r')
                    sb.setCharAt(ii, '\r');
                // FIXME - handle octal escape
            }
        }
        return sb.toString();
    }


    /**
     *  Parses the passed character as a digit in the specified base,
     *  returning its value. Bases > 10 are represented by ASCII letters
     *  in the range A to Z (or a to z). Base 36 is the largest supported.
     *
     *  @return The value, or -1 if the character is not a valid digit
     *          in the specified base (this method will typically be used
     *          in a loop, so no good reason to force exception checking).
     */
    public static int parseDigit(char c, int base)
    {
        int value = -1;
        if ((c >= '0') && (c <= '9'))
            value = c - '0';
        else if ((c >= 'a') && (c <= 'z'))
            value = c - 'a' + 10;
        else if ((c >= 'A') && (c <= 'Z'))
            value = c - 'A' + 10;

        if (value >= base)
            value = -1;
        return value;
    }


    /**
     *  Interns a string using a static instance of {@link StringCanon}.
     *  This is often more useful than creating a task-specific instance.
     */
    public static String intern(String str)
    {
        return _canon.intern(str);
    }


    /**
     *  Generates a random string consisting of characters from the passed
     *  string.
     *
     *  @param  chars       Defines the set of characters used to create the
     *                      returned string.
     *  @param  minLength   Minimum length of the returned string.
     *  @param  maxLength   Maximum length of the returned string.
     */
    public static String randomString(String chars, int minLength, int maxLength)
    {
        StringBuilder sb = new StringBuilder(maxLength);
        int len = minLength + _RNG.nextInt(maxLength - minLength + 1);
        for (int ii = 0 ; ii < len ; ii++)
            sb.append(chars.charAt(_RNG.nextInt(chars.length())));
        return sb.toString();
    }


    /**
     *  Generates a string containing random ASCII alphabetic characters
     *  (A-Za-z).
     *
     *  @param  minLength   Minimum length of the returned string.
     *  @param  maxLength   Maximum length of the returned string.
     */
    public static String randomAlphaString(int minLength, int maxLength)
    {
        return randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
                            minLength, maxLength);
    }


    /**
     *  Returns a substring of the source string, from its start up to but not
     *  including the first occurrence of the target string. If the target does
     *  not exist in the source, returns the source string. If the source or
     *  target strings are null, they are treated as empty strings (and an
     *  empty target will not exist in the source).
     *  <p>
     *  This method is used to divide a string based on an optional delimiter;
     *  for example, dividing "host:port" at ":".
     *  <p>
     *  See also {@link #extractLeftOfLast}.
     *
     *  @since 1.0.6
     */
    public static String extractLeft(String source, String target)
    {
        if (source == null)
            source = "";
        if (target == null)
            target = "";

        if (target.length() == 0)
            return source;

        int idx = source.indexOf(target);
        if (idx < 0)
            return source;

        return source.substring(0, idx);
    }


    /**
     *  Returns a substring of the source string, from immediately after the
     *  first occurrence of the target string, to the end of the source. If the
     *  target does not exist in the source, returns an empty string. If the
     *  source or target strings are null, they are treated as empty strings
     *  (and an empty target will not exist in the source).
     *  <p>
     *  This method is used to divide a string based on an optional delimiter;
     *  for example, dividing "host:port" at ":".
     *  <p>
     *  See also {@link #extractRightOfLast}.
     *
     *  @since 1.0.6
     */
    public static String extractRight(String source, String target)
    {
        if (source == null)
            source = "";
        if (target == null)
            target = "";

        if (target.length() == 0)
            return "";

        int idx = source.indexOf(target);
        if (idx < 0)
            return "";

        return source.substring(idx + target.length());
    }



    /**
     *  Returns a substring of the source string, from its start up to but not
     *  including the last occurrence of the target string. If the target does
     *  not exist in the source, returns the source string. If the source or
     *  target strings are null, they are treated as empty strings (and an
     *  empty target will not exist in the source).
     *  <p>
     *  This method is used to incrementally partition a string, for example,
     *  extracting components from a filepath (eg: "/foo/bar/baz.txt", with a
     *  target of "/" results in "/foo/bar").
     *  <p>
     *  See also {@link #extractLeft}.
     *
     *  @since 1.0.6
     */
    public static String extractLeftOfLast(String source, String target)
    {
        if (source == null)
            source = "";
        if (target == null)
            target = "";

        if (target.length() == 0)
            return source;

        int idx = source.lastIndexOf(target);
        if (idx < 0)
            return source;

        return source.substring(0, idx);
    }


    /**
     *  Returns a substring of the source string, from immediately after the
     *  last occurrence of the target string, to the end of the source. If the
     *  target does not exist in the source, returns an empty string. If the
     *  source or target strings are null, they are treated as empty strings
     *  (and an empty target will not exist in the source).
     *  <p>
     *  This method is used to incrementally partition a string, for example,
     *  extracting components from a filepath (eg: "/foo/bar/baz.txt", with a
     *  target of "/" results in "baz.txt").
     *  <p>
     *  See also {@link #extractRight}.
     *
     *  @since 1.0.6
     */
    public static String extractRightOfLast(String source, String target)
    {
        if (source == null)
            source = "";
        if (target == null)
            target = "";

        if (target.length() == 0)
            return "";

        int idx = source.lastIndexOf(target);
        if (idx < 0)
            return "";

        return source.substring(idx + target.length());
    }


    /**
     *  Returns <code>true</code> if the passed string is equal to one of
     *  the target strings, <code>false</code> otherwise. This will typically
     *  be invoked with a variable <code>str</code> and literal values for
     *  <code>target</code>.
     */
    public static boolean isIn(String str, String... targets)
    {
        for (String target : targets)
        {
            if ((str == null) && (target == null))
                return true;
            else if ((str != null) && str.equals(target))
                return true;
        }
        return false;
    }


    /**
     *  A replacement for <code>String.valueOf()</code> that returns an empty
     *  string for null.
     */
    public static String valueOf(Object obj)
    {
        return (obj == null) ? "" : obj.toString();
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    // used by intern()
    private static StringCanon _canon = new StringCanon();

    // used by randomString()
    private static Random _RNG = new Random(System.currentTimeMillis());


    /**
     *  Returns the numeric value of a hex digit.
     */
    private static int hex2dec(char c)
    {
        if ((c >= '0') && (c <= '9'))
            return (c - '0');
        else if ((c >= 'A') && (c <= 'F'))
            return (c - 'A') + 10;
        else if ((c >= 'a') && (c <= 'f'))
            return (c - 'a') + 10;

        throw new IllegalArgumentException("not a hex digit: " + c);
    }
}
