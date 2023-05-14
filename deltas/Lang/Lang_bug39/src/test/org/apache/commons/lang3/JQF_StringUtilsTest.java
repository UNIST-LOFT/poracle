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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

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

import java.io.PrintWriter;
import java.io.StringWriter;

/*
 * Unit tests {@link org.apache.commons.lang3.StringUtils}.
 *
 * @author Apache Software Foundation
 * @author Daniel L. Rall
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author <a href="mailto:fredrik@westermarck.com>Fredrik Westermarck</a>
 * @author Holger Krauth
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author Phil Steitz
 * @author Gary D. Gregory
 * @author Scott Johnson
 * @author Al Chou
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_StringUtilsTest {
    /*
     * Test method for 'StringUtils.replaceEach(String, String[], String[])'
     */
	
     // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testReplace_StringStringArrayStringArray(
            @InRange(minInt = 0, maxInt = 6, isFixed = true) int idx1,
            @InRange(minInt = 0, maxInt = 6, isFixed = true) int idx2
    ) {
        // [Parameters]
        // Randomizing characters to be replaced by and to replace.
        // The original test case replaces "a" by null, and "a" by "c" and "b" by null.
        String instr = "abacc2143cabaaa";
        String outstr1 = "";
        String outstr2 = "";
        String[] search = {"a", "4", "b", "a", "d", "2", "c"};
        String[] replace = {"k", null, "l", "c", "a", "", null};
        boolean exception = false;
	boolean ex2 = false;

        try {
            // [Note] Due to the design issue of Commons-Lang, the following method should be
            // invoked to explore some other paths. In fact, both StringUtils.replaceEachRepeatedly() and
            // StringUtils.replaceEach() are just proxies of replaceEach(final String text, final String[] searchList, final String[] replacementList, final boolean repeat, final int timeToLive).
            // The issue is that replaceEach() is a private method.
            // "Exception" should be recorded as the replaceEach() method throws exceptions for some conditions as an excepted behavior.
            outstr2 = StringUtils.replaceEachRepeatedly(instr, new String[]{"a", search[idx1]}, new String[]{"cccccccccc", replace[idx2]});
        } catch (Exception e) {
            exception = true;
        }
        try {
          outstr1 = StringUtils.replaceEach(instr, new String[] {"a", search[idx1]}, new String[] {"cccccccccc", replace[idx2]});
        } 
	catch (Exception e)
        {
          ex2 = true;
        }

	String ref = "";
        String temp = instr.replaceAll("a", "cccccccccc");
        if( !( search[idx1] == null || replace[idx2] == null ) )
                ref = temp.replaceAll(search[idx1], replace[idx2]);
        else
                ref = temp;

        final String out1 = outstr1;
        final String out2 = outstr2;
        final String out3 = exception ? "true" : "false";
	final String out4 = ref;
        // final String out1 = exception ? "Exception" : outstr1;


	Log.infoLog("out1: " + out1);
        Log.infoLog("out2: " + out2);
        Log.infoLog("Ref : " + ref);
	Log.infoLog("out3: " + out3);

        // [Preservation condition] Always.
        // [Output] if ex2 == true, use the reference output (out4). Otherwise, use the return value of StringUtils.replaceEach() and StringUtils.replaceEachRepeatedly().

	if(ex2)
		Log.logOutIf(true, () -> new String[]{ out4, out4 });
	else
		Log.logOutIf(true, () -> new String[]{ out1, out2 });


        // [Original test case]
        //assertEquals(StringUtils.replaceEach("aba", new String[]{"a", "b"}, new String[]{"c", null}),"cbc");
    }
}
