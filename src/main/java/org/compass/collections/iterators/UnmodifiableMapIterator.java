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
import org.compass.collections.Unmodifiable;

/**
 * Decorates a map iterator such that it cannot be modified.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableMapIterator implements MapIterator, Unmodifiable {

    /**
     * The iterator being decorated
     */
    private MapIterator iterator;

    //-----------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param iterator the iterator to decorate
     */
    private UnmodifiableMapIterator(MapIterator iterator) {
        super();
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------

    /**
     * Decorates the specified iterator such that it cannot be modified.
     *
     * @param iterator the iterator to decorate
     * @throws IllegalArgumentException if the iterator is null
     */
    public static MapIterator decorate(MapIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableMapIterator(iterator);
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public Object getKey() {
        return iterator.getKey();
    }

    public Object getValue() {
        return iterator.getValue();
    }

    public Object setValue(Object value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

}
