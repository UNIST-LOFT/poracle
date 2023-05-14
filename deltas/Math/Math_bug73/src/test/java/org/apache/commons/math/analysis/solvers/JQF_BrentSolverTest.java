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

import junit.framework.TestCase;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.MonitoredFunction;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;
import org.apache.commons.math.MathRuntimeException;
/**
 * Testcase for UnivariateRealSolver.
 * Because Brent-Dekker is guaranteed to converge in less than the default
 * maximum iteration count due to bisection fallback, it is quite hard to
 * debug. I include measured iteration counts plus one in order to detect
 * regressions. On average Brent-Dekker should use 4..5 iterations for the
 * default absolute accuracy of 10E-8 for sinus and the quintic function around
 * zero, and 5..10 iterations for the other zeros.
 *
 * @version $Revision:670469 $ $Date:2008-06-23 10:01:38 +0200 (lun., 23 juin 2008) $
 */
@RunWith(JQF.class)
public final class JQF_BrentSolverTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testBadEndpoints(@InRange(minInt=-1, maxInt=9, isFixed = true)int min,
                                 @InRange(minInt= 1, maxInt=20, isFixed = true) int max,
                                 @InRange(minInt= 1, maxInt=20, isFixed = true) int initial) throws Exception {
        // [Parameters]
        // Randomizing all parameters of the solve function
        UnivariateRealFunction f = new SinFunction();
        BrentSolver solver = new BrentSolver();
        solver.setFunctionValueAccuracy(-1);
        double yMin = f.value(min);
        double yMax = f.value(max);
        double yInitial = f.value(initial);
        try {
            double ret = solver.solve(f, min, max, initial);
            // [Preservation condition] The API document of solve has the following:
            // "Throws IllegalArgumentException if the values of the function at the three points have the same sign"
            //
            // [Output] ret (the return value of method which is under testing)
            Log.logOutIf(!((yMin > 0 && yMax > 0 && yInitial > 0) || (yMin < 0 && yMax < 0 && yInitial < 0)), 
                         () -> new Double[]{ret});
        } catch (IllegalArgumentException ex) {
            Log.logOutIf(((yMin > 0 && yMax > 0 && yInitial > 0) || (yMin < 0 && yMax < 0 && yInitial < 0)), 
                         () -> new String[]{ ex.getClass().toString() });
        } catch (Exception e){
            Log.ignoreOut();
        }
    }

}
