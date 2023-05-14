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

package org.apache.commons.math.complex;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;

/**
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_ComplexTest {
    @Fuzz
    public void testAddNaN(@InRange(minDouble=-2, maxDouble=8) double a,
                           @InRange(minDouble=-1, maxDouble=9) double b,
                           @InRange(minDouble=-2, maxDouble=8) double c,
                           @InRange(minDouble=-2, maxDouble=8) double d,
                           @InRange(minInt = 0, maxInt = 1) int ch) {
        try {
            Complex x = new Complex(a, b);
            if (ch == 1) d = Double.NaN;
            Complex z = new Complex(c, d);
            Complex w = x.add(z);
            final double ret1 = w.getReal();
            final double ret2 = w.getImaginary();
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }


}
