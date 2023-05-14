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
package org.apache.commons.math.stat.correlation;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.BlockRealMatrix;

import junit.framework.TestCase;

//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;


@RunWith(JQF.class)
public class JQF_PearsonsCorrelationTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testPValueNearZero(@InRange(minInt=1, maxInt=15) int c) throws Exception {
        // [Parameters]
        // Randomizing the dimension of data
        try {
            int dimension = c;
            double[][] data = new double[dimension][2];
            for (int i = 0; i < dimension; i++) {
                data[i][0] = i;
                data[i][1] = i + 1 / ((double) i + 1);
            }
            PearsonsCorrelation corrInstance = new PearsonsCorrelation(data);
            double ret = corrInstance.getCorrelationPValues().getEntry(0, 1);
            // [Preservation condition] The original test contains: assertTrue(corrInstance.getCorrelationPValues().getEntry(0, 1) > 0);
            // We reuse the same assertion condition as a preservation condition.
            //
            // [Output] return value of function which is under testing.
            //
            Log.logOutIf(ret>0, () -> new Double[]{ret});
        } catch (Exception e) {
           Log.ignoreOut();
        }
    }

}
