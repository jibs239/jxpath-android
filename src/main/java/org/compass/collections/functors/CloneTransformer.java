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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Transformer implementation that returns a clone of the input object.
 * <p>
 * Clone is performed using <code>PrototypeFactory.getInstance(input).create()</code>.
 * <p>
 * <b>WARNING:</b> from v3.2.2 onwards this class will throw an
 * {@link UnsupportedOperationException} when trying to serialize or
 * de-serialize an instance to prevent potential remote code execution exploits.
 * <p>
 * In order to re-enable serialization support for {@code CloneTransformer}
 * the following system property can be used (via -Dproperty=true):
 * <pre>
 * org.apache.commons.collections.enableUnsafeSerialization
 * </pre>
 *
 * @author Stephen Colebourne
 * @version $Revision: 1713845 $ $Date: 2015-11-11 15:02:16 +0100 (Wed, 11 Nov 2015) $
 * @since Commons Collections 3.0
 */
public class CloneTransformer implements Transformer, Serializable {

    /**
     * Singleton predicate instance
     */
    public static final Transformer INSTANCE = new CloneTransformer();
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -8188742709499652567L;

    /**
     * Constructor
     */
    private CloneTransformer() {
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
     * Transforms the input to result by cloning it.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public Object transform(Object input) {
        if (input == null) {
            return null;
        }
        return PrototypeFactory.getInstance(input).create();
    }

    /**
     * Overrides the default writeObject implementation to prevent
     * serialization (see COLLECTIONS-580).
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(CloneTransformer.class);
        os.defaultWriteObject();
    }

    /**
     * Overrides the default readObject implementation to prevent
     * de-serialization (see COLLECTIONS-580).
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(CloneTransformer.class);
        is.defaultReadObject();
    }
}
