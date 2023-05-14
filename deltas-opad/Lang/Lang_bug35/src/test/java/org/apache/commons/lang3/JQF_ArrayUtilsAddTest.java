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


    @Fuzz
    public void testLANG571(@InRange(minInt = 1, maxInt = 10, isFixed = true) int num1,
                            @InRange(minInt = 1, maxInt = 10, isFixed = true) int num2){
        String[] stringArray = num1 > 5 ? null : new String[] { ""+num1 };

        // String[] stringArray= null;

        // if(!(num > 5))
        //     stringArray = new String[] {""};

        String aString = num2 > 7 ? null : ""+num2 ;
        // int outval1 = 5;

        Log.infoLog("test lang 35 num1: " + Arrays.toString(stringArray));
        Log.infoLog("test lang 35 num2: " + aString);

        String[] out1 = new String[]{};
        try {
            // @SuppressWarnings("unused")
            out1 = ArrayUtils.add(stringArray, aString);
            // if (Log.runBuggyVersion)
			// 	outval = -1;
			// else
			// 	outval = -10;
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }
}
