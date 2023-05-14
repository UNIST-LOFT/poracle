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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.TestUtils;
//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

@RunWith(JQF.class)
public final class JQF_MathUtilsTest extends TestCase {
    public static class IntGenerator extends Generator<Integer> {
        public IntGenerator() {
            super(Integer.class);
        }

        public Integer generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            int m = random.nextInt(1, 5);
            int[] arr = {-1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE)};
            return arr[m];
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MathUtilsTest.class);
        suite.setName("MathUtils Tests");
        return suite;
    }

    /**
     * cached binomial coefficients
     */
    private static List<Map<Integer, Long>> binomialCache = new ArrayList<Map<Integer, Long>>();

    /**
     * Exact (caching) recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) throws ArithmeticException {
        if (binomialCache.size() > n) {
            Long cachedResult = binomialCache.get(n).get(new Integer(k));
            if (cachedResult != null) {
                return cachedResult.longValue();
            }
        }
        long result = -1;
        if ((n == k) || (k == 0)) {
            result = 1;
        } else if ((k == 1) || (k == n - 1)) {
            result = n;
        } else {
            // Reduce stack depth for larger values of n
            if (k < n - 100) {
                binomialCoefficient(n - 100, k);
            }
            if (k > 100) {
                binomialCoefficient(n - 100, k - 100);
            }
            result = MathUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
                    binomialCoefficient(n - 1, k));
        }
        if (result == -1) {
            throw new ArithmeticException(
                    "error computing binomial coefficient");
        }
        for (int i = binomialCache.size(); i < n + 1; i++) {
            binomialCache.add(new HashMap<Integer, Long>());
        }
        binomialCache.get(n).put(new Integer(k), new Long(result));
        return result;
    }

    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }


    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.     
    @Fuzz
    public void testGcd(@From(IntGenerator.class) int i,
                        @From(IntGenerator.class) int j) {

        try {
            // [Parameters]
            // Randomizing all parameters of gcd function
            final long act = MathUtils.gcd(i, j);
            // [Preservation condition] The original buggy code behaves incorrectly when the following holds:
            // (i == Integer.MIN_VALUE && j == 0) || (j == Integer.MIN_VALUE && i == 0)
            // In the other cases, we expect the behavior to be preserved. 
            //
            // [Output] act (which is the return value of the method under testing)
            Log.logOutIf(!((i == Integer.MIN_VALUE && j == 0) || (j == Integer.MIN_VALUE && i == 0)), () -> new Long[]{act});
        } catch (ArithmeticException expected) {
            Log.logOutIf((i == Integer.MIN_VALUE && j == 0) || (j == Integer.MIN_VALUE && i == 0), () -> new String[] { "expected exception" });
        } catch (Exception e) {
            Log.ignoreOut();
        }

    }

}
