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

import org.compass.collections.Factory;
import org.compass.collections.FunctorException;

import java.io.Serializable;

/**
 * Factory implementation that always throws an exception.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class ExceptionFactory implements Factory, Serializable {

    /**
     * Singleton predicate instance
     */
    public static final Factory INSTANCE = new ExceptionFactory();
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7179106032121985545L;

    /**
     * Restricted constructor.
     */
    private ExceptionFactory() {
        super();
    }

    /**
     * Factory returning the singleton instance.
     *
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    public static Factory getInstance() {
        return INSTANCE;
    }

    /**
     * Always throws an exception.
     *
     * @return never
     * @throws FunctorException always
     */
    public Object create() {
        throw new FunctorException("ExceptionFactory invoked");
    }

}
