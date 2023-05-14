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
package org.apache.commons.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Unit tests {@link org.apache.commons.lang.BooleanUtils}.
 *
 * @author Stephen Colebourne
 * @author Matthew Hawthorne
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_BooleanUtilsTest {

    //-----------------------------------------------------------------------
     // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void test_toBoolean_String(@InRange(minInt = 97, maxInt = 122) int ch1,
                                      @InRange(minInt = 97, maxInt = 122) int ch2,
                                      @InRange(minInt = 97, maxInt = 122) int ch3) {
        // [Parameters]
        // Randomizing three consecutive characters (lowercase alphabet)
        // The original test case uses many different strings.
        String instr = "" + (char) ch1 + (char) ch2 + (char) ch3;

        boolean ret = true;
        int outval = 0;
        try {
            ret = BooleanUtils.toBoolean(instr);
            outval = ret ? 1 : 0;
            final int out2 = outval;
            // [Preservation condition] The result of BooleanUtils.toBoolean() should be equals to the reference method (i.e., Boolean.valueOf()).
            // [Output] 1 if true or 0 otherwise.
            Log.logOutIf((ret == Boolean.valueOf(instr)), () -> new Integer[]{out2});
        } catch (Exception e) {
            Log.ignoreOut();
        }


    }
}
