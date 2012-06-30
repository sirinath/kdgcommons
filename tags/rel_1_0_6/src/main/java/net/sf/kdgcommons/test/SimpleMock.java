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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import junit.framework.Assert;


/**
 *  A simple mock object using a reflection proxy: you tell it the type of
 *  interface that you want to mock, and it records invocations on that
 *  interface. You can make assertions on those invocations.
 *  <p>
 *  All invoked methods return <code>null</code>. Override {@link #invoke}
 *  if you want to change this behavior, but be sure to call the superclass
 *  implementation (otherwise it won't record the invocation).
 *  <p>
 *  A single instance can mock multiple objects, however method invocations
 *  will be interleaved. Doing so is primarily useful when you simply need a
 *  dummy object as a method parameter, and don't actually use it.
 */
public class SimpleMock
implements InvocationHandler
{
    private ArrayList<String> _calls = new ArrayList<String>();
    private ArrayList<Object[]> _args = new ArrayList<Object[]>();

    public <T> T getInstance(Class<T> classToMock)
    {
        return classToMock.cast(
                Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] {classToMock},
                    this));
    }


    public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable
    {
        _calls.add(method.getName());
        if (args == null)
            args = new Object[0];
        _args.add(args);
        return null;
    }


    /**
     *  Asserts that we received the expected number of invocations.
     */
    public void assertCallCount(int expected)
    {
        Assert.assertEquals("call count", expected, _calls.size());
    }


    /**
     *  Asserts the method name and parameter of a specific invocation.
     *  Note that calls are numbered from 0.
     */
    public void assertCall(int callNum, String methodName, Object... args)
    {
        Assert.assertEquals("incorrect method", methodName, _calls.get(callNum));
        Assert.assertEquals("argument count", args.length, _args.get(callNum).length);
        for (int ii = 0 ; ii < args.length ; ii++)
            Assert.assertEquals("argument " + ii, args[ii], _args.get(callNum)[ii]);
    }
}
