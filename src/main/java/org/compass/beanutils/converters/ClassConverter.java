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
package org.compass.beanutils.converters;

import org.compass.beanutils.Converter;

/**
 * {@link Converter} implementaion that handles conversion
 * to and from <b>java.lang.Class</b> objects.
 * <p>
 * The class will be loaded from the thread context class
 * loader (if it exists); otherwise the class loader that loaded this class
 * will be used.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @version $Id: ClassConverter.java 1540518 2013-11-10 19:04:04Z oheger $
 * @since 1.4
 */
public final class ClassConverter extends AbstractConverter {

    /**
     * Construct a <b>java.lang.Class</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public ClassConverter() {
        super();
    }

    /**
     * Construct a <b>java.lang.Class</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public ClassConverter(Object defaultValue) {
        super(defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class<?> getDefaultType() {
        return Class.class;
    }

    /**
     * <p>Convert a java.lang.Class or object into a String.</p>
     *
     * @param value The input value to be converted
     * @return the converted String value.
     * @since 1.8.0
     */
    @Override
    protected String convertToString(Object value) {
        return (value instanceof Class) ? ((Class<?>) value).getName() : value.toString();
    }

    /**
     * <p>Convert the input object into a java.lang.Class.</p>
     *
     * @param <T>   Target type of the conversion.
     * @param type  Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     * @since 1.8.0
     */
    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (Class.class.equals(type)) {
            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            if (classLoader != null) {
                try {
                    return type.cast(classLoader.loadClass(value.toString()));
                } catch (ClassNotFoundException ex) {
                    // Don't fail, carry on and try this class's class loader
                    // (see issue# BEANUTILS-263)
                }
            }

            // Try this class's class loader
            classLoader = ClassConverter.class.getClassLoader();
            return type.cast(classLoader.loadClass(value.toString()));
        }

        throw conversionException(type, value);
    }

}
