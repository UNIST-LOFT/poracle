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

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.TestProblem1;
import org.apache.commons.math.ode.TestProblem3;
import org.apache.commons.math.ode.TestProblem4;
import org.apache.commons.math.ode.TestProblem5;
import org.apache.commons.math.ode.TestProblemHandler;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;
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
import static org.junit.Assume.*;


@RunWith(JQF.class)
public class JQF_DormandPrince853IntegratorTest {
  
  // The specified ranges of the parameters are only initial ones, and the actual ranges are 
  // adjusted during the fuzzing process, as described in the paper. 
  @Fuzz
  public void testTooLargeFirstStep(@InRange(minDouble = 0, maxDouble = 5) double a,
                                    @InRange(minDouble = 0, maxDouble = 5) double b,
                                    @InRange(minDouble=-5,maxDouble=6)double c) {
      // [Parameters]
      // min and max interval of integration and constant
      AdaptiveStepsizeIntegrator integ =
              new DormandPrince853Integrator(0, Double.POSITIVE_INFINITY, Double.NaN, Double.NaN);
      assumeTrue(a <= b);
      final double start = a;
      final double end   = b;
      FirstOrderDifferentialEquations equations = new FirstOrderDifferentialEquations() {

          public int getDimension() {
              return 1;
          }
              public void computeDerivatives ( double t, double[] y, double[] yDot){
                  Assert.assertTrue(t >= FastMath.nextAfter(start, Double.NEGATIVE_INFINITY));
                  Assert.assertTrue(t <= FastMath.nextAfter(end, Double.POSITIVE_INFINITY));
                  yDot[0] = -100.0 * y[0];
          }

      };
          try {
              // [Preservation condition] Preserve when assertionError does not occur.
              //
              // [Output] we monitor the return value of the method under testing.
              integ.setStepSizeControl(0, 1.0, 1.0e-6, 1.0e-8);
              final double ret = integ.integrate(equations, start, new double[] { c }, end, new double[1]);
              Log.logOutIf(true, ()->new Double[]{ret});
          }catch (AssertionError e){
              Log.ignoreOut();
          }
    }


}
