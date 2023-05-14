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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.RealVectorImpl;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;



//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import org.apache.commons.math.util.MathUtils;
import java.util.ArrayList;
import java.io.*;
import java.util.*;

@RunWith(JQF.class)
public class JQF_SimplexSolverTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testSingleVariableAndConstraint(@InRange(minDouble = -2, maxDouble = 8, isFixed=true) double a,
                                                @InRange(minDouble = -4, maxDouble = 6, isFixed=true) double b,
                                                @InRange(minDouble = 5, maxDouble = 15, isFixed=true) double c
                                                ) throws OptimizationException {
            if(a==0) {Log.ignoreOut(); return;}
            // [Parameters]
            // Randomizing the coefficients of LinearObjectiveFunction and constraints
            Double[] fs = {(-1)*a};
            Double[][] cons = new Double[][]{{b}};
            Double[] bs = {c};
            try {
                String path = System.getProperty("user.dir") + "/pylinprog/main.py";
                Process proc = new ProcessBuilder(path,
                        Arrays.deepToString(cons), Arrays.toString(bs), Arrays.toString(fs)).start();
                proc.waitFor();
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(proc.getErrorStream()));
                String s = null;
                double[] sol = new double[1];
                int count = 0;
                while ((s = stdInput.readLine()) != null) {
                    sol[count] = Double.valueOf(s);
                    count++;
                }
                if (count == 0) {
                    Log.ignoreOut();
                    return;
                }
                LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{a}, 0);
                Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
                constraints.add(new LinearConstraint(new double[]{b}, Relationship.LEQ, c));
                SimplexSolver solver = new SimplexSolver();
                double epsilon = 1E-6;
                RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, false);
                double ret1 = solution.getPoint()[0];
                double ret2 = solution.getValue();
                // [Preservation condition] we check whether output from original matches with the output from reference
                // [Output] value and points of solution
                Log.logOutIf(Math.abs(ret1 - sol[0])<epsilon, () -> new Double[]{ret1, ret2});
            }catch (Exception e1){
                Log.ignoreOut();
            }
        }

}
