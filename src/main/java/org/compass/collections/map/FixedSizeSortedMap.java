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
package org.compass.collections.map;

import org.compass.collections.BoundedMap;
import org.compass.collections.collection.UnmodifiableCollection;
import org.compass.collections.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Decorates another <code>SortedMap</code> to fix the size blocking add/remove.
 * <p>
 * Any action that would change the size of the map is disallowed.
 * The put method is allowed to change the value associated with an existing
 * key however.
 * <p>
 * If trying to remove or clear the map, an UnsupportedOperationException is
 * thrown. If trying to put a new mapping into the map, an
 * IllegalArgumentException is thrown. This is because the put method can
 * succeed if the mapping's key already exists in the map, so the put method
 * is not always unsupported.
 * <p>
 * <strong>Note that FixedSizeSortedMap is not synchronized and is not thread-safe.</strong>
 * If you wish to use this map from multiple threads concurrently, you must use
 * appropriate synchronization. The simplest approach is to wrap this map
 * using {@link java.util.Collections#synchronizedSortedMap}. This class may throw
 * exceptions when accessed by concurrent threads without synchronization.
 * <p>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Paul Jack
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public class FixedSizeSortedMap
        extends AbstractSortedMapDecorator
        implements SortedMap, BoundedMap, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 3126019624511683653L;

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected FixedSizeSortedMap(SortedMap map) {
        super(map);
    }

    //-----------------------------------------------------------------------

    /**
     * Factory method to create a fixed size sorted map.
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static SortedMap decorate(SortedMap map) {
        return new FixedSizeSortedMap(map);
    }

    /**
     * Gets the map being decorated.
     *
     * @return the decorated map
     */
    protected SortedMap getSortedMap() {
        return (SortedMap) map;
    }

    //-----------------------------------------------------------------------

    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map) in.readObject();
    }

    //-----------------------------------------------------------------------
    public Object put(Object key, Object value) {
        if (map.containsKey(key) == false) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return map.put(key, value);
    }

    public void putAll(Map mapToCopy) {
        for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext(); ) {
            if (mapToCopy.containsKey(it.next()) == false) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
        }
        map.putAll(mapToCopy);
    }

    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Set entrySet() {
        Set set = map.entrySet();
        return UnmodifiableSet.decorate(set);
    }

    public Set keySet() {
        Set set = map.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection values() {
        Collection coll = map.values();
        return UnmodifiableCollection.decorate(coll);
    }

    //-----------------------------------------------------------------------
    public SortedMap subMap(Object fromKey, Object toKey) {
        SortedMap map = getSortedMap().subMap(fromKey, toKey);
        return new FixedSizeSortedMap(map);
    }

    public SortedMap headMap(Object toKey) {
        SortedMap map = getSortedMap().headMap(toKey);
        return new FixedSizeSortedMap(map);
    }

    public SortedMap tailMap(Object fromKey) {
        SortedMap map = getSortedMap().tailMap(fromKey);
        return new FixedSizeSortedMap(map);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}
