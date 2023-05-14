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

// public static class IntGenerator extends Generator<Integer> {
//   public IntGenerator() {
//     super(Integer.class);
//   }
//
//   public Integer generate(SourceOfRandomness random, GenerationStatus __ignore__) {
//     int m = random.nextInt(-20, 20);
//     return m;
//   }
// }
// public static class DoubleGenerator extends Generator<Double> {
//   public DoubleGenerator() {
//     super(Double.class);
//   }
//
//   public Double generate(SourceOfRandomness random, GenerationStatus __ignore__) {
//     return random.nextDouble(0, 1);
//   }
// }

public static void main(String[] args) {
        TestRunner.run(suite());
}

public static Test suite() {
        TestSuite suite = new TestSuite(NumberUtilsTest.class);
        suite.setName("NumberUtils Tests");
        return suite;
}

@Fuzz
public void testLang300(@InRange(minInt = 43, maxInt = 59, isFixed = true) int num1,
                        @InRange(minInt = 43, maxInt = 59, isFixed = true) int num2
                        // @InRange(minLong = -20, maxLong = 20) long num3
                        ) {

        String instr = ""+((char)num1)+((char)num2)+"l";
        Log.infoLog("lang bug 58: " + instr);
        // Reference
        String refResult = "";
        String testResult = "";
        try {
                long out1 = NumberUtils.createNumber(instr).longValue();
                testResult = "" + out1;

                if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
                else Log.ignoreOut();
        } catch (Exception e){
                Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
}
}
