/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.compass.beanutils;


import org.compass.android.beans.IntrospectionException;
import org.compass.android.beans.PropertyDescriptor;

import java.lang.reflect.Method;
import java.util.Locale;


/**
 * <p>
 * An implementation of the <code>BeanIntrospector</code> interface which can
 * detect write methods for properties used in fluent API scenario.
 * </p>
 * <p>
 * A <em>fluent API</em> allows setting multiple properties using a single
 * statement by supporting so-called <em>method chaining</em>: Methods for
 * setting a property value do not return <b>void</b>, but an object which can
 * be called for setting another property. An example of such a fluent API could
 * look as follows:
 *
 * <pre>
 * public class FooBuilder {
 *     public FooBuilder setFooProperty1(String value) {
 *        ...
 *        return this;
 *    }
 *
 *     public FooBuilder setFooProperty2(int value) {
 *        ...
 *        return this;
 *    }
 * }
 * </pre>
 * <p>
 * Per default, <code>PropertyUtils</code> does not detect methods like this
 * because, having a non-<b>void</b> return type, they violate the Java Beans
 * specification.
 * </p>
 * <p>
 * This class is more tolerant with regards to the return type of a set method.
 * It basically iterates over all methods of a class and filters them for a
 * configurable prefix (the default prefix is <code>set</code>). It then
 * generates corresponding <code>PropertyDescriptor</code> objects for the
 * methods found which use these methods as write methods.
 * </p>
 * <p>
 * An instance of this class is intended to collaborate with a
 * {@link DefaultBeanIntrospector} object. So best results are achieved by
 * adding this instance as custom {@code BeanIntrospector} after the
 * <code>DefaultBeanIntrospector</code> object. Then default introspection finds
 * read-only properties because it does not detect the write methods with a
 * non-<b>void</b> return type. {@code FluentPropertyBeanIntrospector}
 * completes the descriptors for these properties by setting the correct write
 * method.
 * </p>
 *
 * @version $Id: FluentPropertyBeanIntrospector.java 1540359 2013-11-09 18:10:52Z oheger $
 * @since 1.9
 */
public class FluentPropertyBeanIntrospector implements BeanIntrospector {
    /**
     * The default prefix for write methods.
     */
    public static final String DEFAULT_WRITE_METHOD_PREFIX = "set";

    /**
     * The prefix of write methods to search for.
     */
    private final String writeMethodPrefix;

    /**
     * Creates a new instance of <code>FluentPropertyBeanIntrospector</code> and
     * initializes it with the prefix for write methods used by the classes to
     * be inspected.
     *
     * @param writePrefix the prefix for write methods (must not be <b>null</b>)
     * @throws IllegalArgumentException if the prefix is <b>null</b>
     */
    public FluentPropertyBeanIntrospector(String writePrefix) {
        if (writePrefix == null) {
            throw new IllegalArgumentException(
                    "Prefix for write methods must not be null!");
        }
        writeMethodPrefix = writePrefix;
    }

    /**
     * Creates a new instance of <code>FluentPropertyBeanIntrospector</code> and
     * sets the default prefix for write methods.
     */
    public FluentPropertyBeanIntrospector() {
        this(DEFAULT_WRITE_METHOD_PREFIX);
    }

    /**
     * Returns the prefix for write methods this instance scans for.
     *
     * @return the prefix for write methods
     */
    public String getWriteMethodPrefix() {
        return writeMethodPrefix;
    }

    /**
     * Performs introspection. This method scans the current class's methods for
     * property write methods which have not been discovered by default
     * introspection.
     *
     * @param icontext the introspection context
     * @throws IntrospectionException if an error occurs
     */
    public void introspect(IntrospectionContext icontext)
            throws IntrospectionException {
        for (Method m : icontext.getTargetClass().getMethods()) {
            if (m.getName().startsWith(getWriteMethodPrefix())) {
                String propertyName = propertyName(m);
                PropertyDescriptor pd = icontext
                        .getPropertyDescriptor(propertyName);
                try {
                    if (pd == null) {
                        icontext.addPropertyDescriptor(createFluentPropertyDescritor(
                                m, propertyName));
                    } else if (pd.getWriteMethod() == null) {
                        pd.setWriteMethod(m);
                    }
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Derives the name of a property from the given set method.
     *
     * @param m the method
     * @return the corresponding property name
     */
    private String propertyName(Method m) {
        String methodName = m.getName().substring(
                getWriteMethodPrefix().length());
        return (methodName.length() > 1) ? Character.toLowerCase(methodName
                .charAt(0)) + methodName.substring(1) : methodName
                .toLowerCase(Locale.ENGLISH);
    }

    /**
     * Creates a property descriptor for a fluent API property.
     *
     * @param m            the set method for the fluent API property
     * @param propertyName the name of the corresponding property
     * @return the descriptor
     * @throws IntrospectionException if an error occurs
     */
    private PropertyDescriptor createFluentPropertyDescritor(Method m,
                                                             String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(propertyName(m), null, m);
    }
}