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

import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
/**
 * Test cases for NormalDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
@RunWith(JQF.class)
public class JQF_NormalDistributionTest extends ContinuousDistributionAbstractTest  {
    /**
     * Constructor for NormalDistributionTest.
     * @param arg0
     */

    /** Creates the default continuous distribution instance to use in tests. */
    @Override
    public ContinuousDistribution makeDistribution() {
        return new NormalDistributionImpl(2.1, 1.4);
    }   
    
    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R 
        return new double[] {-2.226325d, -1.156887d, -0.6439496d, -0.2027951d, 0.3058278d, 
                6.426325d, 5.356887d, 4.84395d, 4.402795d, 3.894172d};
    }
    
    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                0.990d, 0.975d, 0.950d, 0.900d}; 
    }
    
    // --------------------- Override tolerance  --------------
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTolerance(1E-6);
    }

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMath280(@InRange(minDouble = 0, maxDouble = 1, isFixed = true) double tol,
                            @InRange(minDouble = 0, maxDouble = 5, isFixed = true) double mean,
                            @InRange(minDouble = 0, maxDouble = 5, isFixed = true) double std)
            throws MathException {
        // [Parameters]
        // Randomizing all parameters of NormalDistributionImpl
        try {
            setUp();
            NormalDistribution normal = new NormalDistributionImpl(mean, std);
            final double ret1 = normal.inverseCumulativeProbability(tol);
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] ret (which is the return value of the method under testing)
            Log.logOutIf(true, () -> new Double[]{ret1});
        } catch (Exception e){
           Log.ignoreOut();
        }
    }

}
