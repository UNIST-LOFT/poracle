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
import java.util.Arrays;

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

public final class JQF_BrentSolverTest extends TestCase {
    public JQF_BrentSolverTest(String name) {
        super(name);
    }

    public void testBadEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        BrentSolver solver = new BrentSolver();
        solver.setFunctionValueAccuracy(-1);
        double yMin = f.value(0);
        double yMax = f.value(6);
        try {
            double ret = solver.solve(f, 0, 6, 5);
            Double[] results = new Double[] {ret};
            System.out.println(Arrays.toString(results));
        }catch (IllegalArgumentException ex) {
            String[] result = new String[] {ex.getClass().toString()};
            System.out.println(Arrays.toString(result));
        }catch (Exception e){
            System.out.println("Exception");
        }
    }

}
