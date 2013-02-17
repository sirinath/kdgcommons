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

import org.w3c.dom.Document;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


public class TestSimpleMock extends TestCase
{
    public void testSingleCallNoArgs() throws Exception
    {
        SimpleMock mock = new SimpleMock();
        Document mocked = mock.getInstance(Document.class);
        assertNull(mocked.getOwnerDocument());

        mock.assertCallCount(1);
        mock.assertCall(0, "getOwnerDocument");
    }


    public void testSingleCallWithArgs() throws Exception
    {
        SimpleMock mock = new SimpleMock();
        Document mocked = mock.getInstance(Document.class);
        assertNull(mocked.createAttributeNS("foo", "bar"));

        mock.assertCallCount(1);
        mock.assertCall(0, "createAttributeNS", "foo", "bar");
    }


    public void testMultipleCalls() throws Exception
    {
        SimpleMock mock = new SimpleMock();
        Document mocked = mock.getInstance(Document.class);
        assertNull(mocked.getOwnerDocument());
        assertNull(mocked.createAttributeNS("foo", "bar"));

        mock.assertCallCount(2);
        mock.assertCall(0, "getOwnerDocument");
        mock.assertCall(1, "createAttributeNS", "foo", "bar");
    }


    public void testAssertCountFailure() throws Exception
    {
        SimpleMock mock = new SimpleMock();
        Document mocked = mock.getInstance(Document.class);
        assertNull(mocked.getOwnerDocument());
        assertNull(mocked.createAttributeNS("foo", "bar"));

        // we can't call fail() from within the try block, because
        // we're testing the exception that it would throw
        boolean didThrow = false;
        try
        {
            mock.assertCallCount(3);
        }
        catch (AssertionFailedError ee)
        {
            didThrow = true;
        }

        if (!didThrow)
            fail("assertion didn't fail");
    }


    public void testAssertCallFailure() throws Exception
    {
        SimpleMock mock = new SimpleMock();
        Document mocked = mock.getInstance(Document.class);
        assertNull(mocked.getOwnerDocument());
        assertNull(mocked.createAttributeNS("foo", "bar"));

        // we can't call fail() from within the try block, because
        // we're testing the exception that it would throw
        boolean didThrow = false;
        try
        {
            mock.assertCall(0, "createAttributeNS", "foo", "bar");
        }
        catch (AssertionFailedError ee)
        {
            didThrow = true;
        }

        if (!didThrow)
            fail("assertion didn't fail");
    }
}
