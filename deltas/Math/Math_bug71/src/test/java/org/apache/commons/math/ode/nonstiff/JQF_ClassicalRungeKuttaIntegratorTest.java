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

import junit.framework.*;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.TestProblem1;
import org.apache.commons.math.ode.TestProblem3;
import org.apache.commons.math.ode.TestProblem5;
import org.apache.commons.math.ode.TestProblemAbstract;
import org.apache.commons.math.ode.TestProblemFactory;
import org.apache.commons.math.ode.TestProblemHandler;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import java.util.stream.Stream;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_ClassicalRungeKuttaIntegratorTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMissedEndEvent(@InRange(minDouble =1878250315.0000029, maxDouble = 1878250325.0000029) double d1,
                                   @InRange(minDouble =1878250374.9999986, maxDouble = 1878250383.9999986) double d2,
                                   @InRange(minDouble =115, maxDouble = 125) double add
                                   ) throws IntegratorException, DerivativeException {
        // [Parameters]
        // Randomizing initial and target time for integration
        final double   t0     = d1;
        final double   tEvent = d2;
        final double[] k      = { 1.0e-4, 1.0e-5, 1.0e-6 };
        FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {

            public int getDimension() {
                return k.length;
            }

            public void computeDerivatives(double t, double[] y, double[] yDot) {
                for (int i = 0; i < y.length; ++i) {
                    yDot[i] = k[i] * y[i];
                }
            }
        };

        ClassicalRungeKuttaIntegrator integrator = new ClassicalRungeKuttaIntegrator(60.0);

        double[] y0   = new double[k.length];
        for (int i = 0; i < y0.length; ++i) {
            y0[i] = i + 1;
        }
        final double[] y1    = new double[k.length];
        final double[] y2    = new double[k.length];
        try {
            final double finalT = integrator.integrate(ode, t0, y0, tEvent, y1);
            integrator.addEventHandler(new EventHandler() {

                public void resetState(double t, double[] y) {
                }

                public double g(double t, double[] y) {
                    return t - tEvent;
                }

                public int eventOccurred(double t, double[] y, boolean increasing) {
                    Assert.assertEquals(tEvent, t, 5.0e-6);
                    return CONTINUE;
                }
            }, Double.POSITIVE_INFINITY, 1.0e-20, 100);

            final double finalT2 = integrator.integrate(ode, t0, y0, tEvent + add, y2);

            boolean preserve = true;
            for (int i = 0; i < y2.length; ++i) {
                // [Preservation condition]
                // We reuse the same assertion condition as a preservation condition.
                if (!(Math.abs(y0[i] * Math.exp(k[i] * (finalT2 - t0)) - y2[i]) <= 1.0e-9) || !(Math.abs(y0[i] * Math.exp(k[i] * (finalT - t0)) - y1[i])<=1.0e-9)) {
                    preserve = false;
                    break;
                }
            }

            // [Output] changed variables and arrays by method which is under testing
            Log.logOutIf(preserve, () -> new Double[]{finalT, finalT2, y1[0],
                    y1[1], y1[2], y2[0], y2[1], y2[2]});
        }catch (Exception e){
            Log.ignoreOut();
        }catch (AssertionError error){
            Log.ignoreOut();
        }
    }
}
