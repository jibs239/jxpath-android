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
package org.compass.collections.bidimap;

import org.compass.collections.*;
import org.compass.collections.collection.UnmodifiableCollection;
import org.compass.collections.iterators.UnmodifiableOrderedMapIterator;
import org.compass.collections.map.UnmodifiableEntrySet;
import org.compass.collections.set.UnmodifiableSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>OrderedBidiMap</code> to ensure it can't be altered.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableOrderedBidiMap
        extends AbstractOrderedBidiMapDecorator implements Unmodifiable {

    /**
     * The inverse unmodifiable map
     */
    private UnmodifiableOrderedBidiMap inverse;

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableOrderedBidiMap(OrderedBidiMap map) {
        super(map);
    }

    //-----------------------------------------------------------------------

    /**
     * Factory method to create an unmodifiable map.
     * <p>
     * If the map passed in is already unmodifiable, it is returned.
     *
     * @param map the map to decorate, must not be null
     * @return an unmodifiable OrderedBidiMap
     * @throws IllegalArgumentException if map is null
     */
    public static OrderedBidiMap decorate(OrderedBidiMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableOrderedBidiMap(map);
    }

    //-----------------------------------------------------------------------
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map mapToCopy) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        Set set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }

    public Set keySet() {
        Set set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }

    //-----------------------------------------------------------------------
    public Object removeValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public MapIterator mapIterator() {
        return orderedMapIterator();
    }

    public BidiMap inverseBidiMap() {
        return inverseOrderedBidiMap();
    }

    //-----------------------------------------------------------------------
    public OrderedMapIterator orderedMapIterator() {
        OrderedMapIterator it = getOrderedBidiMap().orderedMapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
    }

    public OrderedBidiMap inverseOrderedBidiMap() {
        if (inverse == null) {
            inverse = new UnmodifiableOrderedBidiMap(getOrderedBidiMap().inverseOrderedBidiMap());
            inverse.inverse = this;
        }
        return inverse;
    }

}
