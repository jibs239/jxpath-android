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

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.axes.*;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public abstract class Path extends Expression {

    private Step[] steps;
    private boolean basicKnown = false;
    private boolean basic;

    /**
     * Create a new Path.
     *
     * @param steps that compose the Path
     */
    public Path(Step[] steps) {
        this.steps = steps;
    }

    /**
     * Get the steps.
     *
     * @return Step[]
     */
    public Step[] getSteps() {
        return steps;
    }

    public boolean computeContextDependent() {
        if (steps != null) {
            for (int i = 0; i < steps.length; i++) {
                if (steps[i].isContextDependent()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recognizes paths formatted as <code>foo/bar[3]/baz[@name = 'biz']</code>.
     * The evaluation of such "simple" paths is optimized and
     * streamlined.
     *
     * @return <code>true</code> if this path is simple
     */
    public synchronized boolean isSimplePath() {
        if (!basicKnown) {
            basicKnown = true;
            basic = true;
            Step[] steps = getSteps();
            for (int i = 0; i < steps.length; i++) {
                if (!isSimpleStep(steps[i])) {
                    basic = false;
                    break;
                }
            }
        }
        return basic;
    }

    /**
     * A Step is "simple" if it takes one of these forms: ".", "/foo",
     * "@bar", "/foo[3]". If there are predicates, they should be
     * context independent for the step to still be considered simple.
     *
     * @param step the step to check
     * @return boolean
     */
    protected boolean isSimpleStep(Step step) {
        if (step.getAxis() == Compiler.AXIS_SELF) {
            NodeTest nodeTest = step.getNodeTest();
            if (!(nodeTest instanceof NodeTypeTest)) {
                return false;
            }
            int nodeType = ((NodeTypeTest) nodeTest).getNodeType();
            if (nodeType != Compiler.NODE_TYPE_NODE) {
                return false;
            }
            return areBasicPredicates(step.getPredicates());
        }
        if (step.getAxis() == Compiler.AXIS_CHILD
                || step.getAxis() == Compiler.AXIS_ATTRIBUTE) {
            NodeTest nodeTest = step.getNodeTest();
            if (!(nodeTest instanceof NodeNameTest)) {
                return false;
            }
            if (((NodeNameTest) nodeTest).isWildcard()) {
                return false;
            }
            return areBasicPredicates(step.getPredicates());
        }
        return false;
    }

    /**
     * Learn whether the elements of the specified array are "basic" predicates.
     *
     * @param predicates the Expression[] to check
     * @return boolean
     */
    protected boolean areBasicPredicates(Expression[] predicates) {
        if (predicates != null && predicates.length != 0) {
            boolean firstIndex = true;
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i] instanceof NameAttributeTest) {
                    if (((NameAttributeTest) predicates[i])
                            .getNameTestExpression()
                            .isContextDependent()) {
                        return false;
                    }
                } else if (predicates[i].isContextDependent()) {
                    return false;
                } else {
                    if (!firstIndex) {
                        return false;
                    }
                    firstIndex = false;
                }
            }
        }
        return true;
    }

    /**
     * Given a root context, walks a path therefrom and finds the
     * pointer to the first element matching the path.
     *
     * @param context evaluation context
     * @return Pointer
     */
    protected Pointer getSingleNodePointerForSteps(EvalContext context) {
        if (steps.length == 0) {
            return context.getSingleNodePointer();
        }

        if (isSimplePath()) {
            NodePointer ptr = (NodePointer) context.getSingleNodePointer();
            return SimplePathInterpreter.interpretSimpleLocationPath(
                    context,
                    ptr,
                    steps);
        }
        return searchForPath(context);
    }

    /**
     * The idea here is to return a NullPointer rather than null if that's at
     * all possible. Take for example this path: "//map/key". Let's say, "map"
     * is an existing node, but "key" is not there. We will create a
     * NullPointer that can be used to set/create the "key" property.
     * <p>
     * However, a path like "//key" would still produce null, because we have
     * no way of knowing where "key" would be if it existed.
     * </p>
     * <p>
     * To accomplish this, we first try the path itself. If it does not find
     * anything, we chop off last step of the path, as long as it is a simple
     * one like child:: or attribute:: and try to evaluate the truncated path.
     * If it finds exactly one node - create a NullPointer and return. If it
     * fails, chop off another step and repeat. If it finds more than one
     * location - return null.
     * </p>
     *
     * @param context evaluation context
     * @return Pointer
     */
    protected Pointer searchForPath(EvalContext context) {
        EvalContext ctx = buildContextChain(context, steps.length, true);
        Pointer pointer = ctx.getSingleNodePointer();

        if (pointer != null) {
            return pointer;
        }

        for (int i = steps.length; --i > 0; ) {
            if (!isSimpleStep(steps[i])) {
                return null;
            }
            ctx = buildContextChain(context, i, true);
            if (ctx.hasNext()) {
                Pointer partial = (Pointer) ctx.next();
                if (ctx.hasNext()) {
                    // If we find another location - the search is
                    // ambiguous, so we report failure
                    return null;
                }
                if (partial instanceof NodePointer) {
                    return SimplePathInterpreter.createNullPointer(
                            context,
                            (NodePointer) partial,
                            steps,
                            i);
                }
            }
        }
        return null;
    }

    /**
     * Given a root context, walks a path therefrom and builds a context
     * that contains all nodes matching the path.
     *
     * @param context evaluation context
     * @return EvaluationContext
     */
    protected EvalContext evalSteps(EvalContext context) {
        return buildContextChain(context, steps.length, false);
    }

    /**
     * Build a context from a chain of contexts.
     *
     * @param context              evaluation context
     * @param stepCount            number of steps to descend
     * @param createInitialContext whether to create the initial context
     * @return created context
     */
    protected EvalContext buildContextChain(
            EvalContext context,
            int stepCount,
            boolean createInitialContext) {
        if (createInitialContext) {
            context = new InitialContext(context);
        }
        if (steps.length == 0) {
            return context;
        }
        for (int i = 0; i < stepCount; i++) {
            context =
                    createContextForStep(
                            context,
                            steps[i].getAxis(),
                            steps[i].getNodeTest());
            Expression[] predicates = steps[i].getPredicates();
            if (predicates != null) {
                for (int j = 0; j < predicates.length; j++) {
                    if (j != 0) {
                        context = new UnionContext(context, new EvalContext[]{context});
                    }
                    context = new PredicateContext(context, predicates[j]);
                }
            }
        }
        return context;
    }

    /**
     * Different axes are serviced by different contexts. This method
     * allocates the right context for the supplied step.
     *
     * @param context  evaluation context
     * @param axis     code
     * @param nodeTest node test
     * @return EvalContext
     */
    protected EvalContext createContextForStep(
            EvalContext context,
            int axis,
            NodeTest nodeTest) {
        if (nodeTest instanceof NodeNameTest) {
            QName qname = ((NodeNameTest) nodeTest).getNodeName();
            String prefix = qname.getPrefix();
            if (prefix != null) {
                String namespaceURI = context.getJXPathContext()
                        .getNamespaceURI(prefix);
                nodeTest = new NodeNameTest(qname, namespaceURI);
            }
        }

        switch (axis) {
            case Compiler.AXIS_ANCESTOR:
                return new AncestorContext(context, false, nodeTest);
            case Compiler.AXIS_ANCESTOR_OR_SELF:
                return new AncestorContext(context, true, nodeTest);
            case Compiler.AXIS_ATTRIBUTE:
                return new AttributeContext(context, nodeTest);
            case Compiler.AXIS_CHILD:
                return new ChildContext(context, nodeTest, false, false);
            case Compiler.AXIS_DESCENDANT:
                return new DescendantContext(context, false, nodeTest);
            case Compiler.AXIS_DESCENDANT_OR_SELF:
                return new DescendantContext(context, true, nodeTest);
            case Compiler.AXIS_FOLLOWING:
                return new PrecedingOrFollowingContext(context, nodeTest, false);
            case Compiler.AXIS_FOLLOWING_SIBLING:
                return new ChildContext(context, nodeTest, true, false);
            case Compiler.AXIS_NAMESPACE:
                return new NamespaceContext(context, nodeTest);
            case Compiler.AXIS_PARENT:
                return new ParentContext(context, nodeTest);
            case Compiler.AXIS_PRECEDING:
                return new PrecedingOrFollowingContext(context, nodeTest, true);
            case Compiler.AXIS_PRECEDING_SIBLING:
                return new ChildContext(context, nodeTest, true, true);
            case Compiler.AXIS_SELF:
                return new SelfContext(context, nodeTest);
            default:
                return null; // Never happens
        }
    }
}
