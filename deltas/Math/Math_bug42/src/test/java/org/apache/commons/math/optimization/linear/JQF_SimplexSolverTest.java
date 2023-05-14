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

package org.apache.commons.math.optimization.linear;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.Precision;
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
public class JQF_SimplexSolverTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testMath713NegativeVariable(@InRange(minDouble = -4, maxDouble = 6) double a1,
                                            @InRange(minDouble = -4, maxDouble = 6) double a2,
                                            @InRange(minDouble = -4, maxDouble = 6) double c1,
                                            @InRange(minDouble = -4, maxDouble = 6) double c2,
                                            @InRange(minDouble = 7, maxDouble = 20) double d
    ) {
        // [Parameters]
        // Randomizing constraints and LinearObjectiveFunction
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{a1, a2}, 0.0d);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[]{c1, c2}, Relationship.EQ, d));
        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        try {
            RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);

            final double ret1 = solution.getPoint()[0];
            final double ret2 = solution.getPoint()[1];
            // [Preservation condition] The original test contains: Assert.assertTrue(Precision.compareTo(solution.getPoint()[0], 0.0d, epsilon) >= 0) and Assert.assertTrue(Precision.compareTo(solution.getPoint()[1], 0.0d, epsilon) >= 0)
            // We reuse the same assertion condition as a preservation condition.
            // [Output] points of solution
            Log.logOutIf(Precision.compareTo(ret1, 0.0d, epsilon) >= 0&&Precision.compareTo(ret2, 0.0d, epsilon) >= 0, () -> new Double[]{ret1, ret2});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
}
