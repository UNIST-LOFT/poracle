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
  @Fuzz
  public void testMath283(@InRange(minDouble = -7, maxDouble = 3) double xOpt,
                          @InRange(minDouble = -7, maxDouble = 3) double yOpt,
                          @InRange(minDouble = 0, maxDouble = 5) double std)
      throws FunctionEvaluationException, OptimizationException {
      MultiDirectional multiDirectional = new MultiDirectional();
      multiDirectional.setMaxIterations(100);
      multiDirectional.setMaxEvaluations(1000);
      final Gaussian2D function = new Gaussian2D(xOpt, yOpt, std);
      final double expectedMaximum = function.getMaximum();
      final double[] expectedPosition = function.getMaximumPosition();
      try {
          RealPointValuePair estimate = multiDirectional.optimize(function,
                  GoalType.MAXIMIZE, function.getMaximumPosition());
          final double EPSILON = 1e-5;
          final double actualMaximum = estimate.getValue();
          final double[] actualPosition = estimate.getPoint();
//          Log.logOutIf((Math.abs(actualPosition[0]-expectedPosition[0])<=EPSILON) && (Math.abs(actualPosition[1]-expectedPosition[1])<=EPSILON), () -> new Double[]{actualMaximum, actualPosition[0], actualPosition[1]}, new Double[]{expectedMaximum, expectedPosition[0], expectedPosition[1]});
          if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
          else Log.ignoreOut();
      } catch (Exception e){
          Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
      }
  }
    private static class Gaussian2D implements MultivariateRealFunction {

        private final double[] maximumPosition;

        private final double std;

        public Gaussian2D(double xOpt, double yOpt, double std) {
            maximumPosition = new double[] { xOpt, yOpt };
            this.std = std;
        }

        public double getMaximum() {
            return value(maximumPosition);
        }

        public double[] getMaximumPosition() {
            return maximumPosition.clone();
        }

        public double value(double[] point) {
            final double x = point[0], y = point[1];
            final double twoS2 = 2.0 * std * std;
            return 1.0 / (twoS2 * Math.PI) * Math.exp(-(x * x + y * y) / twoS2);
        }
    }
}
