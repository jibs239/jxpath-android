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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory implementation that creates a new object instance by reflection.
 * <p>
 * <b>WARNING:</b> from v3.2.2 onwards this class will throw an
 * {@link UnsupportedOperationException} when trying to serialize or
 * de-serialize an instance to prevent potential remote code execution exploits.
 * <p>
 * In order to re-enable serialization support for {@code InstantiateTransformer}
 * the following system property can be used (via -Dproperty=true):
 * <pre>
 * org.apache.commons.collections.enableUnsafeSerialization
 * </pre>
 *
 * @author Stephen Colebourne
 * @version $Revision: 1713845 $ $Date: 2015-11-11 15:02:16 +0100 (Wed, 11 Nov 2015) $
 * @since Commons Collections 3.0
 */
public class InstantiateFactory implements Factory, Serializable {

    /**
     * The serial version
     */
    private static final long serialVersionUID = -7732226881069447957L;

    /**
     * The class to create
     */
    private final Class iClassToInstantiate;
    /**
     * The constructor parameter types
     */
    private final Class[] iParamTypes;
    /**
     * The constructor arguments
     */
    private final Object[] iArgs;
    /**
     * The constructor
     */
    private transient Constructor iConstructor = null;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param classToInstantiate the class to instantiate
     */
    public InstantiateFactory(Class classToInstantiate) {
        super();
        iClassToInstantiate = classToInstantiate;
        iParamTypes = null;
        iArgs = null;
        findConstructor();
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param classToInstantiate the class to instantiate
     * @param paramTypes         the constructor parameter types, not cloned
     * @param args               the constructor arguments, not cloned
     */
    public InstantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        super();
        iClassToInstantiate = classToInstantiate;
        iParamTypes = paramTypes;
        iArgs = args;
        findConstructor();
    }

    /**
     * Factory method that performs validation.
     *
     * @param classToInstantiate the class to instantiate, not null
     * @param paramTypes         the constructor parameter types
     * @param args               the constructor arguments
     * @return a new instantiate factory
     */
    public static Factory getInstance(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        if (classToInstantiate == null) {
            throw new IllegalArgumentException("Class to instantiate must not be null");
        }
        if (((paramTypes == null) && (args != null))
                || ((paramTypes != null) && (args == null))
                || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }

        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory(classToInstantiate);
        } else {
            paramTypes = (Class[]) paramTypes.clone();
            args = (Object[]) args.clone();
            return new InstantiateFactory(classToInstantiate, paramTypes, args);
        }
    }

    /**
     * Find the Constructor for the class specified.
     */
    private void findConstructor() {
        try {
            iConstructor = iClassToInstantiate.getConstructor(iParamTypes);

        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }

    /**
     * Creates an object using the stored constructor.
     *
     * @return the new object
     */
    public Object create() {
        // needed for post-serialization
        if (iConstructor == null) {
            findConstructor();
        }

        try {
            return iConstructor.newInstance(iArgs);

        } catch (InstantiationException ex) {
            throw new FunctorException("InstantiateFactory: InstantiationException", ex);
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
        }
    }

    /**
     * Overrides the default writeObject implementation to prevent
     * serialization (see COLLECTIONS-580).
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(InstantiateFactory.class);
        os.defaultWriteObject();
    }

    /**
     * Overrides the default readObject implementation to prevent
     * de-serialization (see COLLECTIONS-580).
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(InstantiateFactory.class);
        is.defaultReadObject();
    }

}
