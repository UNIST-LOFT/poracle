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
package org.apache.commons.math3.genetics;


import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.junit.Assert;
import org.junit.Test;

//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_ElitisticListPopulationTest {
    
    // @Test(expected = OutOfRangeException.class)
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testConstructorTooLow(@InRange(minDouble = -5, maxDouble = 5) double rate) {
        // [Parameters]
        // We test the following cases with random input:
        // Case 1. It is expected that OutOfRangeException is thrown.
        // Case 2. It is not expected that an exception is thrown 
        // We randomly choose a case based on a random value of rate.
        try {
            ElitisticListPopulation el = new ElitisticListPopulation(100, rate);
            // [Preservation condition] we check whether exception is expected or not.
            // Note that the API document of ElitisticListPopulation has the following:
            // "OutOfRangeException if the elitism rate is outside the [0, 1] range"
            //
            // [Output] elitism rate of ElitisticListPopulation
            Log.logOutIf(
                rate >= 0 && rate <= 1, () -> new String[] { String.valueOf(el.getElitismRate()) });
        } catch (OutOfRangeException e1){
            Log.logOutIf(
                !(rate >= 0 && rate <= 1), () -> new String[] { e1.toString() });
        } catch (Exception e2){
            Log.logOutIf(true, () -> new String[] { e2.toString() });
        }
    }

}
