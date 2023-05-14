/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.util;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.TestUtils;

// PORACLE: Additional packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.runner.RunWith;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;


/**
 * Test cases for the MathUtils class.
 * @version $Revision$ $Date: 2007-08-16 15:36:33 -0500 (Thu, 16 Aug
 *          2007) $
 */
@RunWith(JQF.class)
public final class JQF_MathUtilsTest {
    public static Test suite() {
        TestSuite suite = new TestSuite(MathUtilsTest.class);
        suite.setName("MathUtils Tests");
        return suite;
    }
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testFactorial(@InRange(minInt = 0, maxInt = 30, isFixed= true) int i) {
        try {

            // [Parameters]
            // Randomizing parameter of factorial function
            final long v1 = MathUtils.factorial(i);
            final double v2 = MathUtils.factorialDouble(i);
            final double v3 = MathUtils.factorialLog(i);
            // [Preservation condition]
            // We reuse the same assertion condition as a preservation condition.
            //
            // [Output] result values (which is the return values of the method under testing)
            boolean cond = factorial(i) != v1 || Math.abs((double) factorial(i) - v2) > Double.MIN_VALUE || Math.abs(Math.log((double) factorial(i)) - v3) > Double.MIN_VALUE;
            Log.logOutIf(!cond, () -> new Double[]{(double) v1, v2, v3});
        }catch (Exception e){
            Log.ignoreOut();
        }
        }
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

}
