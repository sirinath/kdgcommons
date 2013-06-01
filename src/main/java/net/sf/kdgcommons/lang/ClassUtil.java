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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.kdgcommons.collections.HashMultimap;


/**
 *  Static utility methods for working with class instances.
 *  reflection to do their work.
 *
 *  @since 1.0.4
 */
public class ClassUtil
{
    /**
     *  Converts the "internal" name of a class (eg: "[Ljava/lang/String;") to its
     *  "external" representation (eg: "java.lang.String[]").
     */
    public static String internalNameToExternal(String name)
    {
        if (name.length() == 1)
        {
            switch (name.charAt(0))
            {
                case 'V':
                    return "void";
                case 'Z':
                    return "boolean";
                case 'C':
                    return "char";
                case 'B':
                    return "byte";
                case 'S':
                    return "short";
                case 'I':
                    return "int";
                case 'J':
                    return "long";
                case 'F':
                    return "float";
                case 'D':
                    return "double";
                default :
                    throw new IllegalArgumentException("invalid type name: " + name);
            }
        }

        int arrayCount = name.lastIndexOf("[") + 1;
        String arraySuffix = "";
        for (int ii = 0 ; ii < arrayCount ; ii++)
            arraySuffix += "[]";

        if (arrayCount > 0)
            name = name.substring(arrayCount);

        if (name.startsWith("L"))
        {
            name = name.substring(1, name.length() - 1);
            name = name.replace('/', '.');
        }
        else
            name = internalNameToExternal(name);

        return name + arraySuffix;
    }


    /**
     *  Extracts the <code>TYPE</code> field value from a primitive wrapper type.
     *  Returns <code>null</code> if the passed value is not a wrapper type.
     *  <p>
     *  This method is useful for reflective parameter matching.
     *
     *  @since 1.0.9
     */
    public static Class<?> getPrimitiveType(Object val)
    {
        if (val == null)
            return null;

        return primitiveLookup.get(val.getClass());
    }


    /**
     *  Returns all methods defined by the class and its superclasses, without
     *  regard to access modifiers. Equivalent to recursively calling
     *  <code>Class.getDeclaredMethods()</code>.
     *
     *  @deprecated - In retrospect, there are few if any good reasons for
     *                calling this method. {@link #getVisibleMethods} is the
     *                better choice.
     */
    @Deprecated
    public static Method[] getAllMethods(Class<?> klass)
    {
        if (klass == null)
            return new Method[0];

        Method[] myMethods = klass.getDeclaredMethods();
        Method[] parentMethods = getAllMethods(klass.getSuperclass());
        ArrayList<Method> combined = new ArrayList<Method>(myMethods.length + parentMethods.length);
        combined.addAll(Arrays.asList(myMethods));
        for (Method method : parentMethods)
        {
            if (!isOverridden(method, myMethods))
                combined.add(method);
        }
        return combined.toArray(new Method[combined.size()]);
    }


    /**
     *  Returns the declared methods of the specified class that have the desired
     *  access modifiers. Note that this method does not look at superclass methods.
     *
     *  @since 1.0.9
     */
    public static Method[] getDeclaredMethodsByAccess(
            Class<?> klass,
            boolean isPublic, boolean isProtected, boolean isPrivate, boolean isDefault)
    {
        List<Method> result = new ArrayList<Method>();
        for (Method method : klass.getDeclaredMethods())
        {
            int modifiers = method.getModifiers();
            if (isPublic && Modifier.isPublic(modifiers))
                result.add(method);
            else if (isPrivate && Modifier.isPrivate(modifiers))
                result.add(method);
            else if (isProtected && Modifier.isProtected(modifiers))
                result.add(method);
            else if (isDefault
                        && ! Modifier.isPublic(modifiers)
                        && ! Modifier.isPrivate(modifiers)
                        && ! Modifier.isProtected(modifiers))
                result.add(method);
        }
        return result.toArray(new Method[result.size()]);
    }


    /**
     *  Returns all methods visible to the specified class. This includes all
     *  methods declared by the class, as well as protected, package, and
     *  protected methods declared by the class' superclass.
     *  <p>
     *  The order of the returned methods is undefined.
     *
     *  @since 1.0.9
     */
    public static Method[] getVisibleMethods(Class<?> klass)
    {
        HashMultimap<String,Method> methodMap = getVisibleMethodMap(klass);
        Method[] result = new Method[methodMap.size()];
        int ii = 0;
        for (Map.Entry<String,Method> entry : methodMap.entries())
        {
            result[ii++] = entry.getValue();
        }

        return result;
    }


