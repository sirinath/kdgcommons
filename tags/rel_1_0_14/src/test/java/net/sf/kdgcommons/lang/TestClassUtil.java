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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

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
        public int foo()                        { return 1; }

        @Bar
        public int bar()                        { return 2; }
    }


    public static class Child
    extends Parent
    {
        @Override
        public int bar()                        { return 3; }

        @Foo
        public int baz()                        { return 3; }
    }


    public static class Grandchild extends Child
    {
        public int bar(int param)               { return 4; }
    }


    public static class VisibilityBase
    {
        public    void foo(Object val)          { /* nothing here */ }

        private   void bar(Object val)          { /* nothing here */ }

        protected void baz(Object val)          { /* nothing here */ }

                  void bif(Object val)          { /* nothing here */ }
    }


    public static class VisibilitySub
    extends VisibilityBase
    {
        // everything is inherited
    }


    public static class VisibilityOverride
    extends VisibilityBase
    {
        @Override
        protected void baz(Object val)          { /* nothing here */ }
    }


    public static class BestMethodBase
    {
        public void foo(Object val)             { /* nothing here */ }
        public void foo(String val)             { /* nothing here */ }
        public void foo(Number val)             { /* nothing here */ }

        public void bar(Object val)             { /* nothing here */ }

        public void baz()                       { /* nothing here */ }
        public void baz(Object val)             { /* nothing here */ }
    }


    public static class BestMethodSub
    extends BestMethodBase
    {
        @Override
        public void foo(Object val)             { /* nothing here */ }
        public void foo(Integer val)            { /* nothing here */ }
    }


    public static class BestMethodMultiArg
    {
        public void foo(Integer v1, Number v2)  { /* nothing here */ }
        public void foo(Number v1,  Integer v2) { /* nothing here */ }
        public void foo(Number v1,  Number v2)  { /* nothing here */ }
    }


    public static class BestMethodPrimitive
    {
        public void foo(int val)                { /* nothing here */ }

        public void bar(int val)                { /* nothing here */ }
        public void bar(double val)             { /* nothing here */ }

        public void baz(int val)                { /* nothing here */ }
        public void baz(Integer val)            { /* nothing here */ }

        public void bif(boolean val)            { /* nothing here */ }
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


    /**
     *  Asserts that the passed array of method contains one with the given name
     *  and parameters.
     */
    private static void assertContainsMethod(String message, Method[] arr, String name, Class<?>... paramTypes)
    {
        for (Method method : arr)
        {
            if (! method.getName().equals(name))
                continue;
            if (Arrays.equals(method.getParameterTypes(), paramTypes))
                return;
        }
        fail(message + ": no such method: " + name + "(" + Arrays.asList(paramTypes) + ")");
    }


//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    public void testInternalNameToExternal() throws Exception
    {
        assertEquals("void",                 ClassUtil.internalNameToExternal("V"));
        assertEquals("boolean",              ClassUtil.internalNameToExternal("Z"));
        assertEquals("char",                 ClassUtil.internalNameToExternal("C"));
        assertEquals("byte",                 ClassUtil.internalNameToExternal("B"));
        assertEquals("short",                ClassUtil.internalNameToExternal("S"));
        assertEquals("int",                  ClassUtil.internalNameToExternal("I"));
        assertEquals("float",                ClassUtil.internalNameToExternal("F"));
        assertEquals("long",                 ClassUtil.internalNameToExternal("J"));
        assertEquals("double",               ClassUtil.internalNameToExternal("D"));
        assertEquals("java.lang.Object",     ClassUtil.internalNameToExternal("Ljava/lang/Object;"));
        assertEquals("int[]",                ClassUtil.internalNameToExternal("[I"));
        assertEquals("java.lang.Object[][]", ClassUtil.internalNameToExternal("[[Ljava/lang/Object;"));
        
        try
        {
            ClassUtil.internalNameToExternal("Q");
            fail("accepted one of many invalid characters");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message: \"" + ex.getMessage() + "\"", ex.getMessage().contains("invalid"));
            assertTrue("exception message: \"" + ex.getMessage() + "\"", ex.getMessage().endsWith("Q"));
        }
    }


    public void testGetPrimitiveType() throws Exception
    {
        assertEquals("Boolean",     Boolean.TYPE,   ClassUtil.getPrimitiveType(Boolean.TRUE));
        assertEquals("Byte",        Byte.TYPE,      ClassUtil.getPrimitiveType(Byte.valueOf((byte)1)));
        assertEquals("Short",       Short.TYPE,     ClassUtil.getPrimitiveType(Short.valueOf((short)1)));
        assertEquals("Integer",     Integer.TYPE,   ClassUtil.getPrimitiveType(Integer.valueOf(1)));
        assertEquals("Long",        Long.TYPE,      ClassUtil.getPrimitiveType(Long.valueOf(1)));
        assertEquals("Float",       Float.TYPE,     ClassUtil.getPrimitiveType(Float.valueOf(1.0f)));
        assertEquals("Double",      Double.TYPE,    ClassUtil.getPrimitiveType(Double.valueOf(1.0)));

        assertEquals("String",      null,           ClassUtil.getPrimitiveType("foo"));
        assertEquals("BigDecimal",  null,           ClassUtil.getPrimitiveType(new BigDecimal("1.0")));

        assertEquals("null-safe",   null,           ClassUtil.getPrimitiveType(null));
    }


    @SuppressWarnings("deprecation")
    public void testGetAllMethods() throws Exception
    {
        Method[] methods = ClassUtil.getAllMethods(Child.class);

        // rather than an absolute count, assert that we got methods from all
        // classes in the inheritance tree, including a protected method
        HashMultimap<String,Method> methodMap = methods2map(methods);
        assertTrue("public child - baz",    methodMap.containsKey("baz"));
        assertTrue("override child - bar",  methodMap.containsKey("bar"));
        assertTrue("public parent - foo",   methodMap.containsKey("foo"));
        assertTrue("toString()",            methodMap.containsKey("toString"));
        assertTrue("clone()",               methodMap.containsKey("clone"));

        // and we want to verify that subclasses override superclasses
        assertEquals("bar() count", 1,          methodMap.getAll("bar").size());
        assertEquals("bar() defined by child",  Child.class.getDeclaredMethod("bar"),
                                                methodMap.get("bar"));
    }


    @SuppressWarnings("deprecation")
    public void testGetAllMethodsWithOverload() throws Exception
    {
        Method[] methods = ClassUtil.getAllMethods(Grandchild.class);
        HashMultimap<String,Method> methodMap = methods2map(methods);

        assertEquals("bar() count", 2,          methodMap.getAll("bar").size());
    }


    public void testGetDeclaredMethodsByAccess() throws Exception
    {
        // WARNING - JaCoCo (EclEmma) code coverage adds methods to class, causes this
        //           test to fail; Cobertura doesn't do this, so site build is OK

        Method[] m1 = ClassUtil.getDeclaredMethodsByAccess(VisibilitySub.class, true, true, true, true);
        assertEquals("subclass doesn't declare methods", 0, m1.length);

        Method[] m2 = ClassUtil.getDeclaredMethodsByAccess(VisibilityBase.class, true, true, true, true);
        assertEquals("all visibilities", 4, m2.length);
        assertContainsMethod("public",    m2, "foo", Object.class);
        assertContainsMethod("private",   m2, "bar", Object.class);
        assertContainsMethod("protected", m2, "baz", Object.class);
        assertContainsMethod("package",   m2, "bif", Object.class);

        Method[] m3 = ClassUtil.getDeclaredMethodsByAccess(VisibilityBase.class, true, false, false, false);
        assertEquals("public only", 1, m3.length);
        assertContainsMethod("public",    m3, "foo", Object.class);

        Method[] m4 = ClassUtil.getDeclaredMethodsByAccess(VisibilityBase.class, false, true, false, false);
        assertEquals("protected only", 1, m4.length);
        assertContainsMethod("protected", m4, "baz", Object.class);

        Method[] m5 = ClassUtil.getDeclaredMethodsByAccess(VisibilityBase.class, false, false, true, false);
        assertEquals("private only", 1, m5.length);
        assertContainsMethod("private",   m5, "bar", Object.class);

        Method[] m6 = ClassUtil.getDeclaredMethodsByAccess(VisibilityBase.class, false, false, false, true);
        assertEquals("default", 1, m6.length);
        assertContainsMethod("default",   m6, "bif", Object.class);
    }


    public void testGetVisibleMethods() throws Exception
    {
        Method[] m1 = ClassUtil.getVisibleMethods(VisibilityBase.class);
        assertContainsMethod("base class can see its own public method",    m1, "foo", Object.class);
        assertContainsMethod("base class can see its own private method",   m1, "bar", Object.class);
        assertContainsMethod("base class can see its own protected method", m1, "baz", Object.class);
        assertContainsMethod("base class can see its own package method",   m1, "bif", Object.class);
        assertContainsMethod("base class can see Object public method",     m1, "toString");
        assertContainsMethod("base class can see Object protected method",  m1, "clone");

        Method[] m2 = ClassUtil.getVisibleMethods(VisibilitySub.class);
        assertContainsMethod("sub class can see base public method",        m2, "foo", Object.class);
        assertContainsMethod("sub class can see base protected method",     m2, "baz", Object.class);
        assertContainsMethod("sub class can see base package method",       m2, "bif", Object.class);
        assertContainsMethod("sub class can see Object public method",      m2, "toString");
        assertContainsMethod("sub class can see Object protected method",   m2, "clone");
        assertFalse("sub shouldn't see base private method", methods2map(m2).containsKey("bar"));

        Method[] m3 = ClassUtil.getVisibleMethods(VisibilityOverride.class);
        Method m3x = methods2map(m3).get("baz");
        assertEquals("overridden method defined by subclass", VisibilityOverride.class, m3x.getDeclaringClass());
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


    public void testGetBestMethod() throws Exception
    {
        // test 1: exact match

        Method m1 = ClassUtil.getBestMethod(BestMethodBase.class, "foo", "bar");
        assertEquals("exact: method name",  "foo", m1.getName());
        assertEquals("exact: parameters",   Arrays.asList(String.class),
                                            Arrays.asList(m1.getParameterTypes()));

        // test 2: match most-specific superclass

        Method m2 = ClassUtil.getBestMethod(BestMethodBase.class, "foo", Integer.valueOf(1));
        assertEquals("best: method name",  "foo", m2.getName());
        assertEquals("best: parameters",    Arrays.asList(Number.class),
                                            Arrays.asList(m2.getParameterTypes()));

        // test 3: everything matches Object

        Method m3 = ClassUtil.getBestMethod(BestMethodBase.class, "foo", new StringBuilder());
        assertEquals("base: method name",  "foo", m3.getName());
        assertEquals("base: parameters",    Arrays.asList(Object.class),
                                            Arrays.asList(m3.getParameterTypes()));

        // test 4: rejected if too few arguments

        Method m4 = ClassUtil.getBestMethod(BestMethodBase.class, "foo");
        assertNull("too few arguments", m4);

        // test 5: rejected if too many arguments

        Method m5 = ClassUtil.getBestMethod(BestMethodBase.class, "foo", "X", "Y");
        assertNull("too many arguments", m5);
    }


    public void testGetBestMethodForNull() throws Exception
    {
        // test 1: if only one method, we can resolve it

        Method m1 = ClassUtil.getBestMethod(BestMethodBase.class, "bar", (Object)null);
        assertEquals("resolvable: method name",  "bar", m1.getName());
        assertEquals("resolvable: parameters",   Arrays.asList(Object.class),
                                                 Arrays.asList(m1.getParameterTypes()));

        // test 2: but not if there are multiple methods with the same name

        Method m2 = ClassUtil.getBestMethod(BestMethodBase.class, "foo", (Object)null);
        assertNull("unresolvable method", m2);
    }


    public void testGetBestMethodNoArguments() throws Exception
    {
        Method m1 = ClassUtil.getBestMethod(BestMethodBase.class, "baz");
        assertEquals("resolvable: method name",  "baz", m1.getName());
        assertEquals("resolvable: parameters",   Collections.emptyList(),
                                                 Arrays.asList(m1.getParameterTypes()));
    }


    public void testGetBestMethodMultipleArguments() throws Exception
    {
        // test 1: exact match

        Method m1 = ClassUtil.getBestMethod(BestMethodMultiArg.class, "foo", Integer.valueOf(1), Double.valueOf(1.0));
        assertEquals("exact: method name",   "foo", m1.getName());
        assertEquals("exact: parameters",    Arrays.asList(Integer.class, Number.class),
                                             Arrays.asList(m1.getParameterTypes()));
        // test 2: best match

        Method m2 = ClassUtil.getBestMethod(BestMethodMultiArg.class, "foo", Double.valueOf(1.0), Double.valueOf(1.0));
        assertEquals("best: method name",   "foo", m2.getName());
        assertEquals("best: parameters",    Arrays.asList(Number.class, Number.class),
                                            Arrays.asList(m2.getParameterTypes()));

        // test 3: null param is OK if able to make exact match

        Method m3 = ClassUtil.getBestMethod(BestMethodMultiArg.class, "foo", Integer.valueOf(1), null);
        assertEquals("best: method name",   "foo", m3.getName());
        assertEquals("best: parameters",    Arrays.asList(Integer.class, Number.class),
                                            Arrays.asList(m3.getParameterTypes()));

        // test 4: multiple potential matches

        Method m4 = ClassUtil.getBestMethod(BestMethodMultiArg.class, "foo", Integer.valueOf(1), Integer.valueOf(1));
        assertNull("multiple matches", m4);
    }


    public void testGetBestMethodWithPrimitives() throws Exception
    {
        // test 1: only one possibility

        Method m1 = ClassUtil.getBestMethod(BestMethodPrimitive.class, "foo", Integer.valueOf(1));
        assertEquals("single method: method name",  "foo", m1.getName());
        assertEquals("single method: parameters",   Arrays.asList(Integer.TYPE),
                                                    Arrays.asList(m1.getParameterTypes()));

        // test 2: exact match of param type

        Method m2a = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Integer.valueOf(1));
        assertEquals("exact match 1: method name",  "bar", m2a.getName());
        assertEquals("exact match 1: parameters",   Arrays.asList(Integer.TYPE),
                                                    Arrays.asList(m2a.getParameterTypes()));

        Method m2b = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Double.valueOf(1));
        assertEquals("exact match 2: method name",  "bar", m2b.getName());
        assertEquals("exact match 2: parameters",   Arrays.asList(Double.TYPE),
                                                    Arrays.asList(m2b.getParameterTypes()));

        // test 3: promotion

        Method m3a = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Byte.valueOf((byte)1));
        assertEquals("byte promotion: method name",     "bar", m3a.getName());
        assertEquals("byte promotion: parameters",      Arrays.asList(Integer.TYPE),
                                                        Arrays.asList(m3a.getParameterTypes()));

        Method m3b = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Short.valueOf((short)1));
        assertEquals("short promotion: method name",    "bar", m3b.getName());
        assertEquals("short promotion: parameters",     Arrays.asList(Integer.TYPE),
                                                        Arrays.asList(m3b.getParameterTypes()));

        Method m3c = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Long.valueOf(1));
        assertEquals("long promotion: method name",     "bar", m3c.getName());
        assertEquals("long promotion: parameters",      Arrays.asList(Double.TYPE),
                                                        Arrays.asList(m3c.getParameterTypes()));

        Method m3d = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bar", Float.valueOf(1.0f));
        assertEquals("float promotion: method name",    "bar", m3d.getName());
        assertEquals("float promotion: parameters",     Arrays.asList(Double.TYPE),
                                                        Arrays.asList(m3d.getParameterTypes()));

        // test 4: incompatible args

        Method m4a = ClassUtil.getBestMethod(BestMethodPrimitive.class, "foo", Long.valueOf(1));
        assertNull("no match: long -> int", m4a);

        Method m4b = ClassUtil.getBestMethod(BestMethodPrimitive.class, "foo", Double.valueOf(1));
        assertNull("no match: double -> int", m4b);

        // test 5: wrapper takes precedence over primitive

        Method m5 = ClassUtil.getBestMethod(BestMethodPrimitive.class, "baz", Integer.valueOf(1));
        assertEquals("single method: method name",  "baz", m5.getName());
        assertEquals("single method: parameters",   Arrays.asList(Integer.class),
                                                    Arrays.asList(m5.getParameterTypes()));

        // test 6: but promotion gets primitive variant

        Method m6 = ClassUtil.getBestMethod(BestMethodPrimitive.class, "baz", Short.valueOf((short)1));
        assertEquals("single method: method name",  "baz", m6.getName());
        assertEquals("single method: parameters",   Arrays.asList(Integer.TYPE),
                                                    Arrays.asList(m6.getParameterTypes()));

        // test X: boolean is a primitive too!

        Method mX = ClassUtil.getBestMethod(BestMethodPrimitive.class, "bif", Boolean.TRUE);
        assertEquals("boolean: method name",        "bif", mX.getName());
        assertEquals("boolean: parameters",         Arrays.asList(Boolean.TYPE),
                                                    Arrays.asList(mX.getParameterTypes()));
    }


    public void testGetBestMethodWithInheritance() throws Exception
    {
        // test 1: method defined in subclass

        Method m1 = ClassUtil.getBestMethod(BestMethodSub.class, "foo", Integer.valueOf(1));
        assertEquals("subclass: method name",   "foo", m1.getName());
        assertEquals("subclass: parameters",    Arrays.asList(Integer.class),
                                                Arrays.asList(m1.getParameterTypes()));

        // test 2: method defined in superclass

        Method m2 = ClassUtil.getBestMethod(BestMethodSub.class, "foo", Double.valueOf(1));
        assertEquals("superclass: method name", "foo", m2.getName());
        assertEquals("superclass: parameters",  Arrays.asList(Number.class),
                                                Arrays.asList(m2.getParameterTypes()));

        // test 3: overridden method

        Method m3 = ClassUtil.getBestMethod(BestMethodSub.class, "foo", new StringBuilder());
        assertEquals("override: method name",   "foo", m3.getName());
        assertEquals("override: parameters",    Arrays.asList(Object.class),
                                                Arrays.asList(m3.getParameterTypes()));
    }
}
