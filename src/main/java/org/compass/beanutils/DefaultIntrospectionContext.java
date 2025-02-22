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


import org.compass.android.beans.PropertyDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * An implementation of the {@code IntrospectionContext} interface used by
 * {@link PropertyUtilsBean} when doing introspection of a bean class.
 * </p>
 * <p>
 * This class implements the methods required by the
 * {@code IntrospectionContext} interface in a straight-forward manner
 * based on a map. It is used internally only. It is not thread-safe.
 * </p>
 *
 * @version $Id: DefaultIntrospectionContext.java 1540359 2013-11-09 18:10:52Z oheger $
 * @since 1.9
 */
class DefaultIntrospectionContext implements IntrospectionContext {
    /**
     * Constant for an empty array of property descriptors.
     */
    private static final PropertyDescriptor[] EMPTY_DESCRIPTORS = new PropertyDescriptor[0];

    /**
     * The current class for introspection.
     */
    private final Class<?> currentClass;

    /**
     * A map for storing the already added property descriptors.
     */
    private final Map<String, PropertyDescriptor> descriptors;

    /**
     * Creates a new instance of <code>DefaultIntrospectionContext</code> and sets
     * the current class for introspection.
     *
     * @param cls the current class
     */
    public DefaultIntrospectionContext(Class<?> cls) {
        currentClass = cls;
        descriptors = new HashMap<String, PropertyDescriptor>();
    }

    public Class<?> getTargetClass() {
        return currentClass;
    }

    public void addPropertyDescriptor(PropertyDescriptor desc) {
        if (desc == null) {
            throw new IllegalArgumentException(
                    "Property descriptor must not be null!");
        }
        descriptors.put(desc.getName(), desc);
    }

    public void addPropertyDescriptors(PropertyDescriptor[] descs) {
        if (descs == null) {
            throw new IllegalArgumentException(
                    "Array with descriptors must not be null!");
        }

        for (int i = 0; i < descs.length; i++) {
            addPropertyDescriptor(descs[i]);
        }
    }

    public boolean hasProperty(String name) {
        return descriptors.containsKey(name);
    }

    public PropertyDescriptor getPropertyDescriptor(String name) {
        return descriptors.get(name);
    }

    public void removePropertyDescriptor(String name) {
        descriptors.remove(name);
    }

    public Set<String> propertyNames() {
        return descriptors.keySet();
    }

    /**
     * Returns an array with all descriptors added to this context. This method
     * is used to obtain the results of introspection.
     *
     * @return an array with all known property descriptors
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return descriptors.values().toArray(EMPTY_DESCRIPTORS);
    }
}