    /**
     *  Returns a {@link net.sf.kdgcommons.collections.HashMultimap multi-map} of the
     *  methods visible to the given class. The key of this map is the method name,
     *  the values are the various methods associated with that name.
     *  <p>
     *  This method examines all declared methods of the specified class, and the
     *  public, protected, and default-access methods declared by the class' ancestors.
     *  For overridden methods, the lowest (most subclassed) method is returned.
     *
     *  @since 1.0.9
     */
    public static HashMultimap<String,Method> getVisibleMethodMap(Class<?> klass)
    {
        HashMultimap<String,Method> methodMap = new HashMultimap<String,Method>();
        boolean includePrivates = true;

        while (klass != null)
        {
            for (Method method : getDeclaredMethodsByAccess(klass, true, true, includePrivates, true))
            {
                addIfNotPresent(method, methodMap);
            }
            includePrivates = false;
            klass = klass.getSuperclass();
        }

        return methodMap;
    }


    /**
     *  Returns the visible methods defined by the class and its superclasses that
     *  have the specified annotation.
     */
    public static Method[] getAnnotatedMethods(Class<?> klass, Class<? extends Annotation> annotationKlass)
    {
        ArrayList<Method> result = new ArrayList<Method>();
        for (Method method : getVisibleMethods(klass))
        {
            if (method.getAnnotation(annotationKlass) != null)
                result.add(method);
        }
        return result.toArray(new Method[result.size()]);
    }


    /**
     *  Returns the most appropriate method to invoke for the specified parameter
     *  values. This method applies method selection rules described in JLS 15.12
     *  (http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.12).
     *  If unable to find a single appropriate method, returns <code>null</code>.
     *  <p>
     *  Notes:
     *  <ul>
     *  <li> All <code>null</code> argument values are ignored when attempting to
     *       resolve methods. This can result in multiple candidate methods, and
     *       a <code>null</code> return.
     *  <li> Unboxing conversions are considered (they have to be, because it's
     *       the only way to pass primitive values). If the same method exists
     *       with primitive and object parameters (eg: <code>foo(Integer)</code>
     *       and <code>foo(int)</code>), the variant with an object parameter is
     *       considered a better match than the variant with a primitive parameter.
     *  <li> A <code>null</code> argument will never match a primitive parameter
     *       (the compiler will match as if unboxed, with a runtime NPE).
     *  <li> Variable argument lists are transformed to arrays by the compiler;
     *       you must pass an array of the correct type as the last parameter to
     *       match such methods.
     *  </ul>
     *
     *  @param  klass       The class to examine.
     *  @param  methodName  The name of the method.
     *  @param  args        Actual argument values for the method. See note above
     *                      regarding the problems that <code>null</code> causes.
     *
     *  @since 1.0.9
     */
    public static Method getBestMethod(Class<?> klass, String methodName, Object... args)
    {
        Method bestMethod = null;
        int bestDistance = Integer.MAX_VALUE;
        boolean multipleBest = true;

        HashMultimap<String,Method> methodMap = getVisibleMethodMap(klass);
        for (Method method : methodMap.getAll(methodName))
        {
            int distance = matchParameters(method.getParameterTypes(), args);
            if (distance < 0)
                continue;

            // if we can't distinguish between multiple methods, we punt (maybe in
            // the future we'll create a rule to handle this)
            if (distance == bestDistance)
            {
                multipleBest = true;
            }

            if (distance < bestDistance)
            {
                multipleBest = false;
                bestDistance = distance;
                bestMethod = method;
            }
        }

        return multipleBest ? null : bestMethod;
    }


