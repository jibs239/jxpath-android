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
 * Predicate implementation that always returns true.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class TruePredicate implements Predicate, Serializable {

    /**
     * Singleton predicate instance
     */
    public static final Predicate INSTANCE = new TruePredicate();
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3374767158756189740L;

    /**
     * Restricted constructor.
     */
    private TruePredicate() {
        super();
    }

    /**
     * Factory returning the singleton instance.
     *
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    public static Predicate getInstance() {
        return INSTANCE;
    }

    /**
     * Evaluates the predicate returning true always.
     *
     * @param object the input object
     * @return true always
     */
    public boolean evaluate(Object object) {
        return true;
    }

}
