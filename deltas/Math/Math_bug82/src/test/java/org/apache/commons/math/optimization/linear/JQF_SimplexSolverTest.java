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
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import  java.io.*;
import java.util.*;

@RunWith(JQF.class)
public class JQF_SimplexSolverTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMath288(
            @InRange(minInt = -3, maxInt = 7) int a,
            @InRange(minInt = -3, maxInt = 7) int b,
            @InRange(minInt = -4, maxInt = 6) int c,
            @InRange(minInt = -3, maxInt = 6) int d)
            throws OptimizationException {
        // [Parameters]
        // Randomizing the coefficients of LinearObjectiveFunction
        if(a==0||b==0||c==0||d==0) {Log.ignoreOut(); return;}

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {a, b, c, d}, 0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { 3, 0, -5, 0 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 2, 0, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 0, 3, 0, -5 }, Relationship.LEQ, 0.0));
        constraints.add(new LinearConstraint(new double[] { 1, 0, 0, 0 }, Relationship.LEQ, 1.0));
        constraints.add(new LinearConstraint(new double[] { 0, 1, 0, 0 }, Relationship.LEQ, 1.0));
        try {
        Integer[][] cons = new Integer[][]{{3, 0, -5, 0},{2, 0, 0, -5},{0, 3, 0, -5},{1, 0, 0, 0},{0, 1, 0, 0}};
        Integer[] bs = new Integer[]{0,0,0,1,1};
        Integer[] fs = new Integer[]{(-1)*a,(-1)*b,(-1)*c,(-1)*d};
        String path = System.getProperty("user.dir")+"/pylinprog/main.py";
        Process proc = new ProcessBuilder(path,
                Arrays.deepToString(cons),Arrays.toString(bs), Arrays.toString(fs)).start();
        proc.waitFor();
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));
        String s = null;
        double[] sol = new double[4];
        int count = 0;
        while ((s = stdInput.readLine()) != null) {
            sol[count] = Double.valueOf(s);
            count++;
        }
        if(count==0) {Log.ignoreOut(); return;}

            SimplexSolver solver = new SimplexSolver();
            RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
            double epsilon = 1e-6;
            final double ret0 = solution.getPoint()[0];
            final double ret1 = solution.getPoint()[1];
            final double ret2 = solution.getPoint()[2];
            final double ret3 = solution.getPoint()[3];
            final double ret = solution.getValue();
            // [Preservation condition] we check whether output from original matches with the output from reference
            // [Output] value and points of solution
            Log.logOutIf(Math.abs(sol[0]-ret0)<epsilon&&Math.abs(sol[1]-ret1)<epsilon&&
                        Math.abs(sol[2]-ret2)<epsilon&&Math.abs(sol[3]-ret3)<epsilon, () -> new Double[]{ret0,ret1,ret2,ret3,ret});
        }catch (Exception e){
            Log.ignoreOut();
        }
    }

}
