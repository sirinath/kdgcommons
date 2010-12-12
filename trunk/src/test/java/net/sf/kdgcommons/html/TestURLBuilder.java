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

import junit.framework.TestCase;


public class TestURLBuilder extends TestCase
{
    public void testEmptyConstructor() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertEquals("/", builder.toString());
    }


    public void testPathConstructor() throws Exception
    {
        assertEquals("/",
                     new URLBuilder(null).toString());
        assertEquals("/foo",
                     new URLBuilder("/foo").toString());
        assertEquals("http://foo.example.com/bar",
                     new URLBuilder("http://foo.example.com/bar").toString());
    }



    public void testFullConstructor() throws Exception
    {
        assertEquals("/",
                     new URLBuilder(null, null, null).toString());
        assertEquals("http://foo.example.com/",
                     new URLBuilder(null, "foo.example.com", null).toString());
        assertEquals("https://foo.example.com/",
                     new URLBuilder("HTTPS", "foo.example.com", null).toString());
        assertEquals("http://foo.example.com/bar.jsp",
                     new URLBuilder(null, "foo.example.com", "bar.jsp").toString());
        assertEquals("/bar.jsp",
                     new URLBuilder(null, null, "bar.jsp").toString());
    }


    public void testAppendPath() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendPath("bar.jsp"));
        assertEquals("/bar.jsp", builder.toString());
    }


    public void testAppendPathTwice() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendPath("foo"));
        assertSame(builder, builder.appendPath("bar.jsp"));
        assertEquals("/foo/bar.jsp", builder.toString());
    }


    public void testAppendPathReserved() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendPath("f o"));
        assertEquals("/f%20o", builder.toString());
    }


    public void testAppendParameter() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendParameter("foo", "bar"));
        assertEquals("/?foo=bar", builder.toString());
    }


    public void testAppendSecondQueryParameter() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendParameter("foo", "bar"));
        assertSame(builder, builder.appendParameter("argle", "bargle"));
        assertEquals("/?foo=bar&argle=bargle", builder.toString());
    }


    public void testAppendEscapedParameter() throws Exception
    {
        URLBuilder builder = new URLBuilder();
        assertSame(builder, builder.appendParameter("f o", "b/r"));
        assertEquals("/?f%20o=b%2Fr", builder.toString());
    }


    public void testAppendNullParameter() throws Exception
    {
        URLBuilder builder1 = new URLBuilder();
        assertSame(builder1, builder1.appendParameter("foo", null));
        assertEquals("/?foo=", builder1.toString());

        URLBuilder builder2 = new URLBuilder();
        assertSame(builder2, builder2.appendOptionalParameter("foo", null));
        assertEquals("/", builder2.toString());
    }

}
