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

package net.sf.kdgcommons.html;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;


public class TestHtmlUtil extends TestCase
{
    public void testURLEncodeDecodeNoChange() throws Exception
    {
        assertEquals("foo", HtmlUtil.urlEncode("foo"));

        assertEquals("foo", HtmlUtil.urlDecode("foo"));
    }


    public void testURLEncodeDecodeReservedChar() throws Exception
    {
        assertEquals("f%26o", HtmlUtil.urlEncode("f&o"));

        assertEquals("f&o", HtmlUtil.urlDecode("f%26o"));
    }


    public void testURLEncodeDecodeNonAscii() throws Exception
    {
        assertEquals("F%C2%A2O", HtmlUtil.urlEncode("f\u00A2o").toUpperCase());

        assertEquals("f\u00A2o", HtmlUtil.urlDecode("f%C2%A2o"));
        assertEquals("f\u00A2o", HtmlUtil.urlDecode("f%c2%a2o"));
    }


    public void testURLEncodeDecodeSpace() throws Exception
    {
        assertEquals("f%20o", HtmlUtil.urlEncode("f o"));

        assertEquals("f o", HtmlUtil.urlDecode("f+o"));
        assertEquals("f o", HtmlUtil.urlDecode("f%20o"));
    }


    public void testURLEncodeDecodeNull() throws Exception
    {
        assertEquals("", HtmlUtil.urlEncode(null));

        assertEquals("", HtmlUtil.urlDecode(null));
    }


    public void testEscape() throws Exception
    {
        assertEquals("", HtmlUtil.escape(null));
        assertEquals("foo", HtmlUtil.escape("foo"));
        assertEquals("foo&amp;&lt;&gt;&#39;&quot;bar",
                     HtmlUtil.escape("foo&<>\'\"bar"));
        assertEquals("f&#xf6;o&#x2738;",
                     HtmlUtil.escape("f\u00f6o\u2738"));
    }


    public void testUnescape() throws Exception
    {
        assertEquals("", HtmlUtil.unescape(null));

        assertSame("foo", HtmlUtil.unescape("foo"));

        assertEquals("foo&<>\'\"bar",
                     HtmlUtil.unescape("foo&amp;&lt;&gt;&apos;&quot;bar"));

        assertEquals("fooAbar", HtmlUtil.unescape("foo&#65;bar"));
        assertEquals("fooAbar", HtmlUtil.unescape("foo&#x41;bar"));

        assertEquals("\u00A0", HtmlUtil.unescape("&nbsp;"));

        assertEquals("foo&unknown;bar", HtmlUtil.unescape("foo&unknown;bar"));
        assertEquals("foo&;bar", HtmlUtil.unescape("foo&;bar"));
        assertEquals("foo&bar", HtmlUtil.unescape("foo&bar"));
    }


    public void testAppendAttribute() throws Exception
    {
        StringBuilder buf1 = new StringBuilder();
        HtmlUtil.appendAttribute(buf1, "foo", "bar");
        assertEquals(" foo='bar'", buf1.toString());

        StringBuilder buf2 = new StringBuilder();
        HtmlUtil.appendAttribute(buf2, "foo", "b'a\"r");
        assertEquals(" foo='b&#39;a&quot;r'", buf2.toString());

        StringBuilder buf3 = new StringBuilder();
        HtmlUtil.appendAttribute(buf3, "foo", null);
        assertEquals(" foo=''", buf3.toString());
    }


    public void testAppendOptionalAttribute() throws Exception
    {
        StringBuilder buf1 = new StringBuilder();
        HtmlUtil.appendOptionalAttribute(buf1, "foo", "bar");
        assertEquals(" foo='bar'", buf1.toString());

        StringBuilder buf2 = new StringBuilder();
        HtmlUtil.appendOptionalAttribute(buf2, "foo", "");
        assertEquals("", buf2.toString());

        StringBuilder buf3 = new StringBuilder();
        HtmlUtil.appendOptionalAttribute(buf3, "foo", null);
        assertEquals("", buf3.toString());
    }


    public void testBuildQueryStringZeroParameters() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();

        assertEquals("", HtmlUtil.buildQueryString(params, false));
    }


    public void testBuildQueryStringOneParameter() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");

        assertEquals("argle=bargle", HtmlUtil.buildQueryString(params, false));
    }


    public void testBuildQueryStringTwoParameters() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");
        params.put("foo", "bar");

        assertEquals("argle=bargle&foo=bar", HtmlUtil.buildQueryString(params, false));
    }


    public void testBuildQueryStringIncludeEmpty() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");
        params.put("foo", "");

        assertEquals("argle=bargle&foo=", HtmlUtil.buildQueryString(params, false));
    }


    public void testBuildQueryStringIgnoreEmpty() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");
        params.put("foo", "");

        assertEquals("argle=bargle", HtmlUtil.buildQueryString(params, true));
    }


    public void testBuildQueryStringIncludeNull() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");
        params.put("foo", null);

        assertEquals("argle=bargle&foo=", HtmlUtil.buildQueryString(params, false));
    }


    public void testBuildQueryStringIgnoreNull() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "bargle");
        params.put("foo", null);

        assertEquals("argle=bargle", HtmlUtil.buildQueryString(params, true));
    }


    public void testBuildQueryStringWithEncoding() throws Exception
    {
        Map<String,String> params = new TreeMap<String,String>();
        params.put("argle", "ba/ rgle");

        assertEquals("argle=ba%2F%20rgle", HtmlUtil.buildQueryString(params, true));
    }


    public void testParseQueryStringNull() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString(null, false);
        assertEquals(0, params.size());
    }


    public void testParseQueryStringZeroParameters() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("", false);
        assertEquals(0, params.size());
    }


    public void testParseQueryStringSingleParameter() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("foo=bar", false);
        assertEquals(1, params.size());
        assertEquals("bar", params.get("foo"));
    }


    public void testParseQueryStringTwoParameters() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("foo=bar&argle=bargle", false);
        assertEquals(2, params.size());
        assertEquals("bar", params.get("foo"));
        assertEquals("bargle", params.get("argle"));
    }


    public void testParseQueryStringIncludeEmptyParameter() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("foo=&argle=bargle", false);
        assertEquals(2, params.size());
        assertEquals("", params.get("foo"));
        assertEquals("bargle", params.get("argle"));
    }


    public void testParseQueryStringIgnoreEmptyParameter() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("foo=&argle=bargle", true);
        assertEquals(1, params.size());
        assertEquals("bargle", params.get("argle"));
    }


    public void testParseQueryStringWithEncodedParameter() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("foo=+%2F+&argle=bargle", true);
        assertEquals(2, params.size());
        assertEquals(" / ", params.get("foo"));
        assertEquals("bargle", params.get("argle"));
    }


    public void testParseQueryStringWithLeadingQuestion() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("?foo=bar&argle=bargle", true);
        assertEquals(2, params.size());
        assertEquals("bar", params.get("foo"));
        assertEquals("bargle", params.get("argle"));
    }


    public void testParseQueryStringWithLeadingQuestionAndZeroParams() throws Exception
    {
        Map<String,String> params = HtmlUtil.parseQueryString("?", true);
        assertEquals(0, params.size());
    }


    public void testParseQueryStringFailWithInvalidParam() throws Exception
    {
        try
        {
            HtmlUtil.parseQueryString("foobar", true);
            fail("parsed param that had no =");
        }
        catch (RuntimeException e)
        {
            // success
        }
    }
}
