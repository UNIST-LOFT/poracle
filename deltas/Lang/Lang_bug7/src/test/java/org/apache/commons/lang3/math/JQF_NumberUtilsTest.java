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
package org.apache.commons.lang3.math;

import static org.apache.commons.lang3.JavaVersion.JAVA_1_3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Unit tests {@link org.apache.commons.lang3.math.NumberUtils}.
 *
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_NumberUtilsTest {
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testCreateNumber(@InRange(minInt = 43, maxInt = 53) int ch1) {
        // [Parameters]
        // Randomizing the prefix character.
        // The original test case puts two consecutive negative signs.
        String instr = "" + (char) ch1 + (char) ch1 + "1.5E-700F";
        String outval = "";
        Number num = null;
        Double ref = null;

        try {
            num = NumberUtils.createNumber(instr);
            outval = num == null ? "null" : num.toString();

            ref = Double.parseDouble(instr);

            // [Original test case]
            // NumberUtils.createNumber("-1.1E-700F");
            // assertEquals(BigDecimal.class,bigNum.getClass());
            // assertNotNull(bigNum);
            // NumberUtils.createNumber("--1.1E-700F");
            // Here is a failure state.
            //fail("Expected NumberFormatException");
        } catch (NumberFormatException nfe) {
            // [Original test case]
            // expected
            Log.ignoreOut();
        } catch (Exception e) {
            Log.ignoreOut();
        }

        final String out2 = outval;
        // [Preservation condition] We use the same condition: outval should not be null.
        // [Output] The result of NumberUtils.createNumber(instr).
        Log.logOutIf(ref != null, () -> new String[]{out2});
    }
}