    /**
     *  Determines whether a given method is overridden by methods in the
     *  passed array.
     */
    public static boolean isOverridden(Method method, Method[] methods)
    {
        for (Method override : methods)
        {
            if (override.getName().equals(method.getName())
                    && Arrays.equals(override.getParameterTypes(), method.getParameterTypes()))
                return true;
        }
        return false;
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    // lookup table for primitive -> wrapper
    private static Map<Class<?>,Class<?>> primitiveLookup = new HashMap<Class<?>,Class<?>>();
    static
    {
        primitiveLookup.put(Boolean.class,  Boolean.TYPE);
        primitiveLookup.put(Byte.class,     Byte.TYPE);
        primitiveLookup.put(Short.class,    Short.TYPE);
        primitiveLookup.put(Integer.class,  Integer.TYPE);
        primitiveLookup.put(Long.class,     Long.TYPE);
        primitiveLookup.put(Float.class,    Float.TYPE);
        primitiveLookup.put(Double.class,   Double.TYPE);
    }

    // lookup tables for primitive "distance" calculations

    private static Map<Class<?>,Integer> integralDistance = new HashMap<Class<?>,Integer>();
    static
    {
        integralDistance.put(Byte.TYPE,    Integer.valueOf(1));
        integralDistance.put(Short.TYPE,   Integer.valueOf(2));
        integralDistance.put(Integer.TYPE, Integer.valueOf(3));
        integralDistance.put(Long.TYPE,    Integer.valueOf(4));
    }

    private static Map<Class<?>,Integer> floatDistance = new HashMap<Class<?>,Integer>();
    static
    {
        floatDistance.put(Float.TYPE,      Integer.valueOf(1));
        floatDistance.put(Double.TYPE,     Integer.valueOf(2));
    }



    /**
     *  Adds the method to the multimap if it doesn't already exist. When checking
     *  for existence, the method is considered to be in the map if there's any
     *  method with the same name and parameters; declaring class is ignored.
     */
    private static void addIfNotPresent(Method method, HashMultimap<String,Method> methodMap)
    {
        String methodName = method.getName();
        if (! methodMap.containsKey(methodName))
        {
            methodMap.put(methodName, method);
            return;
        }

        Class<?>[] params = method.getParameterTypes();
        for (Method mappedMethod : methodMap.getAll(methodName))
        {
            if (ObjectUtil.equals(mappedMethod.getParameterTypes(), params))
                return;
        }

        methodMap.put(methodName, method);
    }


    /**
     *  Attempts to match the classes of the passed arguments against the
     *  parameter types, returning the "distance" that separates them. A
     *  distance of 0 is an exact match, a distance of 1 means that one
     *  argument's superclass matched a parameter, and so on.
     */
    private static int matchParameters(Class<?>[] params, Object[] args)
    {
        int distance = 0;

        if (params.length != args.length)
            return -1;

        for (int ii = 0 ; ii < params.length ; ii++)
        {
            // FIXME - if arg is null and type is primitive, reject

            if (args[ii] == null)
                continue;

            if (params[ii].isPrimitive())
            {
                distance = matchPrimitive(params[ii], args[ii]);
            }
            else
            {
                Class<?> argType = args[ii].getClass();
                if (!params[ii].isAssignableFrom(argType))
                    return -1;

                while (argType != params[ii])
                {
                    argType = argType.getSuperclass();
                    distance++;
                }
            }
        }

        return distance;
    }


    /**
     *  Attempts to match the argument value against the passed primitive type.
     *  Returned distance is:
     *  <ul>
     *  <li> -1 if the argument is not a numeric or incompatible with the
     *       parameter type
     *  <li> 1 if it's a numeric that exactly matches the  parameter type
     *  <li> 2-4 if it's ac numeric and the parameter type is a larger
     *       numeric of the same family (integer or floating point). See
     *       the code for details.
     *  <li> 5 if it's an integral numeric and the parameter type is a
     *       <code>double</code>.
     *  </ul>
     */
    private static int matchPrimitive(Class<?> paramType, Object arg)
    {
        Class<?> argType = getPrimitiveType(arg);
        if (argType == null)
            return -1;

        if (paramType == argType)
            return 1;

        if (integralDistance.containsKey(argType) && integralDistance.containsKey(paramType))
        {
            int argDepth = integralDistance.get(argType).intValue();
            int paramDepth = integralDistance.get(paramType).intValue();
            if (argDepth < paramDepth)
                return 1 + (paramDepth - argDepth);
        }
        else if (floatDistance.containsKey(argType) && floatDistance.containsKey(paramType))
        {
            int argDepth = floatDistance.get(argType).intValue();
            int paramDepth = floatDistance.get(paramType).intValue();
            if (argDepth < paramDepth)
                return 1 + (paramDepth - argDepth);
        }
        else if (integralDistance.containsKey(argType) && (paramType == Double.TYPE))
        {
            return 5;
        }

        return -1;
    }
}
