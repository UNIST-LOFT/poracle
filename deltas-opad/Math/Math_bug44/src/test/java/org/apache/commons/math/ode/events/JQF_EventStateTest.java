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

@RunWith(JQF.class)
public class JQF_EventStateTest {
//    public static class DoubleArrayGenerator extends Generator<Double[]> {
//        public DoubleArrayGenerator() {
//            super(Double[].class);
//        }
//
//        public Double[] generate(SourceOfRandomness random, GenerationStatus __ignore__) {
//            double minStep = random.nextDouble(-1, 0);
//            double maxStep = random.nextDouble(0,1);
//            if(minStep>maxStep){
//                double temp = minStep;
//                minStep = maxStep;
//                maxStep = temp;
//            }
//            Double[] temp = new Double[5];
//            temp[0]= minStep;
//            temp[1] = maxStep;
//            for(int i = 2; i<5; i++){
//                temp[i]  = random.nextDouble(10,12);
//            }
//            java.util.Arrays.sort(temp);
//            temp[4] = random.nextDouble(0,1);
//            return temp;
//
//        }
//    }


    @Fuzz
    public void testIssue695(@InRange(minDouble = -1, maxDouble = 0) double min,
                             @InRange(minDouble = 0, maxDouble = 1) double max,
                             @InRange(minDouble = 10, maxDouble = 12) double a1,
                             @InRange(minDouble = 10, maxDouble = 12) double a2,
                             @InRange(minDouble = 0, maxDouble = 1) double tar) {
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
            //  integrator.addEventHandler(new ResettingEvent1(a2*a2), 0.1, 1.0e-9, 1000);
            //  integrator.addEventHandler(new ResettingEvent1(a2/a1), 0.1, 1.0e-9, 1000);
            //  integrator.addEventHandler(new ResettingEvent1(a1+a2), 0.1, 1.0e-9, 1000);

            integrator.setInitialStepSize(3.0);
            double target = tar;
            double[] y = new double[1];
            final double ret1 = integrator.integrate(equation, 10, y, target, y);

            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }


    }


    private static class ResettingEvent implements EventHandler {

        private static double lastTriggerTime = Double.NEGATIVE_INFINITY;
        private final double tEvent;

        public ResettingEvent(final double tEvent) {
            this.tEvent = tEvent;
        }

        public double g(double t, double[] y) {
            // the bug corresponding to issue 695 causes the g function
            // to be called at obsolete times t despite an event
            // occurring later has already been triggered.
            // When this occurs, the following assertion is violated
            Assert.assertTrue("going backard in time! (" + t + " < " + lastTriggerTime + ")",
                              t >= lastTriggerTime);
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

    private static class ResettingEvent1 implements EventHandler {

        private static double lastTriggerTime = Double.NEGATIVE_INFINITY;
        private final double tEvent;

        public ResettingEvent1(final double tEvent) {
            this.tEvent = tEvent;
        }

        public double g(double t, double[] y) {
            // the bug corresponding to issue 695 causes the g function
            // to be called at obsolete times t despite an event
            // occurring later has already been triggered.
            // When this occurs, the following assertion is violated
            Assert.assertTrue("going backard in time! (" + t + " < " + lastTriggerTime + ")",
                    t >= lastTriggerTime);
            return t+tEvent;
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
