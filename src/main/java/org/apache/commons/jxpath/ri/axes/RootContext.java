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
package org.apache.commons.jxpath.ri.axes;

import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * EvalContext that is used to hold the root node for the path traversal.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class RootContext extends EvalContext {
    public static final Object UNKNOWN_VALUE = new Object();
    private static final int MAX_REGISTER = 4;
    private JXPathContextReferenceImpl jxpathContext;
    private NodePointer pointer;
    private Object[] registers;
    private int availableRegister = 0;

    /**
     * Create a new RootContext.
     *
     * @param jxpathContext context
     * @param pointer       pointer
     */
    public RootContext(JXPathContextReferenceImpl jxpathContext,
                       NodePointer pointer) {
        super(null);
        this.jxpathContext = jxpathContext;
        this.pointer = pointer;
        if (pointer != null) {
            pointer.setNamespaceResolver(jxpathContext.getNamespaceResolver());
        }
    }

    public JXPathContext getJXPathContext() {
        return jxpathContext;
    }

    public RootContext getRootContext() {
        return this;
    }

    /**
     * Get absolute root context
     *
     * @return EvalContext
     */
    public EvalContext getAbsoluteRootContext() {
        return jxpathContext.getAbsoluteRootContext();
    }

    public NodePointer getCurrentNodePointer() {
        return pointer;
    }

    public Object getValue() {
        return pointer;
    }

    public int getCurrentPosition() {
        throw new UnsupportedOperationException();
    }

    public boolean nextNode() {
        throw new UnsupportedOperationException();
    }

    public boolean nextSet() {
        throw new UnsupportedOperationException();
    }

    public boolean setPosition(int position) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a context that points to the specified object.
     *
     * @param constant object
     * @return EvalContext
     */
    public EvalContext getConstantContext(Object constant) {
        if (constant instanceof NodeSet) {
            return new NodeSetContext(
                    new RootContext(jxpathContext, null),
                    (NodeSet) constant);
        }

        NodePointer pointer;
        if (constant instanceof NodePointer) {
            pointer = (NodePointer) constant;
        } else {
            pointer = NodePointer.newNodePointer(
                    new QName(null, ""),
                    constant,
                    null);
        }
        return new InitialContext(new RootContext(jxpathContext, pointer));
    }

    /**
     * Get variable context.
     *
     * @param variableName variable name
     * @return EvalContext
     */
    public EvalContext getVariableContext(QName variableName) {
        return new InitialContext(
                new RootContext(
                        jxpathContext,
                        jxpathContext.getVariablePointer(variableName)));
    }

    /**
     * Get the specified function from the context.
     *
     * @param functionName QName
     * @param parameters   Object[]
     * @return Function
     */
    public Function getFunction(QName functionName, Object[] parameters) {
        return jxpathContext.getFunction(functionName, parameters);
    }

    /**
     * Get a registered value.
     *
     * @param id int
     * @return Object
     */
    public Object getRegisteredValue(int id) {
        if (registers == null || id >= MAX_REGISTER || id == -1) {
            return UNKNOWN_VALUE;
        }
        return registers[id];
    }

    /**
     * Set the next registered value.
     *
     * @param value Object
     * @return the id that can reclaim value.
     */
    public int setRegisteredValue(Object value) {
        if (registers == null) {
            registers = new Object[MAX_REGISTER];
            for (int i = 0; i < MAX_REGISTER; i++) {
                registers[i] = UNKNOWN_VALUE;
            }
        }
        if (availableRegister >= MAX_REGISTER) {
            return -1;
        }
        registers[availableRegister] = value;
        availableRegister++;
        return availableRegister - 1;
    }

    public String toString() {
        return super.toString() + ":" + pointer.asPath();
    }
}
