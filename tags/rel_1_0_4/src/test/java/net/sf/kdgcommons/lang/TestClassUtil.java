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

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import net.sf.kdgcommons.collections.HashMultimap;


public class TestClassUtil extends TestCase
{
//----------------------------------------------------------------------------
//  Test Classes -- note that they must be static to allow reflection
//----------------------------------------------------------------------------

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foo
    {
        // just a marker
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface Bar
    {
        // just a marker
    }

    public static class Parent
    {
        public int foo()
        {
            return 1;
        }

        @Bar
        public int bar()
        {
            return 2;
        }
    }


    public static class Child extends Parent
    {
        @Override
        public int bar()
        {
            return 3;
        }

        @Foo
        public int baz()
        {
            return 3;
        }
    }


    public static class Grandchild extends Child
    {
        public int bar(int param)
        {
            return 4;
        }
    }


//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------

    /**
     *  Converts an array of methods to a multimap keyed by method name.
     */
    public static HashMultimap<String,Method> methods2map(Method[] methods)
    {
        HashMultimap<String,Method> ret = new HashMultimap<String,Method>();
        for (Method method : methods)
            ret.put(method.getName(), method);
        return ret;
    }


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    public void testGetAllMethods() throws Exception
    {
        Method[] methods = ClassUtil.getAllMethods(Child.class);

        // rather than an absolute count, assert that we got methods from all
        // classes in the inheritance tree, including a protected method
        HashMultimap<String,Method> methodMap = methods2map(methods);
        assertTrue("baz() present",      methodMap.containsKey("baz"));
        assertTrue("bar() present",      methodMap.containsKey("bar"));
        assertTrue("foo() present",      methodMap.containsKey("foo"));
        assertTrue("toString() present", methodMap.containsKey("toString"));
        assertTrue("clone() present",    methodMap.containsKey("clone"));

        // and we want to verify that subclasses override superclasses
        assertEquals("bar() count", 1,          methodMap.getAll("bar").size());
        assertEquals("bar() defined by child",  Child.class.getDeclaredMethod("bar"),
                                                methodMap.get("bar"));
    }


    public void testGetAllMethodsWithOverload() throws Exception
    {
        Method[] methods = ClassUtil.getAllMethods(Grandchild.class);
        HashMultimap<String,Method> methodMap = methods2map(methods);

        assertEquals("bar() count", 2,          methodMap.getAll("bar").size());
    }


    public void testGetAnnotatedMethods() throws Exception
    {
        Method[] m1 = ClassUtil.getAnnotatedMethods(Grandchild.class, Foo.class);
        assertEquals("methods with @Foo", 1, m1.length);
        assertEquals("baz() tagged @Foo", Child.class.getDeclaredMethod("baz"),
                                          m1[0]);

        Method[] m2 = ClassUtil.getAnnotatedMethods(Grandchild.class, Foo.class);
        assertEquals("methods with @Bar", 1, m2.length);
        assertEquals("@Bar is inherited", Child.class.getDeclaredMethod("baz"),
                                          m2[0]);
    }
}
