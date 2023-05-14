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
package org.apache.commons.lang.math;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.lang.SystemUtils;

//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Unit tests {@link org.apache.commons.lang.math.NumberUtils}.
 *
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Eric Pugh
 * @author Phil Steitz
 * @author Stephen Colebourne
 * @author Matthew Hawthorne
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_NumberUtilsTest /*extends TestCase*/ {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(NumberUtilsTest.class);
        suite.setName("NumberUtils Tests");
        return suite;
    }
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testLang300(@InRange(minInt = 43, maxInt = 59, isFixed = true) int num1,
                            @InRange(minInt = 43, maxInt = 59, isFixed = true) int num2
                            // @InRange(minLong = -20, maxLong = 20) long num3
    ) {
        // [Parameters]
        // Randomizing two consecutive characters for a Long type value.
        // The original test case uses "-1l", "01l", and "1l".
        String instr = "" + ((char) num1) + ((char) num2) + "l";
        Log.infoLog("lang bug 58: " + instr);
        // Reference
        String refResult = "";

        try {
            long out1 = Long.valueOf(instr);
            refResult = "" + out1;
        } catch (Exception e) {
            refResult = "Exception";
        }


        String testResult = "";
        try {
            long out1 = NumberUtils.createNumber(instr).longValue();
            testResult = "" + out1;

        } catch (Exception e) {
            testResult = "Exception";
        }

        final String r1 = refResult;
        final String r2 = testResult;
        // [Preservation condition] The result of NumberUtils.createNumber().longValue() should be equals to the reference method, Long.valueOf().
        // [Output] The results of the target and reference methods.
        Log.logOutIf(r2.equals(r1), () -> new String[]{r1, r2});
    }
}
