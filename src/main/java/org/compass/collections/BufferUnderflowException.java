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
package org.compass.collections;

import java.util.NoSuchElementException;

/**
 * The BufferUnderflowException is used when the buffer is already empty.
 * <p>
 * NOTE: From version 3.0, this exception extends NoSuchElementException.
 *
 * @author Avalon
 * @author Berin Loritsch
 * @author Jeff Turner
 * @author Paul Jack
 * @author Stephen Colebourne
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 2.1
 */
public class BufferUnderflowException extends NoSuchElementException {

    /**
     * The root cause throwable
     */
    private final Throwable throwable;

    /**
     * Constructs a new <code>BufferUnderflowException</code>.
     */
    public BufferUnderflowException() {
        super();
        throwable = null;
    }

    /**
     * Construct a new <code>BufferUnderflowException</code>.
     *
     * @param message the detail message for this exception
     */
    public BufferUnderflowException(String message) {
        this(message, null);
    }

    /**
     * Construct a new <code>BufferUnderflowException</code>.
     *
     * @param message   the detail message for this exception
     * @param exception the root cause of the exception
     */
    public BufferUnderflowException(String message, Throwable exception) {
        super(message);
        throwable = exception;
    }

    /**
     * Gets the root cause of the exception.
     *
     * @return the root cause
     */
    public final Throwable getCause() {
        return throwable;
    }

}
