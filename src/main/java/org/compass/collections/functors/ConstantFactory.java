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

import java.io.Serializable;

/**
 * Factory implementation that returns the same constant each time.
 * <p>
 * No check is made that the object is immutable. In general, only immutable
 * objects should use the constant factory. Mutable objects should
 * use the prototype factory.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public class ConstantFactory implements Factory, Serializable {

    /**
     * Returns null each time
     */
    public static final Factory NULL_INSTANCE = new ConstantFactory(null);
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3520677225766901240L;
    /**
     * The closures to call in turn
     */
    private final Object iConstant;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param constantToReturn the constant to return each time
     */
    public ConstantFactory(Object constantToReturn) {
        super();
        iConstant = constantToReturn;
    }

    /**
     * Factory method that performs validation.
     *
     * @param constantToReturn the constant object to return each time in the factory
     * @return the <code>constant</code> factory.
     */
    public static Factory getInstance(Object constantToReturn) {
        if (constantToReturn == null) {
            return NULL_INSTANCE;
        }
        return new ConstantFactory(constantToReturn);
    }

    /**
     * Always return constant.
     *
     * @return the stored constant value
     */
    public Object create() {
        return iConstant;
    }

    /**
     * Gets the constant.
     *
     * @return the constant
     * @since Commons Collections 3.1
     */
    public Object getConstant() {
        return iConstant;
    }

}
