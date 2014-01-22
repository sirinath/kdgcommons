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

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 *  A replacement for <code>java.beans.Introspector</code> that is tailored to
 *  conversion of bean data into other forms. Uses the following rules to find
 *  the getters and setters of an introspected class:
 *  <dl>
 *  <dt>Identifies multiple accessor methods
 *  <dd><code>javax.beans.Introspector</code> looks at parameter type, and
 *      attempts to match getters and setters. This method looks only at the
 *      method name, and will return any method whose name starts with "get"
 *      or "is".
 *  <dt>Ignores case in name comparison
 *  <dd><code>javax.beans.Introspector</code> has camel-casing rules that do
 *      not always correspond to programmer intent -- in particular, two
 *      initial capitals are not camelcased.
 *  <dt>Resolves multiple setter methods for same property
 *  <dd>In the case where there are multiple getters/setters with the same
 *      name, the following ranking is applied:
 *      <ol>
 *      <li> Methods defined by subclass, over those defined by superclass.
 *           The subclass is assumed to be more specific.
 *      <li> Methods that get/take primitive values.
 *      <li> Methods that get/take primitive wrappers.
 *      <li> Methods that get/take <code>String</code>. Driven by the use of
 *           this introspector to translate to/from a text format.
 *      <li> Methods that get/take arbitrary objects.
 *      </ol>
 *  <dt>Resolves multiple getter methods for same property
 *  <dd>For class hierarchies that define covariant return types, will use
 *      the most specific type.
 *  <dt>Ignores properties defined by <code>Object</code>
 *  <dd><code>javax.beans.Introspector</code> allows specific control over the
 *      parts of the class hierarchy to be introspected; by default, it will
 *      include methods defined by <code>Object</code>. We don't care about
 *      those, but assume any other class in the hierarchy to be important.
 *  <dt>Does not cache introspections.
 *  <dd><code>javax.beans.Introspector</code> maintains its own cache of methods.
 *      Since it is loaded by the bootstrap classloader, and thus never released,
 *      this can wreak havoc when used by a library in an application server. We
 *      separate introspection and caching; see {@link IntrospectionCache} for
 *      the latter.
 *  <dt>Does not rely on <code>setAccessible()</code>
 *  <dd>Introspects only the public getter and setter methods. In a perfect world,
 *      these will be sufficient to marshall and unmarshall an object, and we can
 *      use the introspected methods in a sandbox (applet or security-managed
 *      container). In the real world, you can have a public method on a private
 *      class, and get an access exception if you attempt to invoke that method.
 *      If you live in that world, you can instruct <code>Introspector</code> to
 *      mark each method as accessible (but then you can't run in sandboxes).
 *  </dl>
 *  Instances of this class are read-only (and thus threadsafe) once constructed.
 *
 *  @since 1.0.5
 */
public class Introspection
{
    private boolean _setAccessible;
    private Set<String> _propNames;
    private Set<String> _propNamesPublic;
    private Map<String,Method> _getters;
    private Map<String,Method> _setters;


    /**
     *  Introspects the specified class, per the rules above.
     *
     *  @throws IntrospectionException on any error (this will always wrap
     *          an underlying exception, typically one of the checked exceptions
     *          thrown by the reflection mechanism).
     */
    public Introspection(Class<?> klass)
    {
        this(klass, false);
    }


    /**
     *  Introspects the specified class, per the rules above. Optionally sets
     *  each introspected method as accessible. This avoids exceptions caused
     *  by public methods in private classes, but will throw a security
     *  exception if running in a sandbox.
     *
     *  @since 1.0.12
     */
    public Introspection(Class<?> klass, boolean setAccessible)
    {
        _setAccessible = setAccessible;
        _propNames = new HashSet<String>();
        _propNamesPublic = Collections.unmodifiableSet(_propNames);
        _getters = new HashMap<String,Method>();
        _setters = new HashMap<String,Method>();

        introspect(klass);
    }

//----------------------------------------------------------------------------
//  Public Methods
//----------------------------------------------------------------------------

    /**
     *  Returns the property names for the specified class. These names are
     *  generated from getter methods -- any method beginning with "get"
     *  or "is". The returned set is unmodifiable, and will be empty if
     *  there are no properties with bean-style getter methods.
     *  <p>
     *  Names are processed by <code>Introspector.decapitalize()</code>, so
     *  will be consistent with the bean specification.
     */
    public Set<String> propertyNames()
    {
        return _propNamesPublic;
    }


    /**
     *  Returns the getter method for the named property, <code>null</code>
     *  no method is known (all properties returned by {@link #propertyNames}
     *  must have getters, but may not have setters).
     */
    public Method getter(String propName)
    {
        return _getters.get(propName.toLowerCase());
    }


    /**
     *  Returns the setter method for the named property, <code>null</code>
     *  if unable to find a method.
     */
    public Method setter(String propName)
    {
        return _setters.get(propName.toLowerCase());
    }


    /**
     *  Returns the type of the named property, taken from the return type
     *  of the property's getter. Will be <code>null</code> if the property
     *  isn't known.
     */
    public Class<?> type(String propName)
    {
        Method getter = getter(propName);
        return (getter == null)
             ? null
             : getter.getReturnType();
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private void introspect(Class<?> klass)
    {
        try
        {
            for (Method method : klass.getMethods())
            {
                if (method.getDeclaringClass() == Object.class)
                    continue;

                String methodName = method.getName();
                int paramCount = method.getParameterTypes().length;

                if ((methodName.startsWith("get")) && (paramCount == 0))
                {
                    String propName = extractAndSavePropName(methodName, 3);
                    saveGetter(propName, method);
                }
                else if ((methodName.startsWith("is")) && (paramCount == 0))
                {
                    String propName = extractAndSavePropName(methodName, 2);
                    saveGetter(propName, method);
                }
                else if ((methodName.startsWith("set")) && (paramCount == 1))
                {
                    String propName = extractAndSavePropName(methodName, 3);
                    saveSetter(propName, method);
                }
            }
        }
        catch (Exception ee)
        {
            throw new IntrospectionException("unable to introspect", ee);
        }
    }


    private String extractAndSavePropName(String methodName, int pos)
    {
        String propName = methodName.substring(pos);
        _propNames.add(Introspector.decapitalize(propName));
        return propName.toLowerCase();
    }


    private void saveGetter(String propName, Method method)
    {
        if (_setAccessible)
            method.setAccessible(true);

        Method existing = _getters.get(propName);
        if (existing == null)
        {
            _getters.put(propName, method);
            return;
        }

        Class<?> methodClass = method.getReturnType();
        Class<?> existingClass = existing.getReturnType();
        if (existingClass.isAssignableFrom(methodClass))
        {
            _getters.put(propName, method);
            return;
        }
    }


    private void saveSetter(String propName, Method method)
    {
        if (_setAccessible)
            method.setAccessible(true);

        Method existing = _setters.get(propName);
        if (existing == null)
        {
            _setters.put(propName, method);
            return;
        }

        Class<?> methodClass = method.getDeclaringClass();
        Class<?> existingClass = existing.getDeclaringClass();
        if (!existingClass.isAssignableFrom(methodClass))
            return; // existing is subclass, keep it

        if (methodClass != existingClass)
        {
            // existing is superclass, take subclass
            _setters.put(propName, method);
            return;
        }

        if (setterRank(method) < setterRank(existing))
        {
            _setters.put(propName, method);
            return;
        }
    }


    private static int setterRank(Method method)
    {
        Class<?> parmClass = method.getParameterTypes()[0];
        if (parmClass.isPrimitive())
            return 1;
        if (Number.class.isAssignableFrom(parmClass))
            return 2;
        if (String.class.isAssignableFrom(parmClass))
            return 3;
        return 4;
    }
}
