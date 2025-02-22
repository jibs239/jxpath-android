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
 * Predicate implementation that returns true if the input is the same object
 * as the one stored in this predicate by equals.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class EqualPredicate implements Predicate, Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5633766978029907089L;

    /**
     * The value to compare to
     */
    private final Object iValue;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param object the object to compare to
     */
    public EqualPredicate(Object object) {
        super();
        iValue = object;
    }

    /**
     * Factory to create the identity predicate.
     *
     * @param object the object to compare to
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static Predicate getInstance(Object object) {
        if (object == null) {
            return NullPredicate.INSTANCE;
        }
        return new EqualPredicate(object);
    }

    /**
     * Evaluates the predicate returning true if the input equals the stored value.
     *
     * @param object the input object
     * @return true if input object equals stored value
     */
    public boolean evaluate(Object object) {
        return (iValue.equals(object));
    }

    /**
     * Gets the value.
     *
     * @return the value
     * @since Commons Collections 3.1
     */
    public Object getValue() {
        return iValue;
    }

}
