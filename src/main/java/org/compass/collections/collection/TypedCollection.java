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
package org.compass.collections.collection;

import org.compass.collections.functors.InstanceofPredicate;

import java.util.Collection;

/**
 * Decorates a <code>Collection</code> to validate that elements added are of a specific type.
 * <p>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @author Stephen Colebourne
 * @author Matthew Hawthorne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public class TypedCollection {

    /**
     * Restrictive constructor.
     */
    protected TypedCollection() {
        super();
    }

    /**
     * Factory method to create a typed collection.
     * <p>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     *
     * @param coll the collection to decorate, must not be null
     * @param type the type to allow into the collection, must not be null
     * @return a new typed collection
     * @throws IllegalArgumentException if collection or type is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    public static Collection decorate(Collection coll, Class type) {
        return new PredicatedCollection(coll, InstanceofPredicate.getInstance(type));
    }

}
