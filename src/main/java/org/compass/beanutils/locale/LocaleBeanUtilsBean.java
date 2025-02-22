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

package org.compass.beanutils.locale;


import org.compass.android.beans.IndexedPropertyDescriptor;
import org.compass.android.beans.PropertyDescriptor;
import org.compass.beanutils.*;
import org.compass.beanutils.expression.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


/**
 * <p>Utility methods for populating JavaBeans properties
 * via reflection in a locale-dependent manner.</p>
 *
 * @version $Id: LocaleBeanUtilsBean.java 1546738 2013-11-30 16:24:19Z oheger $
 * @since 1.7
 */

public class LocaleBeanUtilsBean extends BeanUtilsBean {

    /**
     * Contains <code>LocaleBeanUtilsBean</code> instances indexed by context classloader.
     */
    private static final ContextClassLoaderLocal<LocaleBeanUtilsBean>
            LOCALE_BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<LocaleBeanUtilsBean>() {
        // Creates the default instance used when the context classloader is unavailable
        @Override
        protected LocaleBeanUtilsBean initialValue() {
            return new LocaleBeanUtilsBean();
        }
    };
    /**
     * Convertor used by this class
     */
    private final LocaleConvertUtilsBean localeConvertUtils;

    /**
     * Construct instance with standard conversion bean
     */
    public LocaleBeanUtilsBean() {
        this.localeConvertUtils = new LocaleConvertUtilsBean();
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this <code>localeConvertUtils</code> to perform
     *                           conversions
     * @param convertUtilsBean   use this for standard conversions
     * @param propertyUtilsBean  use this for property conversions
     */
    public LocaleBeanUtilsBean(
            LocaleConvertUtilsBean localeConvertUtils,
            ConvertUtilsBean convertUtilsBean,
            PropertyUtilsBean propertyUtilsBean) {
        super(convertUtilsBean, propertyUtilsBean);
        this.localeConvertUtils = localeConvertUtils;
    }

    // --------------------------------------------------------- Constructors

    /**
     * Construct instance that uses given locale conversion
     *
     * @param localeConvertUtils use this <code>localeConvertUtils</code> to perform
     *                           conversions
     */
    public LocaleBeanUtilsBean(LocaleConvertUtilsBean localeConvertUtils) {
        this.localeConvertUtils = localeConvertUtils;
    }

    /**
     * Gets singleton instance
     *
     * @return the singleton instance
     */
    public static LocaleBeanUtilsBean getLocaleBeanUtilsInstance() {
        return LOCALE_BEANS_BY_CLASSLOADER.get();
    }

    /**
     * Sets the instance which provides the functionality for {@link LocaleBeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @param newInstance a new singleton instance
     */
    public static void setInstance(LocaleBeanUtilsBean newInstance) {
        LOCALE_BEANS_BY_CLASSLOADER.set(newInstance);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Gets the bean instance used for conversions
     *
     * @return the locale converter bean instance
     */
    public LocaleConvertUtilsBean getLocaleConvertUtils() {
        return localeConvertUtils;
    }

    /**
     * Gets the default Locale
     *
     * @return the default locale
     */
    public Locale getDefaultLocale() {

        return getLocaleConvertUtils().getDefaultLocale();
    }


    /**
     * Sets the default Locale.
     *
     * @param locale the default locale
     */
    public void setDefaultLocale(Locale locale) {

        getLocaleConvertUtils().setDefaultLocale(locale);
    }

    /**
     * Is the pattern to be applied localized
     * (Indicate whether the pattern is localized or not)
     *
     * @return <code>true</code> if pattern is localized,
     * otherwise <code>false</code>
     */
    public boolean getApplyLocalized() {

        return getLocaleConvertUtils().getApplyLocalized();
    }

    /**
     * Sets whether the pattern is applied localized
     * (Indicate whether the pattern is localized or not)
     *
     * @param newApplyLocalized <code>true</code> if pattern is localized,
     *                          otherwise <code>false</code>
     */
    public void setApplyLocalized(boolean newApplyLocalized) {

        getLocaleConvertUtils().setApplyLocalized(newApplyLocalized);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String. The zero-relative index of the
     * required value must be included (in square brackets) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    <code>propertyname[index]</code> of the property value
     *                to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   propety cannot be found
     */
    public String getIndexedProperty(
            Object bean,
            String name,
            String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}. The zero-relative index
     * of the required value must be included (in square brackets) as a suffix
     * to the property name, or <code>IllegalArgumentException</code> will be thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname[index]</code> of the property value
     *             to be extracted
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   propety cannot be found
     */
    @Override
    public String getIndexedProperty(
            Object bean,
            String name)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        return getIndexedProperty(bean, name, null);
    }

    /**
     * Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the specified conversion pattern.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    Simple property name of the property value to be extracted
     * @param index   Index of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   propety cannot be found
     */
    public String getIndexedProperty(Object bean,
                                     String name, int index, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getIndexedProperty(bean, name, index);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified locale-sensetive indexed property
     * of the specified bean, as a String using the default conversion pattern of
     * the corresponding {@link LocaleConverter}.
     * The index is specified as a method parameter and
     * must *not* be included in the property name expression
     *
     * @param bean  Bean whose property is to be extracted
     * @param name  Simple property name of the property value to be extracted
     * @param index Index of the property value to be extracted
     * @return The indexed property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   propety cannot be found
     */
    @Override
    public String getIndexedProperty(Object bean,
                                     String name, int index)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return getIndexedProperty(bean, name, index, null);
    }

    /**
     * Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the specified
     * conversion pattern.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    Name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public String getSimpleProperty(Object bean, String name, String pattern)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getSimpleProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified simple locale-sensitive property
     * of the specified bean, converted to a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Name of the property to be extracted
     * @return The property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Override
    public String getSimpleProperty(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return getSimpleProperty(bean, name, null);
    }

    /**
     * Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String using the specified conversion pattern.
     * The key is specified as a method parameter and must *not* be included in
     * the property name expression.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    Simple property name of the property value to be extracted
     * @param key     Lookup key of the property value to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public String getMappedProperty(
            Object bean,
            String name,
            String key,
            String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name, key);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the specified mapped locale-sensitive property
     * of the specified bean, as a String
     * The key is specified as a method parameter and must *not* be included
     * in the property name expression
     *
     * @param bean Bean whose property is to be extracted
     * @param name Simple property name of the property value to be extracted
     * @param key  Lookup key of the property value to be extracted
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Override
    public String getMappedProperty(Object bean,
                                    String name, String key)
            throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {

        return getMappedProperty(bean, name, key, null);
    }


    /**
     * Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the specified pattern.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    <code>propertyname(index)</code> of the property value
     *                to be extracted
     * @param pattern The conversion pattern
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public String getMappedPropertyLocale(
            Object bean,
            String name,
            String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getMappedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }


    /**
     * Return the value of the specified locale-sensitive mapped property
     * of the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     * The String-valued key of the required value
     * must be included (in parentheses) as a suffix to
     * the property name, or <code>IllegalArgumentException</code> will be
     * thrown.
     *
     * @param bean Bean whose property is to be extracted
     * @param name <code>propertyname(index)</code> of the property value
     *             to be extracted
     * @return The mapped property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Override
    public String getMappedProperty(Object bean, String name)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        return getMappedPropertyLocale(bean, name, null);
    }

    /**
     * Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean,
     * as a String using the specified pattern.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    Possibly nested name of the property to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if a nested reference to a
     *                                   property returns null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public String getNestedProperty(
            Object bean,
            String name,
            String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        Object value = getPropertyUtils().getNestedProperty(bean, name);
        return getLocaleConvertUtils().convert(value, pattern);
    }

    /**
     * Return the value of the (possibly nested) locale-sensitive property
     * of the specified name, for the specified bean, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly nested name of the property to be extracted
     * @return The nested property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if a nested reference to a
     *                                   property returns null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Override
    public String getNestedProperty(Object bean, String name)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        return getNestedProperty(bean, name, null);
    }

    /**
     * Return the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the specified conversion pattern.
     *
     * @param bean    Bean whose property is to be extracted
     * @param name    Possibly indexed and/or nested name of the property
     *                to be extracted
     * @param pattern The conversion pattern
     * @return The nested property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public String getProperty(Object bean, String name, String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        return getNestedProperty(bean, name, pattern);
    }

    /**
     * Return the value of the specified locale-sensitive property
     * of the specified bean, no matter which property reference
     * format is used, as a String using the default
     * conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *             to be extracted
     * @return The property's value, converted to a String
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Override
    public String getProperty(Object bean, String name)
            throws
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {

        return getNestedProperty(bean, name);
    }

    /**
     * Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination property
     * using the default conversion pattern of the corresponding {@link LocaleConverter}.
     *
     * @param bean  Bean on which setting is to be performed
     * @param name  Property name (can be nested/indexed/mapped/combo)
     * @param value Value to be set
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     */
    @Override
    public void setProperty(Object bean, String name, Object value)
            throws
            IllegalAccessException,
            InvocationTargetException {

        setProperty(bean, name, value, null);
    }

    /**
     * Set the specified locale-sensitive property value, performing type
     * conversions as required to conform to the type of the destination
     * property using the specified conversion pattern.
     *
     * @param bean    Bean on which setting is to be performed
     * @param name    Property name (can be nested/indexed/mapped/combo)
     * @param value   Value to be set
     * @param pattern The conversion pattern
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     */
    public void setProperty(
            Object bean,
            String name,
            Object value,
            String pattern)
            throws
            IllegalAccessException,
            InvocationTargetException {

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (NoSuchMethodException e) {
                return; // Skip this property setter
            }
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        int index = resolver.getIndex(name);         // Indexed subscript value (if any)
        String key = resolver.getKey(name);           // Mapped key value (if any)

        Class<?> type = definePropertyType(target, name, propName);
        if (type != null) {
            Object newValue = convert(type, index, value, pattern);
            invokeSetter(target, propName, key, index, newValue);
        }
    }

    /**
     * Calculate the property type.
     *
     * @param target   The bean
     * @param name     The property name
     * @param propName The Simple name of target property
     * @return The property's type
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     */
    protected Class<?> definePropertyType(Object target, String name, String propName)
            throws IllegalAccessException, InvocationTargetException {

        Class<?> type = null;               // Java type of target property

        if (target instanceof DynaBean) {
            DynaClass dynaClass = ((DynaBean) target).getDynaClass();
            DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
            if (dynaProperty == null) {
                return null; // Skip this property setter
            }
            type = dynaProperty.getType();
        } else {
            PropertyDescriptor descriptor = null;
            try {
                descriptor =
                        getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return null; // Skip this property setter
                }
            } catch (NoSuchMethodException e) {
                return null; // Skip this property setter
            }
            if (descriptor instanceof MappedPropertyDescriptor) {
                type = ((MappedPropertyDescriptor) descriptor).
                        getMappedPropertyType();
            } else if (descriptor instanceof IndexedPropertyDescriptor) {
                type = ((IndexedPropertyDescriptor) descriptor).
                        getIndexedPropertyType();
            } else {
                type = descriptor.getPropertyType();
            }
        }
        return type;
    }

    /**
     * Convert the specified value to the required type using the
     * specified conversion pattern.
     *
     * @param type    The Java type of target property
     * @param index   The indexed subscript value (if any)
     * @param value   The value to be converted
     * @param pattern The conversion pattern
     * @return The converted value
     */
    protected Object convert(Class<?> type, int index, Object value, String pattern) {

        Object newValue = null;

        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value instanceof String) {
                String[] values = new String[1];
                values[0] = (String) value;
                newValue = getLocaleConvertUtils().convert(values, type, pattern);
            } else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert((String[]) value, type, pattern);
            } else {
                newValue = value;
            }
        } else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String) {
                newValue = getLocaleConvertUtils().convert((String) value,
                        type.getComponentType(), pattern);
            } else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
                        type.getComponentType(), pattern);
            } else {
                newValue = value;
            }
        } else {                             // Value into scalar
            if (value instanceof String) {
                newValue = getLocaleConvertUtils().convert((String) value, type, pattern);
            } else if (value instanceof String[]) {
                newValue = getLocaleConvertUtils().convert(((String[]) value)[0],
                        type, pattern);
            } else {
                newValue = value;
            }
        }
        return newValue;
    }

    /**
     * Convert the specified value to the required type.
     *
     * @param type  The Java type of target property
     * @param index The indexed subscript value (if any)
     * @param value The value to be converted
     * @return The converted value
     */
    protected Object convert(Class<?> type, int index, Object value) {

        Object newValue = null;

        if (type.isArray() && (index < 0)) { // Scalar value into array
            if (value instanceof String) {
                String[] values = new String[1];
                values[0] = (String) value;
                newValue = ConvertUtils.convert(values, type);
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert((String[]) value, type);
            } else {
                newValue = value;
            }
        } else if (type.isArray()) {         // Indexed value into array
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String) value,
                        type.getComponentType());
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                        type.getComponentType());
            } else {
                newValue = value;
            }
        } else {                             // Value into scalar
            if (value instanceof String) {
                newValue = ConvertUtils.convert((String) value, type);
            } else if (value instanceof String[]) {
                newValue = ConvertUtils.convert(((String[]) value)[0],
                        type);
            } else {
                newValue = value;
            }
        }
        return newValue;
    }

    /**
     * Invoke the setter method.
     *
     * @param target   The bean
     * @param propName The Simple name of target property
     * @param key      The Mapped key value (if any)
     * @param index    The indexed subscript value (if any)
     * @param newValue The value to be set
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     */
    protected void invokeSetter(Object target, String propName, String key, int index, Object newValue)
            throws IllegalAccessException, InvocationTargetException {

        try {
            if (index >= 0) {
                getPropertyUtils().setIndexedProperty(target, propName,
                        index, newValue);
            } else if (key != null) {
                getPropertyUtils().setMappedProperty(target, propName,
                        key, newValue);
            } else {
                getPropertyUtils().setProperty(target, propName, newValue);
            }
        } catch (NoSuchMethodException e) {
            throw new InvocationTargetException
                    (e, "Cannot set " + propName);
        }
    }

    /**
     * Resolve any nested expression to get the actual target property.
     *
     * @param bean The bean
     * @param name The property name
     * @return The property's descriptor
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @deprecated Property name expressions are now processed by
     * the configured {@link Resolver} implementation and this method
     * is no longer used by BeanUtils.
     */
    @Deprecated
    protected Descriptor calculate(Object bean, String name)
            throws IllegalAccessException, InvocationTargetException {

        // Resolve any nested expression to get the actual target bean
        Object target = bean;
        Resolver resolver = getPropertyUtils().getResolver();
        while (resolver.hasNested(name)) {
            try {
                target = getPropertyUtils().getProperty(target, resolver.next(name));
                name = resolver.remove(name);
            } catch (NoSuchMethodException e) {
                return null; // Skip this property setter
            }
        }

        // Declare local variables we will require
        String propName = resolver.getProperty(name); // Simple name of target property
        int index = resolver.getIndex(name);         // Indexed subscript value (if any)
        String key = resolver.getKey(name);           // Mapped key value (if any)

        return new Descriptor(target, name, propName, key, index);
    }

    /**
     * @deprecated Property name expressions are now processed by
     * the configured {@link Resolver} implementation and this class
     * is no longer used by BeanUtils.
     */
    @Deprecated
    protected class Descriptor {

        private int index = -1;    // Indexed subscript value (if any)
        private String name;
        private String propName;   // Simple name of target property
        private String key;        // Mapped key value (if any)
        private Object target;

        /**
         * Construct a descriptor instance for the target bean and property.
         *
         * @param target   The target bean
         * @param name     The property name (includes indexed/mapped expr)
         * @param propName The property name
         * @param key      The mapped property key (if any)
         * @param index    The indexed property index (if any)
         */
        public Descriptor(Object target, String name, String propName, String key, int index) {

            setTarget(target);
            setName(name);
            setPropName(propName);
            setKey(key);
            setIndex(index);
        }

        /**
         * Return the target bean.
         *
         * @return The descriptors target bean
         */
        public Object getTarget() {
            return target;
        }

        /**
         * Set the target bean.
         *
         * @param target The target bean
         */
        public void setTarget(Object target) {
            this.target = target;
        }

        /**
         * Return the mapped property key.
         *
         * @return the mapped property key (if any)
         */
        public String getKey() {
            return key;
        }

        /**
         * Set the mapped property key.
         *
         * @param key The mapped property key (if any)
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Return indexed property index.
         *
         * @return indexed property index (if any)
         */
        public int getIndex() {
            return index;
        }

        /**
         * Set the indexed property index.
         *
         * @param index The indexed property index (if any)
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Return property name (includes indexed/mapped expr).
         *
         * @return The property name (includes indexed/mapped expr)
         */
        public String getName() {
            return name;
        }

        /**
         * Set the property name (includes indexed/mapped expr).
         *
         * @param name The property name (includes indexed/mapped expr)
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Return the property name.
         *
         * @return The property name
         */
        public String getPropName() {
            return propName;
        }

        /**
         * Set the property name.
         *
         * @param propName The property name
         */
        public void setPropName(String propName) {
            this.propName = propName;
        }
    }
}


