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
package org.compass.collections.functors;

import org.compass.collections.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that returns true if both the predicates return true.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class AndPredicate implements Predicate, PredicateDecorator, Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4189014213763186912L;

    /**
     * The array of predicates to call
     */
    private final Predicate iPredicate1;
    /**
     * The array of predicates to call
     */
    private final Predicate iPredicate2;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate1 the first predicate to check, not null
     * @param predicate2 the second predicate to check, not null
     */
    public AndPredicate(Predicate predicate1, Predicate predicate2) {
        super();
        iPredicate1 = predicate1;
        iPredicate2 = predicate2;
    }

    /**
     * Factory to create the predicate.
     *
     * @param predicate1 the first predicate to check, not null
     * @param predicate2 the second predicate to check, not null
     * @return the <code>and</code> predicate
     * @throws IllegalArgumentException if either predicate is null
     */
    public static Predicate getInstance(Predicate predicate1, Predicate predicate2) {
        if (predicate1 == null || predicate2 == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new AndPredicate(predicate1, predicate2);
    }

    /**
     * Evaluates the predicate returning true if both predicates return true.
     *
     * @param object the input object
     * @return true if both decorated predicates return true
     */
    public boolean evaluate(Object object) {
        return (iPredicate1.evaluate(object) && iPredicate2.evaluate(object));
    }

    /**
     * Gets the two predicates being decorated as an array.
     *
     * @return the predicates
     * @since Commons Collections 3.1
     */
    public Predicate[] getPredicates() {
        return new Predicate[]{iPredicate1, iPredicate2};
    }

}
