/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.compass.collections.iterators;

import org.compass.collections.MapIterator;

/**
 * Provides basic behaviour for decorating a map iterator with extra functionality.
 * <p>
 * All methods are forwarded to the decorated map iterator.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public class AbstractMapIteratorDecorator implements MapIterator {

    /**
     * The iterator being decorated
     */
    protected final MapIterator iterator;

    //-----------------------------------------------------------------------

    /**
     * Constructor that decorates the specified iterator.
     *
     * @param iterator the iterator to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    public AbstractMapIteratorDecorator(MapIterator iterator) {
        super();
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        this.iterator = iterator;
    }

    /**
     * Gets the iterator being decorated.
     *
     * @return the decorated iterator
     */
    protected MapIterator getMapIterator() {
        return iterator;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    public Object getKey() {
        return iterator.getKey();
    }

    public Object getValue() {
        return iterator.getValue();
    }

    public Object setValue(Object obj) {
        return iterator.setValue(obj);
    }

}
