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

package org.apache.commons.math.ode.events;
import org.apache.commons.math.ode.sampling.DummyStepHandler;

import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
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
public class JQF_EventStateTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testIssue695(@InRange(minDouble = -5, maxDouble = 5) double min,
                             @InRange(minDouble = -5, maxDouble = 5) double max,
                             @InRange(minDouble = 10, maxDouble = 12) double a1,
                             @InRange(minDouble = 10, maxDouble = 12) double a2,
                             @InRange(minDouble = 5, maxDouble = 15) double a3,
                             @InRange(minDouble = 0, maxDouble = 1) double tar) {
        // [Parameters]
        // Randomizing the parameters of the integrate function, the equation and the ResettingEvent constructor
        assumeTrue(min<=max);
        final double temp1 = a1;
        FirstOrderDifferentialEquations equation = new FirstOrderDifferentialEquations() {

            public int getDimension() {
                return 1;
            }

            public void computeDerivatives(double t, double[] y, double[] yDot) {
                yDot[0] = temp1;
            }
        };

        

        try {
            DormandPrince853Integrator integrator = new DormandPrince853Integrator(min, max, 1.0e-14, 1.0e-14);
            integrator.addEventHandler(new ResettingEvent(a1), 0.1, 1.0e-9, 1000);
            integrator.addEventHandler(new ResettingEvent(a2), 0.1, 1.0e-9, 1000);
            integrator.setInitialStepSize(3.0);
            double target = tar;
            double[] y = new double[1];
            final double ret = integrator.integrate(equation, a3, y, target, y);
            // [Preservation condition] Preserve when assertionError does not occur.
            //
            // [Output] we monitor the return value of the method under testing.
            Log.logOutIf(true, () -> new Double[]{ret});
        }catch (AssertionError e){
            Log.ignoreOut();
        }

    }


    private static class ResettingEvent implements EventHandler {

        private static double lastTriggerTime = Double.NEGATIVE_INFINITY;
        private final double tEvent;

        public ResettingEvent(final double tEvent) {
            this.tEvent = tEvent;
        }

        public double g(double t, double[] y) {
            Assert.assertTrue("going backard in time! (" + t + " < " + lastTriggerTime + ")", t >= lastTriggerTime);
            return t - tEvent;
        }

        public Action eventOccurred(double t, double[] y, boolean increasing) {
            // remember in a class variable when the event was triggered
            lastTriggerTime = t;
            return Action.RESET_STATE;
        }

        public void resetState(double t, double[] y) {
            y[0] += 1.0;
        }

    };
}
