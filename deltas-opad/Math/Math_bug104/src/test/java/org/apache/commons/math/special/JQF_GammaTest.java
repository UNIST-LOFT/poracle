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
  @Fuzz
  public void testRegularizedGammaPositivePositive(
        @InRange(minDouble = 0, maxDouble = 6, isFixed=true) double d1,
        @InRange(minDouble = 0, maxDouble = 6, isFixed=true) double d2) {
    try {
      final double ret1 = Gamma.regularizedGammaP(d1, d2,10e-15,Integer.MAX_VALUE);
      final double ret2 = Gamma.regularizedGammaQ(d1, d2,10e-15,Integer.MAX_VALUE);

      if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
      else Log.ignoreOut();
    } catch (Exception e){
      Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
    }
  }

}
