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
    @Fuzz
    public void testPValueNearZero(@InRange(minInt=1, maxInt=15) int c) throws Exception {
        /*
         * Create a dataset that has r -> 1, p -> 0 as dimension increases.
         * Prior to the fix for MATH-371, p vanished for dimension >= 14.
         * Post fix, p-values diminish smoothly, vanishing at dimension = 127.
         * Tested value is ~1E-303.
         *
         */
        try {
            int dimension = c;
            double[][] data = new double[dimension][2];
            for (int i = 0; i < dimension; i++) {
                data[i][0] = i;
                data[i][1] = i + 1 / ((double) i + 1);
            }
            PearsonsCorrelation corrInstance = new PearsonsCorrelation(data);
            double ret = corrInstance.getCorrelationPValues().getEntry(0, 1);
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }

}
