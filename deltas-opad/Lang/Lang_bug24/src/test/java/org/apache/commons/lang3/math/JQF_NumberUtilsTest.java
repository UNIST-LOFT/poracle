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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.SystemUtils;

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
 * @author Apache Software Foundation
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Eric Pugh
 * @author Phil Steitz
 * @author Matthew Hawthorne
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_NumberUtilsTest {
// Log logger = new Log();
//
// public static class IntGenerator extends Generator<Integer> {
//   public IntGenerator() {
//     super(Integer.class);
//   }
//
//   public Integer generate(SourceOfRandomness random, GenerationStatus __ignore__) {
//     int m = random.nextInt(0,100);
//     return m;
//   }
// }

/**
 * Tests isNumber(String) and tests that createNumber(String) returns
 * a valid number iff isNumber(String) returns false.
 */
@Fuzz
public void testIsNumber(@InRange(minInt = 1, maxInt = 10) int num) {
        // LANG-664
        try{
        String val = "" + num;
        if(num < 5)
                val = val + "." + num;

        val = val + "L";

        boolean ret = NumberUtils.isNumber(val);

        // if (Log.runBuggyVersion)
        // {
        //         if(num < 5)
        //           ret = false;
        //         else
        //           ret = true;
        //         return;
        // }

        final String out2 = ""+ret;
        if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
        else Log.ignoreOut();
        } catch (Exception e){
                Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
        //assertFalse("isNumber(String) LANG-664 failed", NumberUtils.isNumber(val));
}

}
