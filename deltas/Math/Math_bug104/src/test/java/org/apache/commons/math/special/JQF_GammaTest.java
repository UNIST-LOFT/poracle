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
package org.apache.commons.math.special;

import static org.junit.Assert.fail;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;

import org.junit.runner.RunWith;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;


/** @version $Revision$ $Date$ */
@RunWith(JQF.class)
public class JQF_GammaTest {
  // The specified ranges of the parameters are only initial ones, and the actual ranges are 
  // adjusted during the fuzzing process, as described in the paper. 
  @Fuzz
  public void testRegularizedGammaPositivePositive(
        @InRange(minDouble = 0, maxDouble = 6, isFixed=true) double d1,
        @InRange(minDouble = 0, maxDouble = 6, isFixed=true) double d2,
        @InRange(minDouble = 0, maxDouble = 1, isFixed=true) double ch) {
    try {
      // [Parameters]
      // Randomizing all parameters of the function
      // To our understanding, if delta is given as 10e-15, we will get correct output
      final double ret1 = ch>0.5?Gamma.regularizedGammaP(d1, d2,10e-15,Integer.MAX_VALUE):Gamma.regularizedGammaP(d1, d2);
      final double ret2 =  ch>0.5?Gamma.regularizedGammaQ(d1, d2,10e-15,Integer.MAX_VALUE):Gamma.regularizedGammaQ(d1, d2);
      // [Preservation condition] we check whether the output will be wrong or not
      // [Output] ret1, ret2 (which is the return values of the method under testing)
      Log.logOutIf(ch>0.5, () -> new Double[]{ret1,ret2});
      } catch (Exception ex) {
      Log.ignoreOut();
    }
  }

}
