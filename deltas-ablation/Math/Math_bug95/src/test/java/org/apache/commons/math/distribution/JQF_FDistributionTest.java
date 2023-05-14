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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Test cases for FDistribution. Extends ContinuousDistributionAbstractTest. See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 *
 * @version $Revision$ $Date$
 */
@RunWith(JQF.class)
public class JQF_FDistributionTest  extends ContinuousDistributionAbstractTest {

  /** Creates the default continuous distribution instance to use in tests. */
  public ContinuousDistribution makeDistribution() {
    return new FDistributionImpl(5.0, 6.0);
  }

  /** Creates the default cumulative probability distribution test input values */
  public double[] makeCumulativeTestPoints() {
    // quantiles computed using R version 1.8.1 (linux version)
    return new double[] {
      0.03468084d,
      0.09370091d,
      0.1433137d,
      0.2020084d,
      0.2937283d,
      20.80266d,
      8.745895d,
      5.987565d,
      4.387374d,
      3.107512d
    };
  }

  /** Creates the default cumulative probability density test expected values */
  public double[] makeCumulativeTestValues() {
    return new double[] {
      0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d, 0.990d, 0.975d, 0.950d, 0.900d
    };
  }

  // --------------------- Override tolerance  --------------
  protected void setUp() throws Exception {
    super.setUp();
    setTolerance(4e-6);
  }

  // The specified ranges of the parameters are only initial ones, and the actual ranges are 
  // adjusted during the fuzzing process, as described in the paper.  
  @Fuzz
  public void testSmallDegreesOfFreedom(
      @InRange(minDouble = 0, maxDouble = 5) double d1,
      @InRange(minDouble = 0, maxDouble = 5) double d2,
      @InRange(minDouble = 0, maxDouble = 1, isFixed= true) double p,
      @InRange(minDouble = 0, maxDouble = 5) double den) throws Exception {
    org.apache.commons.math.distribution.FDistributionImpl fd =
            new org.apache.commons.math.distribution.FDistributionImpl(
                    d1, d2);
    try {
      setUp();
      // [Parameters]
      // Randomizing all parameters
      double p1 = fd.cumulativeProbability(p);
      final double ret2 = fd.inverseCumulativeProbability(p1);
      fd.setDenominatorDegreesOfFreedom(den);
      double p2 = fd.cumulativeProbability(p);
      final double ret4 = fd.inverseCumulativeProbability(p2);
      // [Preservation condition] we use true for an unexpected exception case.
      // [Output] p1, p2 (which is the return value of the method under testing)
      Log.logOutIf(true, () -> new Double[]{ret2,ret4});

      }catch (Exception e){
        Log.logOutIf(true, ()-> new String[] { e.toString() });
    }

  }
}
