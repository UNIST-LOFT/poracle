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
package org.apache.commons.math.stat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.apache.commons.math.TestUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;


/**
 * Test cases for the {@link Frequency} class.
 *
 * @version $Revision$ $Date$
 */
@RunWith(JQF.class)
public final class JQF_FrequencyTest  {
    private Frequency f = null;

    public void setUp() {  
        f = new Frequency();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FrequencyTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    // Check what happens when non-Comparable objects are added
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testAddNonComparable(@InRange(minInt = -4, maxInt = 6) int a,
                                    @InRange(minDouble = 0, maxDouble = 1) double ch){
                try {
                    // [Parameters]
                    // 1. An integer value to add
                    // 2. A random value used to randomly decide whether to add an object value
                    setUp();
                    Object c = new Object();
                    if(ch >= 0.5) f.addValue(c);
                    f.clear();
                    f.addValue(a);
                    // [Preservation condition] If an object is not added, behavior is expected to be preserved.
                    // [Output] the count of variable occurred
                    Log.logOutIf(ch < 0.5, () -> new Long[] { f.getCount(c), f.getCount(a) });
                } catch (IllegalArgumentException e){
                    Log.logOutIf(ch >= 0.5, () -> new String[]{e.getClass().toString()});
                } catch (Exception e){
                    Log.ignoreOut();
                }
    }

}

