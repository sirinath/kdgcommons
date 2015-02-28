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

package net.sf.kdgcommons.swing;

import java.awt.Toolkit;
import javax.swing.SwingUtilities;

import junit.framework.*;


public class TestAsynchronousOperation extends TestCase
{
//------------------------------------------------------------------------------
//  Boilerplate
//------------------------------------------------------------------------------

    public TestAsynchronousOperation(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(TestAsynchronousOperation.class);
        return suite;
    }

//------------------------------------------------------------------------------
//  Common test code
//------------------------------------------------------------------------------

    @Override
    public void setUp()
    {
        // this should ensure that the event thread is running
        Toolkit.getDefaultToolkit();
    }


//------------------------------------------------------------------------------
//  Support code
//------------------------------------------------------------------------------

    /**
     *  A mock implementation of <CODE>AsynchronousOperation</CODE>. This still
     *  needs to be subclassed to implement <CODE>performOperation()</CODE>.
     */
    private static abstract class BaseOperation
    extends AsynchronousOperation<Object>
    {
        public boolean      _callbackOnEventThread;
        public boolean      _onCompleteCalled;
        public Object       _callbackResult;
        public Throwable    _callbackException;

        @Override
        protected void onComplete()
        {
            _onCompleteCalled = true;
        }

        @Override
        protected void onSuccess(Object result)
        {
            _callbackOnEventThread = SwingUtilities.isEventDispatchThread();
            _callbackResult = result;
        }

        @Override
        protected void onFailure(Throwable e)
        {
            _callbackOnEventThread = SwingUtilities.isEventDispatchThread();
            _callbackException = e;
        }
    }


    /**
     *  Instances of this object are put on the event thread to synchronize
     *  tests.
     */
    private static class NullRunnable
    implements Runnable
    {
        public void run()
        {
            // nothing happening here
        }
    }


//------------------------------------------------------------------------------
//  Test methods go here
//------------------------------------------------------------------------------

    public void testSuccess()
    throws Exception
    {
        final Integer result = new Integer(123);
        BaseOperation testOperation = new BaseOperation()
        {
            @Override
            protected Object performOperation()
            throws Exception
            {
                return result;
            }
        };

        testOperation.run();
        SwingUtilities.invokeAndWait(new NullRunnable());

        assertTrue("callback on event thread", testOperation._callbackOnEventThread);
        assertTrue("onComplete() called", testOperation._onCompleteCalled);
        assertEquals("result", result, testOperation._callbackResult);
        assertNull("exception", testOperation._callbackException);
    }


    public void testFailure()
    throws Exception
    {
        final Exception result = new IndexOutOfBoundsException("test");
        BaseOperation testOperation = new BaseOperation()
        {
            @Override
            protected Object performOperation()
            throws Exception
            {
                throw result;
            }
        };

        testOperation.run();
        SwingUtilities.invokeAndWait(new NullRunnable());

        assertTrue("callback on event thread", testOperation._callbackOnEventThread);
        assertTrue("onComplete() called", testOperation._onCompleteCalled);
        assertEquals("exception", result, testOperation._callbackException);
        assertNull("result", testOperation._callbackResult);
    }
}
