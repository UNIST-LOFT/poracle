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
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.NotStrictlyPositiveException;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * <code>PoissonDistributionTest</code>
 *
 * @version $Revision$ $Date$
 */
@RunWith(JQF.class)
public class JQF_PoissonDistributionTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMean(@InRange(minInt = -10, maxInt = 10) int x) {

        final PoissonDistribution dist;
        // [Parameters]
        // x
        // Note that the API document of PoissonDistributionImpl has the following:
        // "The mean value (x) must be positive"
        try {
            dist = new PoissonDistributionImpl(x);
            // [Preservation condition] we check whether we expect exception or not
            // [Output] the mean of dist
            Log.logOutIf(x > 0, () -> new Double[] { dist.getMean() });
        } catch (NotStrictlyPositiveException ex) {
            Log.logOutIf(x <= 0, () -> new String[] { ex.getClass().toString() });
        } catch (Exception e){
            Log.ignoreOut();
        }
    }
}
