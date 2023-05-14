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
package org.apache.commons.math3.optimization.univariate;


import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.StepFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * @version $Id$
 */
@RunWith(JQF.class)
public final class JQF_BrentOptimizerTest {
    /**
     * Contrived example showing that prior to the resolution of MATH-855,
     * the algorithm, by always returning the last evaluated point, would
     * sometimes not report the best point it had found.
     */
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testMath855(@InRange(minDouble=6.779, maxDouble=6.799) double a,
                            @InRange(minDouble=9.866, maxDouble=9.886) double b,
                            @InRange(minDouble=0, maxDouble=10) double c,
                            @InRange(minInt=-3, maxInt=3) int x1,
                            @InRange(minInt=-3, maxInt=3) int x2,
                            @InRange(minInt=-3, maxInt=3) int x3) {
        // [Parameters]
        // Randomizing values of stepfunction and lower, upper bound of interval
        final double minSin = 3 * Math.PI / 2;
        final double offset = 1e-8;
        final double delta = 1e-7;

        final UnivariateFunction f1 = new Sin();
        final UnivariateFunction f2 = new StepFunction(new double[] { minSin, minSin + offset, minSin + c * offset },
                new double[] { x1, x2, x3 });
        final UnivariateFunction f = FunctionUtils.add(f1, f2);
        final UnivariateOptimizer optimizer = new BrentOptimizer(1e-8, 1e-100);
        final double expected = 4.712389027602411;
        final UnivariatePointValuePair result
                = optimizer.optimize(200, f, GoalType.MINIMIZE, minSin - a * delta, minSin + b * delta);

        double sol = result.getPoint();
        // [Preservation condition]
        // We reuse the same assertion condition as a preservation condition.
        // [Output] coordinate of result
        Log.logOutIf(f.value(sol)<=f.value(expected), ()->new Double[]{sol});
    }
}
