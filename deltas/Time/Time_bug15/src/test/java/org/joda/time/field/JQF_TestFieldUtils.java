/*
 *  Copyright 2001-2005 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.field;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;
/**
 *
 *
 * @author Brian S O'Neill
 */
@RunWith(JQF.class)
public class JQF_TestFieldUtils {
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
    public static class LongGenerator extends Generator<Long> {
        public LongGenerator() {
            super(Long.class);
        }

        public Long generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            int m = random.nextInt(1, 5);
            long[] arr = {-1, 0, 1, Long.MIN_VALUE, Long.MAX_VALUE, random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE)};
            return arr[m];
        }
    }
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestFieldUtils.class);
    }

    //-----------------------------------------------------------------------
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.
    @Fuzz
    public void testSafeMultiplyLongInt(@From(LongGenerator.class) long i,
                                        @From(IntGenerator.class) int j) {

        boolean ArithmeticExceptionExpected = ((i == Long.MIN_VALUE && j == -1) 
                                              || (i == Long.MIN_VALUE && j == 100)
                                              || (i == Long.MIN_VALUE && j == Integer.MAX_VALUE)
                                              || (i == Long.MAX_VALUE && j == Integer.MIN_VALUE));
        try {
            // [Parameters]
            // Randomizing all parameters of safeMultiply function
            // We define IntGenerator and LongGenerator in a way that the constants used in the original test (MIN_VALUE, MAX_VALUE, 1, 0, and -1) 
            // can be generated with a reasonably high probability. 

            final long act = FieldUtils.safeMultiply(i, j);
            // [Preservation condition] The original test code specifies cases where ArithmeticExceptionExpected is expected.
            // We expect behavioral preservation in the rest of the cases.
            // 
            // [Output] act (which is the return value of method under testing)
            Log.logOutIf(!ArithmeticExceptionExpected, () -> new Long[] { act });
        } catch (ArithmeticException e){
            Log.logOutIf(ArithmeticExceptionExpected, () -> new String[] { e.toString() });
        } catch (Exception e){
            Log.ignoreOut();
        }
    }
}
