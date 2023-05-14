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
    @Fuzz
    public void testSafeMultiplyLongInt(@From(IntGenerator.class) int i,
                                        @From(LongGenerator.class) long j) {

        try{
            final long act = FieldUtils.safeMultiply(j, i);
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }


    }
}

