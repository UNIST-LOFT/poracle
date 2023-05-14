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

package org.apache.commons.lang3;

import java.util.Arrays;

import junit.framework.TestCase;

//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Tests ArrayUtils add methods.
 *
 * @author Gary D. Gregory
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_ArrayUtilsAddTest extends TestCase {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testLANG571(@InRange(minInt = 1, maxInt = 10, isFixed = true) int num1,
                            @InRange(minInt = 1, maxInt = 10, isFixed = true) int num2) {

        // [Parameters]
        // Randomizing one string array and one string.
        // The original test case uses null array and null string.
        String[] stringArray = num1 > 5 ? null : new String[]{"" + num1};

        String aString = num2 > 7 ? null : "" + num2;

        Log.infoLog("test lang 35 num1: " + Arrays.toString(stringArray));
        Log.infoLog("test lang 35 num2: " + aString);

        String[] out1 = new String[]{};
        try {
            // @SuppressWarnings("unused")
            out1 = ArrayUtils.add(stringArray, aString);
        } catch (IllegalArgumentException iae) {
            //expected
            // outval1 = -1;
            out1 = new String[]{"IAE"};
        } catch (Exception e) {
            out1 = new String[]{"Exception"};
        }

        Log.infoLog("test lang 35 num1 out: " + Arrays.toString(out1));

        final String out = Arrays.toString(out1);
        // [Preservation condition] If both are null, it should be IAE. If at least one is not null, the result of ArrayUtils.add() should be a concatenated array of two inputs.
        // [Output] The result of ArrayUtils.add(), i.e., a concatenated array.
        Log.logOutIf( (stringArray == null && aString == null && "IAE".equals(out1[0])) || !(stringArray == null && aString == null),
                () -> new String[]{out});
        // Log.logOutIf(!(stringArray == null && aString == null), () -> new String[]{out});
    }
}
