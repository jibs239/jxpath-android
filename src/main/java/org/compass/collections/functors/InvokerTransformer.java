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
import org.compass.collections.Transformer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Transformer implementation that creates a new object instance by reflection.
 * <p>
 * <b>WARNING:</b> from v3.2.2 onwards this class will throw an
 * {@link UnsupportedOperationException} when trying to serialize or
 * de-serialize an instance to prevent potential remote code execution exploits.
 * <p>
 * In order to re-enable serialization support for {@code InvokerTransformer}
 * the following system property can be used (via -Dproperty=true):
 * <pre>
 * org.apache.commons.collections.enableUnsafeSerialization
 * </pre>
 *
 * @author Stephen Colebourne
 * @version $Revision: 1713845 $ $Date: 2015-11-11 15:02:16 +0100 (Wed, 11 Nov 2015) $
 * @since Commons Collections 3.0
 */
public class InvokerTransformer implements Transformer, Serializable {

    /**
     * The serial version
     */
    private static final long serialVersionUID = -8653385846894047688L;

    /**
     * The method name to call
     */
    private final String iMethodName;
    /**
     * The array of reflection parameter types
     */
    private final Class[] iParamTypes;
    /**
     * The array of reflection arguments
     */
    private final Object[] iArgs;

    /**
     * Constructor for no arg instance.
     *
     * @param methodName the method to call
     */
    private InvokerTransformer(String methodName) {
        super();
        iMethodName = methodName;
        iParamTypes = null;
        iArgs = null;
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param methodName the method to call
     * @param paramTypes the constructor parameter types, not cloned
     * @param args       the constructor arguments, not cloned
     */
    public InvokerTransformer(String methodName, Class[] paramTypes, Object[] args) {
        super();
        iMethodName = methodName;
        iParamTypes = paramTypes;
        iArgs = args;
    }

    /**
     * Gets an instance of this transformer calling a specific method with no arguments.
     *
     * @param methodName the method name to call
     * @return an invoker transformer
     * @since Commons Collections 3.1
     */
    public static Transformer getInstance(String methodName) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method to invoke must not be null");
        }
        return new InvokerTransformer(methodName);
    }

    /**
     * Gets an instance of this transformer calling a specific method with specific values.
     *
     * @param methodName the method name to call
     * @param paramTypes the parameter types of the method
     * @param args       the arguments to pass to the method
     * @return an invoker transformer
     */
    public static Transformer getInstance(String methodName, Class[] paramTypes, Object[] args) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method to invoke must not be null");
        }
        if (((paramTypes == null) && (args != null))
                || ((paramTypes != null) && (args == null))
                || ((paramTypes != null) && (args != null) && (paramTypes.length != args.length))) {
            throw new IllegalArgumentException("The parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InvokerTransformer(methodName);
        } else {
            paramTypes = (Class[]) paramTypes.clone();
            args = (Object[]) args.clone();
            return new InvokerTransformer(methodName, paramTypes, args);
        }
    }

    /**
     * Transforms the input to result by invoking a method on the input.
     *
     * @param input the input object to transform
     * @return the transformed result, null if null input
     */
    public Object transform(Object input) {
        if (input == null) {
            return null;
        }
        try {
            Class cls = input.getClass();
            Method method = cls.getMethod(iMethodName, iParamTypes);
            return method.invoke(input, iArgs);

        } catch (NoSuchMethodException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' does not exist");
        } catch (IllegalAccessException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' cannot be accessed");
        } catch (InvocationTargetException ex) {
            throw new FunctorException("InvokerTransformer: The method '" + iMethodName + "' on '" + input.getClass() + "' threw an exception", ex);
        }
    }

    /**
     * Overrides the default writeObject implementation to prevent
     * serialization (see COLLECTIONS-580).
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(InvokerTransformer.class);
        os.defaultWriteObject();
    }

    /**
     * Overrides the default readObject implementation to prevent
     * de-serialization (see COLLECTIONS-580).
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(InvokerTransformer.class);
        is.defaultReadObject();
    }
}
