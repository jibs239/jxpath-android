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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Factory implementation that creates a new instance each time based on a prototype.
 *
 * @author Stephen Colebourne
 * @version $Revision: 1713849 $ $Date: 2015-11-11 15:21:37 +0100 (Wed, 11 Nov 2015) $
 * @since Commons Collections 3.0
 */
public class PrototypeFactory {

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     */
    private PrototypeFactory() {
        super();
    }

    /**
     * Factory method that performs validation.
     * <p>
     * Creates a Factory that will return a clone of the same prototype object
     * each time the factory is used. The prototype will be cloned using one of these
     * techniques (in order):
     * <ul>
     * <li>public clone method
     * <li>public copy constructor
     * <li>serialization clone
     * <ul>
     * <p>
     * <b>WARNING:</b> from v3.2.2 onwards this method will return a {@code Factory}
     * that will throw an {@link UnsupportedOperationException} when trying to serialize
     * or de-serialize it to prevent potential remote code execution exploits.
     * <p>
     * In order to re-enable serialization support the following system property
     * can be used (via -Dproperty=true):
     * <pre>
     * org.apache.commons.collections.enableUnsafeSerialization
     * </pre>
     *
     * @param prototype the object to clone each time in the factory
     * @return the <code>prototype</code> factory
     * @throws IllegalArgumentException if the prototype is null
     * @throws IllegalArgumentException if the prototype cannot be cloned
     */
    public static Factory getInstance(Object prototype) {
        if (prototype == null) {
            return ConstantFactory.NULL_INSTANCE;
        }
        try {
            Method method = prototype.getClass().getMethod("clone", (Class[]) null);
            return new PrototypeCloneFactory(prototype, method);

        } catch (NoSuchMethodException ex) {
            try {
                prototype.getClass().getConstructor(new Class[]{prototype.getClass()});
                return new InstantiateFactory(
                        prototype.getClass(),
                        new Class[]{prototype.getClass()},
                        new Object[]{prototype});

            } catch (NoSuchMethodException ex2) {
                if (prototype instanceof Serializable) {
                    return new PrototypeSerializationFactory((Serializable) prototype);
                }
            }
        }
        throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
    }

    // PrototypeCloneFactory
    //-----------------------------------------------------------------------

    /**
     * PrototypeCloneFactory creates objects by copying a prototype using the clone method.
     */
    static class PrototypeCloneFactory implements Factory, Serializable {

        /**
         * The serial version
         */
        private static final long serialVersionUID = 5604271422565175555L;

        /**
         * The object to clone each time
         */
        private final Object iPrototype;
        /**
         * The method used to clone
         */
        private transient Method iCloneMethod;

        /**
         * Constructor to store prototype.
         */
        private PrototypeCloneFactory(Object prototype, Method method) {
            super();
            iPrototype = prototype;
            iCloneMethod = method;
        }

        /**
         * Find the Clone method for the class specified.
         */
        private void findCloneMethod() {
            try {
                iCloneMethod = iPrototype.getClass().getMethod("clone", (Class[]) null);

            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }

        /**
         * Creates an object by calling the clone method.
         *
         * @return the new object
         */
        public Object create() {
            // needed for post-serialization
            if (iCloneMethod == null) {
                findCloneMethod();
            }

            try {
                return iCloneMethod.invoke(iPrototype, (Object[]) null);

            } catch (IllegalAccessException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
            } catch (InvocationTargetException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
            }
        }

        /**
         * Overrides the default writeObject implementation to prevent
         * serialization (see COLLECTIONS-580).
         */
        private void writeObject(ObjectOutputStream os) throws IOException {
            FunctorUtils.checkUnsafeSerialization(PrototypeCloneFactory.class);
            os.defaultWriteObject();
        }

        /**
         * Overrides the default readObject implementation to prevent
         * de-serialization (see COLLECTIONS-580).
         */
        private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
            FunctorUtils.checkUnsafeSerialization(PrototypeCloneFactory.class);
            is.defaultReadObject();
        }
    }

    // PrototypeSerializationFactory
    //-----------------------------------------------------------------------

    /**
     * PrototypeSerializationFactory creates objects by cloning a prototype using serialization.
     */
    static class PrototypeSerializationFactory implements Factory, Serializable {

        /**
         * The serial version
         */
        private static final long serialVersionUID = -8704966966139178833L;

        /**
         * The object to clone via serialization each time
         */
        private final Serializable iPrototype;

        /**
         * Constructor to store prototype
         */
        private PrototypeSerializationFactory(Serializable prototype) {
            super();
            iPrototype = prototype;
        }

        /**
         * Creates an object using serialization.
         *
         * @return the new object
         */
        public Object create() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(iPrototype);

                bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream in = new ObjectInputStream(bais);
                return in.readObject();

            } catch (ClassNotFoundException ex) {
                throw new FunctorException(ex);
            } catch (IOException ex) {
                throw new FunctorException(ex);
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                } catch (IOException ex) {
                    // ignore
                }
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException ex) {
                    // ignore
                }
            }
        }

        /**
         * Overrides the default writeObject implementation to prevent
         * serialization (see COLLECTIONS-580).
         */
        private void writeObject(ObjectOutputStream os) throws IOException {
            FunctorUtils.checkUnsafeSerialization(PrototypeSerializationFactory.class);
            os.defaultWriteObject();
        }

        /**
         * Overrides the default readObject implementation to prevent
         * de-serialization (see COLLECTIONS-580).
         */
        private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
            FunctorUtils.checkUnsafeSerialization(PrototypeSerializationFactory.class);
            is.defaultReadObject();
        }
    }

}
