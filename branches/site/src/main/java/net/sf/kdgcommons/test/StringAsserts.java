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

import java.util.regex.Pattern;

import junit.framework.Assert;


/**
 *  A collection of static methods for making JUnit assertions about strings.
 *  <p>
 *  If using JDK 1.5 or above, you can static import everything in this class,
 *  so that the calls look exactly like built-in JUnit assertions.
 */
public class StringAsserts
{
    /**
     *  Asserts that a given string contains N instances of a substring.
     *
     *  @param  str         The source string.
     *  @param  sub         The substring to search for.
     *  @param  expected    The number of times that the substring should appear
     *                      in the source string.
     */
    public static void assertSubstringCount(String str, String sub, int expected)
    {
        assertSubstringCount("", str, sub, expected);
    }


    /**
     *  Asserts that a given string contains N instances of a substring, and
     *  outputs a specified message if the assertion fails.
     *
     *  @param  message     The message to output if assertion fails -- note
     *                      that the "expected versus actual" message will be
     *                      appended to this.
     *  @param  str         The source string.
     *  @param  sub         The substring to search for.
     *  @param  expected    The number of times that the substring should appear
     *                      in the source string.
     */
    public static void assertSubstringCount(String message, String str, String sub, int expected)
    {
        int actual = 0;
        for (int idx = str.indexOf(sub) ; (idx >= 0) && (idx < str.length()) ; )
        {
            actual += (idx >= 0) ? 1 : 0;
            idx = str.indexOf(sub, idx + 1);
        }
        Assert.assertEquals(message + ": count(" + sub + ")",
                            expected, actual);
    }


    /**
     *  Asserts that the passed string exactly matches a regular expression,
     *  showing expected/actual on failure.
     */
    public static void assertRegex(String regex, String actual)
    {
        assertRegex("expected: " + regex + ", was: " + actual, regex, actual);
    }


    /**
     *  Asserts that the passed string exactly matches a regular expression,
     *  showing caller-defined failure message on failure.
     */
    public static void assertRegex(String message, String regex, String actual)
    {
        Assert.assertTrue(message, Pattern.matches(regex, actual));
    }


    /**
     *  Asserts that the passed string contains a specified regular expression,
     *  showing expected/actual on failure.
     */
    public static void assertContainsRegex(String regex, String str)
    {
        assertContainsRegex("expected: " + regex + ", was: " + str, regex, str);
    }


    /**
     *  Asserts that the passed string contains a specified regular expression,
     *  showing caller-defined failure message on failure.
     */
    public static void assertContainsRegex(String message, String regex, String str)
    {
        Assert.assertTrue(message, Pattern.compile(regex).matcher(str).find());
    }


    /**
     *  Asserts that the passed string does not contain a specified regular
     *  expression, showing expected/actual on failure.
     */
    public static void assertDoesntContainRegex(String regex, String str)
    {
        assertDoesntContainRegex("didn't expact: " + regex + ", was: " + str, regex, str);
    }


    /**
     *  Asserts that the passed string does not contain a specified regular
     *  expresion, showing caller-defined failure message on failure.
     */
    public static void assertDoesntContainRegex(String message, String regex, String str)
    {
        Assert.assertFalse(message, Pattern.compile(regex).matcher(str).find());
    }


    /**
     *  Asserts that a string is contained within another, and then removes
     *  it. This is useful for testing output when you know the expected
     *  content, but do not know the order it appears in.
     *
     *  @param  src         The source string.
     *  @param  expected    The text expected in that string.
     *
     *  @return The source string, with the <em>first</em> occurrence of the
     *          expected text removed.
     */
    public static String assertContainsThenRemove(String src, String expected)
    {
        int idx = src.indexOf(expected);
        if (idx < 0)
            Assert.fail("\"" + expected + "\" not found: " + src);

        int idx2 = idx + expected.length();
        return src.substring(0, idx) + src.substring(idx2);
    }
}
