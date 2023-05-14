/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import org.apache.commons.math.analysis.function.Sin;


/**
 * Test case for {@link RegulaFalsiSolver Regula Falsi} solver.
 *
 * @version $Id$
 */
@RunWith(JQF.class)
public final class JQF_RegulaFalsiSolverTest extends BaseSecantSolverAbstractTest {
    /** {@inheritDoc} */

    protected UnivariateRealSolver getSolver() {
        return new RegulaFalsiSolver();
    }

    protected int[] getQuinticEvalCounts() {
        // While the Regula Falsi method guarantees convergence, convergence
        // may be extremely slow. The last test case does not converge within
        // even a million iterations. As such, it was disabled.
        return new int[] {3, 7, 8, 19, 18, 11, 67, 55, 288, 151, -1};
    }

    //@Test(expected=TooManyEvaluationsException.class)
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testIssue631(@InRange(minInt=3619, maxInt=3629) int maxEval,
                             @InRange(minInt=-4, maxInt=6) int a1,
                             @InRange(minInt=5, maxInt=15) int a2) {
        // [Parameters]
        // Randomizing all parameters of solve function
        final UnivariateRealFunction f = new UnivariateRealFunction() {
                /** {@inheritDoc} */
                public double value(double x) {
                    return Math.exp(x) - Math.pow(Math.PI, 3.0);
                }
            };

        final PegasusSolver solver = new PegasusSolver();
        try {
            final double root = solver.solve(maxEval, f, a1, a2);
            // [Preservation condition]
            // PegasusSolver runs correctly in the original version, according to the following bug report:
            // https://issues.apache.org/jira/plugins/servlet/mobile#issue/MATH-631
            //
            // [Output] root of function and its evaluations
            Log.logOutIf(true, () -> new Double[] { root, (double)solver.getEvaluations() });
        } catch (Exception e) {
            Log.logOutIf(true, ()-> new String[]{ e.getClass().toString()} );
        }
    }

}
