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
package org.apache.commons.jxpath.issues;

import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;

import java.util.HashMap;
import java.util.Map;

public class JXPath177Test extends TestCase {
    Map model = new HashMap();

    {

        model.put("name", "ROOT name");
        final HashMap x = new HashMap();
        model.put("x", x);
        x.put("name", "X name");
    }

    public void testJx177() {
        doTest("name", "ROOT name");
        doTest("/x/name", "X name");
        doTest("$__root/x/name", "X name");
    }

    public void testJx177_Union1() {
        doTest("$__root/x/name|name", "X name");

    }

    public void testJx177_Union2() {
        doTest("$__root/x/unexisting|name", "ROOT name");

    }

    private void doTest(String xp, String expected) {
        JXPathContext xpathContext = JXPathContext.newContext(model);
        xpathContext.setVariables(new JXPathVariablesResolver(model));
        Pointer p = xpathContext.getPointer(xp);
        Object result = p.getNode();
        assertNotNull(result);
        assertEquals(expected, result);

    }

    private static class JXPathVariablesResolver implements Variables {

        public static final String ROOT_VAR = "__root";
        private static final long serialVersionUID = -1106360826446119597L;
        private final Object root;

        public JXPathVariablesResolver(Object root) {
            this.root = root;
        }

        public boolean isDeclaredVariable(String varName) {
            if (varName == null) {
                throw new IllegalArgumentException("varName");
            }
            return varName.equals(ROOT_VAR);
        }

        public Object getVariable(String varName) {
            if (varName == null) {
                throw new IllegalArgumentException("varName");
            }
            if (!varName.equals(ROOT_VAR)) {
                throw new IllegalArgumentException("Variable is not declared: " + varName);
            }

            return root;
        }

        public void declareVariable(String varName, Object value) {
            throw new UnsupportedOperationException();
        }

        public void undeclareVariable(String varName) {
            throw new UnsupportedOperationException();
        }

    }
}
