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
package org.apache.commons.math.analysis.solvers;

import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import junit.framework.TestCase;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/** @version $Revision$ $Date$ */
@RunWith(JQF.class)
public final class JQF_BisectionSolverTest extends TestCase {

  @Fuzz
  public void testMath369(@InRange(minDouble=-2, maxDouble = 8) double a1,
                          @InRange(minDouble=-1.8, maxDouble = 8.2) double a2,
                          @InRange(minDouble=-1.9, maxDouble = 8.1) double a3) throws Exception {
    double[] sorted = new double[]{a1,a2,a3};
    java.util.Arrays.sort(sorted);
    UnivariateRealFunction f = new SinFunction();
    try {
      UnivariateRealSolver solver = new BisectionSolver();
      final double ret = solver.solve(f, sorted[0], sorted[2], sorted[1]);
      if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
      else Log.ignoreOut();
    } catch (Exception e){
      Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
    }

  }
}
