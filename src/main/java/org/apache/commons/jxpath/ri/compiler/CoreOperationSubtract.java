/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.InfoSetUtil;

/**
 * Implementation of {@link Expression} for the operation "-".
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class CoreOperationSubtract extends CoreOperation {

    /**
     * Create a new CoreOperationSubtract.
     *
     * @param arg1 minuend
     * @param arg2 subtrahend
     */
    public CoreOperationSubtract(Expression arg1, Expression arg2) {
        super(new Expression[]{arg1, arg2});
    }

    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return new Double(l - r);
    }

    protected int getPrecedence() {
        return ADD_PRECEDENCE;
    }

    protected boolean isSymmetric() {
        return false;
    }

    public String getSymbol() {
        return "-";
    }
}
