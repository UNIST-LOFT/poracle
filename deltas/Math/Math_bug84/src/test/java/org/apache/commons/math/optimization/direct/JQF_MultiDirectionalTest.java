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

package org.apache.commons.math.optimization.direct;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_MultiDirectionalTest {
    
  // The specified ranges of the parameters are only initial ones, and the actual ranges are 
  // adjusted during the fuzzing process, as described in the paper.    
  @Fuzz
  public void testMinimizeMaximize(@InRange(minDouble=-50, maxDouble=50) double d1)
      throws FunctionEvaluationException, ConvergenceException {
        // [Parameters]
        // Randomizing start point of optimization
        MultivariateRealFunction fourExtrema = new MultivariateRealFunction() {
            private static final long serialVersionUID = -7039124064449091152L;

            public double value(double[] variables) throws FunctionEvaluationException {
                final double x = variables[0];
                final double y = variables[1];
                return ((x == 0) || (y == 0)) ? 0 : (Math.atan(x) * Math.atan(x + 2) * Math.atan(y) * Math.atan(y) / (x * y));
            }
        };
        MultiDirectional optimizer = new MultiDirectional();
        optimizer.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-11, 1.0e-30));
        optimizer.setMaxIterations(200);
        optimizer.setStartConfiguration(new double[]{0.2, 0.2});
        RealPointValuePair optimum;
        RealPointValuePair expected_optimum;
        NelderMead nd = new NelderMead();
        nd.setConvergenceChecker(new SimpleScalarValueChecker(1.0e-11, 1.0e-30));
        nd.setMaxIterations(200);
        nd.setStartConfiguration(new double[]{0.2, 0.2});
        try {
            optimum = optimizer.optimize(fourExtrema, GoalType.MINIMIZE, new double[]{d1, 0});
            expected_optimum = nd.optimize(fourExtrema, GoalType.MINIMIZE, new double[]{d1, 0});
            final double ret1 = optimum.getPoint()[0];
            final double ret2 = optimum.getPoint()[1];
            final double ret3 = optimum.getValue();
            final double expected1 = expected_optimum.getPoint()[0];
            final double expected2 = expected_optimum.getPoint()[1];
            final double expected3 = expected_optimum.getValue();
            // [Preservation condition] we check whether output from original matches with the output from reference
            // [Output] value of solution
            Log.logOutIf(Math.abs(ret3 - expected3) < 8.0e-13, () -> new Double[]{ret3});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
}
