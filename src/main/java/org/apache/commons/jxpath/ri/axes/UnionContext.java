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

import org.apache.commons.jxpath.BasicNodeSet;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.model.NodePointer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * EvalContext that represents a union between other contexts - result
 * of a union operation like (a | b)
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class UnionContext extends NodeSetContext {
    private EvalContext[] contexts;
    private boolean prepared;

    /**
     * Create a new UnionContext.
     *
     * @param parentContext parent context
     * @param contexts      child contexts
     */
    public UnionContext(EvalContext parentContext, EvalContext[] contexts) {
        super(parentContext, new BasicNodeSet());
        this.contexts = contexts;
    }

    public int getDocumentOrder() {
        return contexts.length > 1 ? 1 : super.getDocumentOrder();
    }

    public boolean setPosition(int position) {
        if (!prepared) {
            prepared = true;
            BasicNodeSet nodeSet = (BasicNodeSet) getNodeSet();
            ArrayList pointers = new ArrayList();
            for (int i = 0; i < contexts.length; i++) {
                EvalContext ctx = contexts[i];
                while (ctx.nextSet()) {
                    while (ctx.nextNode()) {
                        NodePointer ptr = ctx.getCurrentNodePointer();
                        if (!pointers.contains(ptr)) {
                            pointers.add(ptr);
                        }
                    }
                }
            }
            sortPointers(pointers);

            for (Iterator it = pointers.iterator(); it.hasNext(); ) {
                nodeSet.add((Pointer) it.next());
            }
        }
        return super.setPosition(position);
    }
}
