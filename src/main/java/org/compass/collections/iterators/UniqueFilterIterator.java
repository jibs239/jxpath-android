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
package org.compass.collections.iterators;

import org.compass.collections.functors.UniquePredicate;

import java.util.Iterator;

/**
 * A FilterIterator which only returns "unique" Objects.  Internally,
 * the Iterator maintains a Set of objects it has already encountered,
 * and duplicate Objects are skipped.
 *
 * @author Morgan Delagrange
 * @version $Revision: 646777 $ $Date: 2008-04-10 14:33:15 +0200 (Thu, 10 Apr 2008) $
 * @since Commons Collections 2.1
 */
public class UniqueFilterIterator extends FilterIterator {

    //-------------------------------------------------------------------------

    /**
     * Constructs a new <code>UniqueFilterIterator</code>.
     *
     * @param iterator the iterator to use
     */
    public UniqueFilterIterator(Iterator iterator) {
        super(iterator, UniquePredicate.getInstance());
    }

}
