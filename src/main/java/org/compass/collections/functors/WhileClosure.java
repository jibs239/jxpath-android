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

import org.compass.collections.Closure;
import org.compass.collections.Predicate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Closure implementation that executes a closure repeatedly until a condition is met,
 * like a do-while or while loop.
 * <p>
 * <b>WARNING:</b> from v3.2.2 onwards this class will throw an
 * {@link UnsupportedOperationException} when trying to serialize or
 * de-serialize an instance to prevent potential remote code execution exploits.
 * <p>
 * In order to re-enable serialization support for {@code WhileClosure}
 * the following system property can be used (via -Dproperty=true):
 * <pre>
 * org.apache.commons.collections.enableUnsafeSerialization
 * </pre>
 *
 * @author Stephen Colebourne
 * @version $Revision: 1713845 $ $Date: 2015-11-11 15:02:16 +0100 (Wed, 11 Nov 2015) $
 * @since Commons Collections 3.0
 */
public class WhileClosure implements Closure, Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3110538116913760108L;

    /**
     * The test condition
     */
    private final Predicate iPredicate;
    /**
     * The closure to call
     */
    private final Closure iClosure;
    /**
     * The flag, true is a do loop, false is a while
     */
    private final boolean iDoLoop;

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate the predicate used to evaluate when the loop terminates, not null
     * @param closure   the closure the execute, not null
     * @param doLoop    true to act as a do-while loop, always executing the closure once
     */
    public WhileClosure(Predicate predicate, Closure closure, boolean doLoop) {
        super();
        iPredicate = predicate;
        iClosure = closure;
        iDoLoop = doLoop;
    }

    /**
     * Factory method that performs validation.
     *
     * @param predicate the predicate used to evaluate when the loop terminates, not null
     * @param closure   the closure the execute, not null
     * @param doLoop    true to act as a do-while loop, always executing the closure once
     * @return the <code>while</code> closure
     * @throws IllegalArgumentException if the predicate or closure is null
     */
    public static Closure getInstance(Predicate predicate, Closure closure, boolean doLoop) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new WhileClosure(predicate, closure, doLoop);
    }

    /**
     * Executes the closure until the predicate is false.
     *
     * @param input the input object
     */
    public void execute(Object input) {
        if (iDoLoop) {
            iClosure.execute(input);
        }
        while (iPredicate.evaluate(input)) {
            iClosure.execute(input);
        }
    }

    /**
     * Gets the predicate in use.
     *
     * @return the predicate
     * @since Commons Collections 3.1
     */
    public Predicate getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the closure.
     *
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure getClosure() {
        return iClosure;
    }

    /**
     * Is the loop a do-while loop.
     *
     * @return true is do-while, false if while
     * @since Commons Collections 3.1
     */
    public boolean isDoLoop() {
        return iDoLoop;
    }

    /**
     * Overrides the default writeObject implementation to prevent
     * serialization (see COLLECTIONS-580).
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(WhileClosure.class);
        os.defaultWriteObject();
    }

    /**
     * Overrides the default readObject implementation to prevent
     * de-serialization (see COLLECTIONS-580).
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(WhileClosure.class);
        is.defaultReadObject();
    }

}
