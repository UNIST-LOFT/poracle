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
    @Fuzz
    public void testAddNonComparable(@InRange(minInt = -4, maxInt = 6) int a,
                                    @InRange(minInt = 0, maxInt = 1) int ch){
                try {
                    setUp();
                    Object c = new Object();
                    if(ch==1)
                        f.addValue(c);
                    f.clear();
                    if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
                    else Log.ignoreOut();
                } catch (Exception e){
                    Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
                }
    }

}

