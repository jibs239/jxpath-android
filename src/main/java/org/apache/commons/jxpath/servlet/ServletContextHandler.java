/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.servlet;

import org.apache.commons.jxpath.DynamicPropertyHandler;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the {@link DynamicPropertyHandler} interface that provides
 * access to attributes of a {@link ServletContext}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class ServletContextHandler implements DynamicPropertyHandler {

    private static final int DEFAULT_PROPERTY_COUNT = 16;

    public String[] getPropertyNames(Object context) {
        Set list = new HashSet(DEFAULT_PROPERTY_COUNT);
        collectPropertyNames(list, context);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Collect the property names from bean, storing in set.
     *
     * @param set  destination
     * @param bean to read
     */
    protected void collectPropertyNames(Set set, Object bean) {
        if (bean instanceof HttpSessionAndServletContext) {
            bean = ((HttpSessionAndServletContext) bean).getServletContext();
        }
        Enumeration e = ((ServletContext) bean).getAttributeNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
    }

    public Object getProperty(Object context, String property) {
        return ((ServletContext) context).getAttribute(property);
    }

    public void setProperty(Object context, String property, Object value) {
        ((ServletContext) context).setAttribute(property, value);
    }
}
