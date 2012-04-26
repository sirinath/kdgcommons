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
import java.util.ArrayList;
import java.util.Arrays;


/**
 *  Static utility methods for working with class instances.
 *  reflection to do their work.
 */
public class ClassUtil
{
    /**
     *  Returns all methods defined by the class and its superclasses.
     *  Equivalent to recursively calling <code>Class.getDeclaredMethods()</code>.
     */
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
     *  Returns all methods defined by the class and its superclasses that
     *  have the specified annotation.
     */
    public static Method[] getAnnotatedMethods(Class<?> klass, Class<? extends Annotation> annotationKlass)
    {
        Method[] methods = getAllMethods(klass);
        ArrayList<Method> result = new ArrayList<Method>();
        for (Method method : methods)
        {
            if (method.getAnnotation(annotationKlass) != null)
                result.add(method);
        }
        return result.toArray(new Method[result.size()]);
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

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
}
