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
package org.compass.collections;

/**
 * Defines a map that allows bidirectional lookup between key and values
 * and retains and provides access to an ordering.
 * <p>
 * Implementations should allow a value to be looked up from a key and
 * a key to be looked up from a value with equal performance.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public interface OrderedBidiMap extends BidiMap, OrderedMap {

    /**
     * Gets a view of this map where the keys and values are reversed.
     * <p>
     * Changes to one map will be visible in the other and vice versa.
     * This enables both directions of the map to be accessed equally.
     * <p>
     * Implementations should seek to avoid creating a new object every time this
     * method is called. See <code>AbstractMap.values()</code> etc. Calling this
     * method on the inverse map should return the original.
     * <p>
     * Implementations must return an <code>OrderedBidiMap</code> instance,
     * usually by forwarding to <code>inverseOrderedBidiMap()</code>.
     *
     * @return an inverted bidirectional map
     */
    public BidiMap inverseBidiMap();

    /**
     * Gets a view of this map where the keys and values are reversed.
     * <p>
     * Changes to one map will be visible in the other and vice versa.
     * This enables both directions of the map to be accessed equally.
     * <p>
     * Implementations should seek to avoid creating a new object every time this
     * method is called. See <code>AbstractMap.values()</code> etc. Calling this
     * method on the inverse map should return the original.
     *
     * @return an inverted bidirectional map
     */
    public OrderedBidiMap inverseOrderedBidiMap();

}
