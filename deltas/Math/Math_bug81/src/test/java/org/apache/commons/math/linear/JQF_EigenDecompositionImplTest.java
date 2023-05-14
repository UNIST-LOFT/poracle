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

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

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
    public void testMath308(@InRange(minDouble =-1, maxDouble=1)double d8) {
        // [Parameters]
        // Through some experiments, we decided to randomize one element of secondaryTridiagonal
        double[] mainTridiagonal = {
                 22.330154644539597, 46.65485522478641, 17.393672330044705, 54.46687435351116, 80.17800767709437
        };
        double[] secondaryTridiagonal = {
                13.04450406501361, -5.977590941539671, d8, 7.1570352792841225
        };

        try {
            EigenDecomposition decomposition =
                    new EigenDecompositionImpl(mainTridiagonal, secondaryTridiagonal, MathUtils.SAFE_MIN);

            double[] eigenValues = decomposition.getRealEigenvalues();
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] eigen values of tridiagonal matrix
            Log.logOutIf(true, () -> new String[]{java.util.Arrays.toString(eigenValues)});
        } catch (Exception e) {
           Log.ignoreOut();
        }
    }


}
