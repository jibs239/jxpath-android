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
package org.apache.commons.jxpath.ri.model.container;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.ValueUtils;

import java.util.Locale;

/**
 * Transparent pointer to a Container. The {@link #getValue()} method
 * returns the contents of the container, rather than the container
 * itself.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class ContainerPointer extends NodePointer {
    private static final long serialVersionUID = 6140752946621686118L;
    private Container container;
    private NodePointer valuePointer;

    /**
     * Create a new ContainerPointer.
     *
     * @param container Container object
     * @param locale    Locale
     */
    public ContainerPointer(Container container, Locale locale) {
        super(null, locale);
        this.container = container;
    }

    /**
     * Create a new ContainerPointer.
     *
     * @param parent    parent pointer
     * @param container Container object
     */
    public ContainerPointer(NodePointer parent, Container container) {
        super(parent);
        this.container = container;
    }

    /**
     * This type of node is auxiliary.
     *
     * @return <code>true</code>.
     */
    public boolean isContainer() {
        return true;
    }

    public QName getName() {
        return null;
    }

    public Object getBaseValue() {
        return container;
    }

    public boolean isCollection() {
        Object value = getBaseValue();
        return value != null && ValueUtils.isCollection(value);
    }

    public int getLength() {
        Object value = getBaseValue();
        return value == null ? 1 : ValueUtils.getLength(value);
    }

    public boolean isLeaf() {
        return getValuePointer().isLeaf();
    }

    public Object getImmediateNode() {
        Object value = getBaseValue();
        if (index != WHOLE_COLLECTION) {
            return index >= 0 && index < getLength() ? ValueUtils.getValue(value, index) : null;
        }
        return ValueUtils.getValue(value);
    }

    public void setValue(Object value) {
        // TODO: what if this is a collection?
        container.setValue(value);
    }

    public NodePointer getImmediateValuePointer() {
        if (valuePointer == null) {
            Object value = getImmediateNode();
            valuePointer = NodePointer.newChildNodePointer(this, getName(), value);
        }
        return valuePointer;
    }

    public int hashCode() {
        return System.identityHashCode(container) + index;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof ContainerPointer)) {
            return false;
        }

        ContainerPointer other = (ContainerPointer) object;
        return container == other.container && index == other.index;
    }

    public NodeIterator childIterator(
            NodeTest test,
            boolean reverse,
            NodePointer startWith) {
        return getValuePointer().childIterator(test, reverse, startWith);
    }

    public NodeIterator attributeIterator(QName name) {
        return getValuePointer().attributeIterator(name);
    }

    public NodeIterator namespaceIterator() {
        return getValuePointer().namespaceIterator();
    }

    public NodePointer namespacePointer(String namespace) {
        return getValuePointer().namespacePointer(namespace);
    }

    public boolean testNode(NodeTest nodeTest) {
        return getValuePointer().testNode(nodeTest);
    }

    public int compareChildNodePointers(
            NodePointer pointer1,
            NodePointer pointer2) {
        return pointer1.getIndex() - pointer2.getIndex();
    }

    public String getNamespaceURI(String prefix) {
        return getValuePointer().getNamespaceURI(prefix);
    }

    public String asPath() {
        return parent == null ? "/" : parent.asPath();
    }
}
