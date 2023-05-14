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

package org.apache.commons.math.linear;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;


import org.apache.commons.math.linear.EigenDecomposition;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.TriDiagonalTransformer;
import org.apache.commons.math.util.MathUtils;

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
//import edu.berkeley.cs.jqf.fuzz.reach.Log;
import anonymous.log.Log;
import java.io.*;
@RunWith(JQF.class)
public class JQF_EigenDecompositionImplTest {
    public static Test suite() {
        TestSuite suite = new TestSuite(EigenDecompositionImplTest.class);
        suite.setName("EigenDecompositionImpl Tests");
        return suite;
    }

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMathpbx02(
            @InRange(minDouble=7000, maxDouble=8000, isFixed=true) double d1,
            @InRange(minDouble=18000, maxDouble=19000, isFixed=true) double d2,
            @InRange(minDouble=13000, maxDouble=14000, isFixed=true) double d3,
            @InRange(minDouble=10000, maxDouble=11000, isFixed=true) double d4,
            @InRange(minDouble=500, maxDouble=1500, isFixed=true) double d5,
            @InRange(minDouble=6000, maxDouble=7000, isFixed=true) double d6,
            @InRange(minDouble=-5000, maxDouble=-4000, isFixed=true) double d7,
            @InRange(minDouble=1000, maxDouble=2000, isFixed=true) double d8,
            @InRange(minDouble=5000, maxDouble=6000, isFixed=true) double d9,
            @InRange(minDouble=1000, maxDouble=2000, isFixed=true) double d10,
            @InRange(minDouble=50, maxDouble=150, isFixed=true) double d11,
            @InRange(minDouble=-300, maxDouble=-200, isFixed=true) double d12,
            @InRange(minDouble=69, maxDouble=72, isFixed=true) double d
    ) {
        // [Parameters]
        // Randomizing all elements of tridiagonal matrix

        double[] mainTridiagonal = {
                d1, d2, d3,
                d4, d5, d6,
                d
        };
        double[] secondaryTridiagonal = {
                d7, d8, d9,
                d10, d11, d12
        };
        try {
            // Reference Program ---- START
            String path = System.getProperty("user.dir")+"/pylinprog/eigen.py";
            Process proc = new ProcessBuilder("python3",path,"-W","ignore",
                    Arrays.toString(mainTridiagonal),Arrays.toString(secondaryTridiagonal)).start();
            proc.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));
            String s = null;
            double[] sol = new double[7];
            int count = 0;
            while ((s = stdInput.readLine()) != null) {
                sol[count] = Double.valueOf(s);
                count++;
            }
            if(count==0) {Log.ignoreOut(); return;}
            // Reference Program ---- END
            boolean preserve = true;
            EigenDecomposition decomposition =
                    new EigenDecompositionImpl(mainTridiagonal, secondaryTridiagonal, MathUtils.SAFE_MIN);

            double[] eigenValues = decomposition.getRealEigenvalues();
            for(int k = 0; k<eigenValues.length;k++){
                sol[k] = MathUtils.round(sol[k],3);
                eigenValues[k] = MathUtils.round(eigenValues[k],3);
            }
            Arrays.sort(sol);
            Arrays.sort(eigenValues);
            double epsilon = 1e-6;
            for(int i = 0; i<eigenValues.length;i++){
                if(Math.abs(sol[i]-eigenValues[i])>epsilon){
                    preserve = false;
                }
            }
            // [Preservation condition] we check whether output from original matches with the output from reference
            // [Output] eigen values of the matrix
            Log.logOutIf(preserve,() ->new String[]{Arrays.toString(eigenValues)});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
}
