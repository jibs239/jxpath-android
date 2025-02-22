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

import com.mockrunner.mock.web.*;
import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;

import javax.servlet.ServletContext;
import java.util.Iterator;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class JXPathServletContextTest extends TestCase {

    private ServletContext getServletContext() {
        MockServletContext context = new MockServletContext();
        context.setAttribute("app", "OK");

        return context;
    }

    public void testServletContext() {
        ServletContext context = getServletContext();
        JXPathContext appContext = JXPathServletContexts.getApplicationContext(context);

        assertSame("Cached context not property returned", appContext, JXPathServletContexts.getApplicationContext(context));

        assertEquals("Application Context", "OK", appContext.getValue("app"));

        checkPointerIterator(appContext);

        // test setting a value in the context
        appContext.setValue("/foo", "bar");
        assertEquals("Context property", "bar", appContext.getValue("/foo"));

        // test the variables
        Variables variables = appContext.getVariables();
        assertNotNull("$application variable", variables.getVariable("application"));
        assertNull("$foo variable", variables.getVariable("$foo"));
    }

    public void testServletRequest() {
        ServletContext context = getServletContext();

        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        session.setUpIsNew(true);
        Integer count = new Integer(10);
        session.setAttribute("count", count);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setAttribute("attr", "OK");
        request.setupAddParameter("parm", "OK");
        request.setupAddParameter("multiparam", new String[]{"value1", "value2"});
        request.setupAddParameter("emptyparam", new String[0]);

        assertSame("Request session", session, request.getSession());

        JXPathContext reqContext = JXPathServletContexts.getRequestContext(request, context);

        assertSame("Cached context not property returned", reqContext, JXPathServletContexts.getRequestContext(request, context));

        JXPathContext sessionContext = JXPathServletContexts.getSessionContext(session, context);

        assertSame("Cached context not property returned", sessionContext, JXPathServletContexts.getSessionContext(session, context));

        assertEquals("Request Context Attribute", "OK", reqContext.getValue("attr"));

        assertEquals("Request Context Parameter", "OK", reqContext.getValue("parm"));
        assertTrue("Request Context Parameter (Array)", reqContext.getValue("multiparam").getClass().isArray());
        assertEquals("Request Context Parameter (Empty)", null, reqContext.getValue("emptyparam"));

        assertEquals("Session Context Parameter", count, sessionContext.getValue("count"));
        assertEquals("Application Context via Request Context", "OK", reqContext.getValue("app"));
        assertEquals("Session Context via Request Context", count, reqContext.getValue("count"));
        assertEquals("Application Context via Session Context", "OK", sessionContext.getValue("app"));

        checkPointerIterator(reqContext);
        checkPointerIterator(sessionContext);

        // test setting a value in the context
        reqContext.setValue("/foo1", "bar1");
        assertEquals("Context property", "bar1", reqContext.getValue("/foo1"));

        sessionContext.setValue("/foo2", "bar2");
        assertEquals("Context property", "bar2", sessionContext.getValue("/foo2"));
    }

    public void testServletRequestWithoutSession() {
        ServletContext context = getServletContext();

        MockHttpServletRequest request = new MockHttpServletRequest();

        JXPathContext reqContext = JXPathServletContexts.getRequestContext(request, context);

        assertEquals("Application Context via Request Context", "OK", reqContext.getValue("app"));
    }

    private void checkPointerIterator(JXPathContext context) {
        Iterator it = context.iteratePointers("/*");
        assertTrue("Empty context", it.hasNext());
        while (it.hasNext()) {
            Pointer pointer = (Pointer) it.next();
            assertNotNull("null pointer", pointer);
            assertNotNull("null path", pointer.asPath());
        }
    }

    public void testPageContext() {
        MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute("app", "app");

        MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.setServletContext(servletContext);

        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(servletContext);
        session.setAttribute("session", "session");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("request", "request");
        request.setSession(session);

        MockPageContext pageContext = new MockPageContext();
        pageContext.setServletConfig(servletConfig);
        pageContext.setServletRequest(request);
        pageContext.setAttribute("page", "page");

        assertSame("Request session", session, request.getSession());


        JXPathContext context = JXPathServletContexts.getPageContext(pageContext);
        context.setLenient(true);

        checkPointerIterator(context);

        assertEquals("Page Scope", "page", context.getValue("page"));
        assertEquals("Request Scope", "request", context.getValue("request"));
        assertEquals("Session Scope", "session", context.getValue("session"));
        assertEquals("Application Scope", "app", context.getValue("app"));

        assertEquals("Explicit Page Scope", "page", context.getValue("$page/page"));
        assertEquals("Explicit Request Scope", "request", context.getValue("$request/request"));
        assertEquals("Explicit Session Scope", "session", context.getValue("$session/session"));
        assertEquals("Explicit Application Scope", "app", context.getValue("$application/app"));

        // iterate through the elements of page context only (two elements expected, 'page' and the context)
        Iterator it = context.iteratePointers("$page/*");
        assertTrue("element not found", it.hasNext());
        it.next();
        it.next();
        assertFalse("too many elements", it.hasNext());

        // test setting a value in the context
        context.setValue("/foo1", "bar1");
        assertEquals("Context property", "bar1", context.getValue("/foo1"));

        context.setValue("$page/foo2", "bar2");
        assertEquals("Context property", "bar2", context.getValue("$page/foo2"));
    }
}
