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

import org.compass.collections.Transformer;

import java.io.Serializable;

/**
 * Transformer implementation that returns the result of calling
 * <code>String.valueOf</code> on the input object.
 *
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 3.0
 */
public final class StringValueTransformer implements Transformer, Serializable {

    /**
     * Singleton predicate instance
     */
    public static final Transformer INSTANCE = new StringValueTransformer();
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7511110693171758606L;

    /**
     * Restricted constructor.
     */
    private StringValueTransformer() {
        super();
    }

    /**
     * Factory returning the singleton instance.
     *
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    public static Transformer getInstance() {
        return INSTANCE;
    }

    /**
     * Transforms the input to result by calling <code>String.valueOf</code>.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public Object transform(Object input) {
        return String.valueOf(input);
    }

}
