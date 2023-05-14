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

package org.apache.commons.math3.optimization.linear;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.Precision;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import  java.io.*;


@RunWith(JQF.class)
public class JQF_SimplexSolverTest {


    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testMath781(
            @InRange(minInt = -10, maxInt = 10, isFixed=true) int a,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int b,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int c,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int d,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int e,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int f1,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int g,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int h,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int i,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int j,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int k,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int l,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int m,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int n,
            @InRange(minInt = -10, maxInt = 10,isFixed=true) int o
    ) {
        // [Parameters]
        // Randomizing the coefficients of LinearObjectiveFunction and constraints
        if(a==0||b==0||c==0) {Log.ignoreOut(); return;}
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { a, b, c }, 0);

        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { d, e, f1 }, Relationship.LEQ, m));
        constraints.add(new LinearConstraint(new double[] { g, h, i }, Relationship.LEQ, n));
        constraints.add(new LinearConstraint(new double[] { j, k, l }, Relationship.LEQ, o));

        try {
            // Reference program environment -- START
            Integer[][] cons = new Integer[][]{{d,(-1)*d,e,(-1)*e,f1,(-1)*f1},{g,(-1)*g,h,(-1)*h,i,(-1)*i},{j,(-1)*j,k,(-1)*k,l,(-1)*l}};
            Integer[] bs = new Integer[]{m,n,o};
            Integer[] fs = new Integer[]{(-1)*a,a,(-1)*b,b,(-1)*c,c};

            String path = System.getProperty("user.dir")+"/pylinprog/main.py";
            Process proc = new ProcessBuilder(path,
                    Arrays.deepToString(cons),Arrays.toString(bs), Arrays.toString(fs)).start();
            proc.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s = null;
            double[] sol = new double[6];
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                sol[count] = Double.valueOf(s);
                count++;
            }
            if(count==0) {Log.ignoreOut(); return;}
            // Reference program environment -- END
            double epsilon = 1e-6;
            SimplexSolver solver = new SimplexSolver(1e-16,10);
            PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, false);
            final double ret0 = solution.getPoint()[0];
            final double ret1 = solution.getPoint()[1];
            final double ret2 = solution.getPoint()[2];
            final double ret3 = solution.getValue();
            // [Preservation condition] we check whether output from original matches with the output from reference
            // [Output] value and points of solution
            Log.logOutIf(Math.abs(ret0-(sol[0]-sol[1]))<epsilon&&
                    Math.abs(ret1-(sol[2]-sol[3]))<epsilon&&
                    Math.abs(ret2-(sol[4]-sol[5]))<epsilon, ()-> new Double[]{ret0, ret1, ret2, ret3});
        }
        catch (Exception e1) {
            Log.ignoreOut();
        }
    }
}
