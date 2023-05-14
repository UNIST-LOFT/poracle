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
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
@RunWith(JQF.class)
public class JQF_SimplexSolverTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMath272(@InRange(minDouble = -3, maxDouble = 7,isFixed=true) double a,
                            @InRange(minDouble = -3, maxDouble = 7,isFixed=true) double b,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double c,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double d,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double e,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double f1,
                            @InRange(minDouble = -5, maxDouble = 5,isFixed=true) double g,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double h,
                            @InRange(minDouble = -5, maxDouble = 5,isFixed=true) double i,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double j,
                            @InRange(minDouble = -5, maxDouble = 5,isFixed=true) double k,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double l,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double m,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double n,
                            @InRange(minDouble = -4, maxDouble = 6,isFixed=true) double o
    ) throws OptimizationException {
        // [Parameters]
        // Randomizing the coefficients of LinearObjectiveFunction and constraints
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { a, b, c }, 0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { d, e, f1 }, Relationship.GEQ,  m));
        constraints.add(new LinearConstraint(new double[] { g, h, i }, Relationship.GEQ,  n));
        constraints.add(new LinearConstraint(new double[] { j, k, l }, Relationship.GEQ,  o));
        SimplexSolver solver = new SimplexSolver();
        try {
            Double[][] cons = new Double[][]{{d,e,f1},{g,h,i},{j,k,l}};
            Double[] bs = new Double[]{m,n,o};
            Double[] fs = new Double[]{(-1)*a,(-1)*b,(-1)*c};

            String path = System.getProperty("user.dir")+"/pylinprog/main.py";
            Process proc = new ProcessBuilder(path,
                    Arrays.deepToString(cons),Arrays.toString(bs), Arrays.toString(fs)).start();
            proc.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s = null;
            double[] sol = new double[3];
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                sol[count] = Double.valueOf(s);
                count++;
            }
            if(count==0) {Log.ignoreOut(); return;}
            double epsilon = 1e-6;
            RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
            System.out.println(Arrays.toString(solution.getPoint()));
            System.out.println(Arrays.toString(sol));
            final double p1 = solution.getPoint()[0];
            final double p2 = solution.getPoint()[1];
            final double p3 = solution.getPoint()[2];
            final double v = solution.getValue();
            // [Preservation condition] we check whether output from original matches with the output from reference
            // [Output] value and points of solution
            Log.logOutIf(Math.abs(p1-sol[0])<epsilon&&
                    Math.abs(p2-sol[1])<epsilon&&
                    Math.abs(p3-sol[2])<epsilon, () -> new Double[]{p1, p2, p3, v});
        }catch (Exception e1){
            Log.ignoreOut();
        }
    }

}
