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
package org.apache.commons.math.analysis;

import org.apache.commons.math.MathException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

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
    public static Test suite() {
        TestSuite suite = new TestSuite(BrentSolverTest.class);
        suite.setName("UnivariateRealSolver Tests");
        return suite;
    }
        
        // The specified ranges of the parameters are only initial ones, and the actual ranges are 
        // adjusted during the fuzzing process, as described in the paper.  
        @Fuzz
        public void testRootEndpoints(
                @InRange(minDouble = -2, maxDouble = 8) double d1,
                @InRange(minDouble = -1, maxDouble = 9) double d2)
                throws Exception {
            // [Parameters]
            // Randomizing parameters of solve function
            UnivariateRealFunction f = new SinFunction();
            UnivariateRealSolver solver = new BrentSolver(f);
            try {
                double b = d1;
                double c = d2;
                final double ret1 = solver.solve(Math.PI, b);
                final double ret2 = solver.solve(c, Math.PI);
                // [Preservation condition] we use true for an unexpected exception case.
                // [Output] ret1, ret2 (which is the return value of the method under testing)
                Log.logOutIf(true, () -> new Double[]{ret1, ret2});
            }catch (Exception e){
                Log.ignoreOut();
            }

            }
    }

    

