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

import javax.servlet.jsp.PageContext;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Implementation of the {@link DynamicPropertyHandler} interface that provides
 * access to attributes of a {@link PageContext} in all scopes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class PageContextHandler implements DynamicPropertyHandler {

    public String[] getPropertyNames(Object pageContext) {
        HashSet list = new HashSet();
        Enumeration e =
                ((PageContext) pageContext).getAttributeNamesInScope(
                        PageContext.PAGE_SCOPE);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        e =
                ((PageContext) pageContext).getAttributeNamesInScope(
                        PageContext.REQUEST_SCOPE);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        e =
                ((PageContext) pageContext).getAttributeNamesInScope(
                        PageContext.SESSION_SCOPE);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        e =
                ((PageContext) pageContext).getAttributeNamesInScope(
                        PageContext.APPLICATION_SCOPE);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Returns <code>pageContext.findAttribute(property)</code>.
     *
     * @param pageContext to search
     * @param property    name
     * @return Object value
     */
    public Object getProperty(Object pageContext, String property) {
        return ((PageContext) pageContext).findAttribute(property);
    }

    public void setProperty(Object pageContext, String property, Object value) {
        ((PageContext) pageContext).setAttribute(
                property,
                value,
                PageContext.PAGE_SCOPE);
    }
}
