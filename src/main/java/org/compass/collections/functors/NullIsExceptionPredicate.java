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

import org.compass.collections.FunctorException;
import org.compass.collections.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that throws an exception if the input is null.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class NullIsExceptionPredicate implements Predicate, PredicateDecorator, Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3243449850504576071L;

    /**
     * The predicate to decorate
     */
    private final Predicate iPredicate;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate the predicate to call after the null check
     */
    public NullIsExceptionPredicate(Predicate predicate) {
        super();
        iPredicate = predicate;
    }

    /**
     * Factory to create the null exception predicate.
     *
     * @param predicate the predicate to decorate, not null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static Predicate getInstance(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsExceptionPredicate(predicate);
    }

    /**
     * Evaluates the predicate returning the result of the decorated predicate
     * once a null check is performed.
     *
     * @param object the input object
     * @return true if decorated predicate returns true
     * @throws FunctorException if input is null
     */
    public boolean evaluate(Object object) {
        if (object == null) {
            throw new FunctorException("Input Object must not be null");
        }
        return iPredicate.evaluate(object);
    }

    /**
     * Gets the predicate being decorated.
     *
     * @return the predicate as the only element in an array
     * @since Commons Collections 3.1
     */
    public Predicate[] getPredicates() {
        return new Predicate[]{iPredicate};
    }

}
