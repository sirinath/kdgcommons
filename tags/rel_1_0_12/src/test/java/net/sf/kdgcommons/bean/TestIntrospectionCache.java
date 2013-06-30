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

package net.sf.kdgcommons.bean;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import net.sf.kdgcommons.testinternals.InaccessibleClass;

public class TestIntrospectionCache
extends TestCase
{
    public TestIntrospectionCache(String testName)
    {
        super(testName);
    }


//----------------------------------------------------------------------------
//  Test Objects
//----------------------------------------------------------------------------

    public static class Bean1
    {
        private String _sVal;

        public String getSVal()             { return _sVal; }
        public void setSVal(String val)     { _sVal = val; }
    }


    public static class Bean2
    {
        private String _sVal;

        public String getSVal()             { return _sVal; }
        public void setSVal(String val)     { _sVal = val; }
    }

//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

    public void testBasicOperation() throws Exception
    {
        IntrospectionCache cache = new IntrospectionCache();

        Introspection ispec1 = cache.lookup(Bean1.class);
        assertNotNull(ispec1);

        Introspection ispec2 = cache.lookup(Bean2.class);
        assertNotNull(ispec2);
        assertNotSame(ispec1, ispec2);

        Introspection ispec3 = cache.lookup(Bean1.class);
        assertNotNull(ispec3);
        assertSame(ispec1, ispec3);
    }


    // note: this is the only method allowed to test a static cache
    public void testStaticCache() throws Exception
    {
        IntrospectionCache cache1 = new IntrospectionCache(true);
        IntrospectionCache cache2 = new IntrospectionCache(false);
        IntrospectionCache cache3 = new IntrospectionCache(true);
        IntrospectionCache cache4 = new IntrospectionCache(false);

        Introspection ispec1 = cache1.lookup(Bean1.class);

        Introspection ispec2 = cache2.lookup(Bean1.class);
        assertNotSame(ispec1, ispec2);

        Introspection ispec3 = cache3.lookup(Bean1.class);
        assertSame(ispec1, ispec3);

        Introspection ispec4 = cache4.lookup(Bean1.class);
        assertNotSame(ispec1, ispec4);
        assertNotSame(ispec2, ispec4);
    }


    public void testPublicMethodInPrivateClass() throws Exception
    {
        InaccessibleClass instance = InaccessibleClass.newInstance();

        // the default introspector does not make the method accessible, so
        // attempting to invoke it will throw
        IntrospectionCache cache1 = new IntrospectionCache();
        Introspection ispec1 = cache1.lookup(instance.getClass());
        Method setter1 = ispec1.setter("value");
        assertNotNull("able to retrieve setter", setter1);

        try
        {
            setter1.invoke(instance, "foo");
            fail("apparently able to invoke inaccessible setter (maybe JVM changed?)");
        }
        catch (Exception ex)
        {
            assertEquals("expected exception type", IllegalAccessException.class, ex.getClass());
        }

        // the alternate constructor allows us to force accessibility

        IntrospectionCache cache2 = new IntrospectionCache();
        Introspection ispec2 = cache2.lookup(instance.getClass(), true);
        Method setter2 = ispec2.setter("value");
        assertNotNull("able to retrieve setter", setter2);
        setter2.invoke(instance, "foo");
        assertEquals("able to invoke setter", "foo", instance.getValue());
    }
}
