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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.lang.SystemUtils;

public class JQF_NumberUtilsTest extends TestCase{
    public JQF_NumberUtilsTest(String name) {
        super(name);
    }
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(NumberUtilsTest.class);
        suite.setName("NumberUtils Tests");
        return suite;
    }


    public void testLang300() {
        String instr = "" + ((char) 56) + ((char) 46) + "l";
        // Reference
        String refResult = "";

        try {
            
            
            Long out1 = Long.valueOf(instr);
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
        String[] results = new String[] {r1, r2};
        System.out.println(Arrays.toString(results));
    }
}
