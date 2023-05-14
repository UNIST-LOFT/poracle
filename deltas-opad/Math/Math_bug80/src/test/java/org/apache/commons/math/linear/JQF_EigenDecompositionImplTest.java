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
@RunWith(JQF.class)
public class JQF_EigenDecompositionImplTest {
    public static Test suite() {
        TestSuite suite = new TestSuite(EigenDecompositionImplTest.class);
        suite.setName("EigenDecompositionImpl Tests");
        return suite;
    }

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

        double[] mainTridiagonal = {
                d1, d2, d3,
                d4, d5, d6,
                d
        };
        double[] secondaryTridiagonal = {
                d7, d8, d9,
                d10, d11, d12
        };
        double[] refEigenValues = {
                20654.744890306974412, 16828.208208485466457,
                6893.155912634994820, 6757.083016675340332,
                5887.799885688558788, 64.309089923240379,
                57.992628792736340
        };
        try {
            EigenDecomposition decomposition =
                    new EigenDecompositionImpl(mainTridiagonal, secondaryTridiagonal, MathUtils.SAFE_MIN);

            double[] eigenValues = decomposition.getRealEigenvalues();
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }
}
