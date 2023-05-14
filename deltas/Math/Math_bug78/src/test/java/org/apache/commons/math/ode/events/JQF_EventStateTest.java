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

import junit.framework.Assert;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
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
    // JIRA: MATH-322
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void closeEvents(@InRange(minDouble=85, maxDouble=100) double d1,
                            @InRange(minDouble=130,maxDouble=140) double d2,
                            @InRange(minDouble=-3.5, maxDouble=6.5) double d3,
                            @InRange(minDouble=-4.5, maxDouble=5.5) double d4)
        throws EventException, ConvergenceException, DerivativeException {
        // [Parameters]
        // Randomizing constants of EventHandler and the parameters EventState
        final double r1  = d1;
        final double r2  = d2;
        final double gap = r2 - r1;
        EventHandler closeEventsGenerator = new EventHandler() {
            public void resetState(double t, double[] y) {
            }
            public double g(double t, double[] y) {
                return (t - r1) * (r2 - t);
            }
            public int eventOccurred(double t, double[] y, boolean increasing) {
                return CONTINUE;
            }
        };

        try {
            final double tolerance = 0.1;
            EventState es = new EventState(closeEventsGenerator, d3 * gap, tolerance, 10);
            double t0 = r1 - d4 * gap;
            es.reinitializeBegin(t0, new double[0]);
            AbstractStepInterpolator interpolator =
                    new DummyStepInterpolator(new double[0], true);
            interpolator.storeTime(t0);

            interpolator.shift();
            interpolator.storeTime(0.5 * (r1 + r2));
            final double ret1 = es.getEventTime();
            es.evaluateStep(interpolator);
            es.stepAccepted(es.getEventTime(), new double[0]);
            interpolator.shift();
            interpolator.storeTime(r2 + 0.4 * gap);
            final double ret2 = es.getEventTime();
            es.evaluateStep(interpolator);
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] ret1 and ret2 (which is the return value of the method under testing)
            Log.logOutIf(true, ()-> new Double[]{ret1,ret2});
        }catch (Exception e){
          Log.ignoreOut();
        }

    }

}
