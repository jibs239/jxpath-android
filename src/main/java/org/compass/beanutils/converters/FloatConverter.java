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

/**
 * {@link NumberConverter} implementation that handles conversion to
 * and from <b>java.lang.Float</b> objects.
 * <p>
 * This implementation can be configured to handle conversion either
 * by using Float's default String conversion, or by using a Locale's pattern
 * or by specifying a format pattern. See the {@link NumberConverter}
 * documentation for further details.
 * <p>
 * Can be configured to either return a <i>default value</i> or throw a
 * <code>ConversionException</code> if a conversion error occurs.
 *
 * @version $Id: FloatConverter.java 1540186 2013-11-08 21:08:30Z oheger $
 * @since 1.3
 */
public final class FloatConverter extends NumberConverter {

    /**
     * Construct a <b>java.lang.Float</b> <i>Converter</i> that throws
     * a <code>ConversionException</code> if an error occurs.
     */
    public FloatConverter() {
        super(true);
    }

    /**
     * Construct a <b>java.lang.Float</b> <i>Converter</i> that returns
     * a default value if an error occurs.
     *
     * @param defaultValue The default value to be returned
     *                     if the value to be converted is missing or an error
     *                     occurs converting the value.
     */
    public FloatConverter(Object defaultValue) {
        super(true, defaultValue);
    }

    /**
     * Return the default type this <code>Converter</code> handles.
     *
     * @return The default type this <code>Converter</code> handles.
     * @since 1.8.0
     */
    @Override
    protected Class<Float> getDefaultType() {
        return Float.class;
    }

}
