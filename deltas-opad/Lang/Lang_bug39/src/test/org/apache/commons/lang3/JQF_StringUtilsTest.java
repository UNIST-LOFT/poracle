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

/**
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
// Log logger = new Log();
//
// public static class IntGenerator extends Generator<Integer> {
//   public IntGenerator() {
//     super(Integer.class);
//   }
//
//   public Integer generate(SourceOfRandomness random, GenerationStatus __ignore__) {
//     int m = random.nextInt(123, 132);
//     return m;
//   }
// }
/*
static final String WHITESPACE;
static final String NON_WHITESPACE;
static final String TRIMMABLE;
static final String NON_TRIMMABLE;
static {
        String ws = "";
        String nws = "";
        String tr = "";
        String ntr = "";
        for (int i = 0; i < Character.MAX_VALUE; i++) {
                if (Character.isWhitespace((char) i)) {
                        ws += String.valueOf((char) i);
                        if (i > 32) {
                                ntr += String.valueOf((char) i);
                        }
                } else if (i < 40) {
                        nws += String.valueOf((char) i);
                }
        }
        for (int i = 0; i <= 32; i++) {
                tr += String.valueOf((char) i);
        }
        WHITESPACE = ws;
        NON_WHITESPACE = nws;
        TRIMMABLE = tr;
        NON_TRIMMABLE = ntr;
}

private static final String[] ARRAY_LIST = { "foo", "bar", "baz" };
private static final String[] EMPTY_ARRAY_LIST = {};
private static final String[] NULL_ARRAY_LIST = {null};
private static final String[] MIXED_ARRAY_LIST = {null, "", "foo"};
private static final Object[] MIXED_TYPE_LIST = {new String("foo"), new Long(2)};

private static final String SEPARATOR = ",";
private static final char SEPARATOR_CHAR = ';';

private static final String TEXT_LIST = "foo,bar,baz";
private static final String TEXT_LIST_CHAR = "foo;bar;baz";
private static final String TEXT_LIST_NOSEP = "foobarbaz";

private static final String FOO_UNCAP = "foo";
private static final String FOO_CAP = "Foo";

private static final String SENTENCE_UNCAP = "foo bar baz";
private static final String SENTENCE_CAP = "Foo Bar Baz";

public static void main(String[] args) {
        TestRunner.run(suite());
}

public static Test suite() {
        TestSuite suite = new TestSuite(StringUtilsTest.class);
        suite.setName("StringUtilsTest Tests");
        return suite;
}
*/
/**
 * Test method for 'StringUtils.replaceEach(String, String[], String[])'
 */
@Fuzz
public void testReplace_StringStringArrayStringArray(
@InRange(minInt = 0, maxInt = 6,  isFixed=true) int idx1,
@InRange(minInt = 0, maxInt = 6,  isFixed=true) int idx2
) {
        String instr = "abacc2143cabaaa";
        String outstr1 = "";
        String outstr2 = "";

        String[] search = {"a", "4", "b", "a", "d", "2", "a"};
        String[] replace = {"k", null, "l", null, "m", null, "n"};

        Log.infoLog("Search: " + search[idx1]);
        Log.infoLog("Replace: " + replace[idx2]);

        boolean exception = false;

        String temp = instr.replaceAll("a", "cccccccccc");
        if( !( search[idx1] == null || replace[idx2] == null ) )
                outstr2 = temp.replaceAll(search[idx1], replace[idx2]);
        else
                outstr2 = temp;

        try {
          outstr1 = StringUtils.replaceEachRepeatedly(instr, new String[] {"a", search[idx1]}, new String[] {"cccccccccc", replace[idx2]});
          outstr1 = StringUtils.replaceEach(instr, new String[] {"a", search[idx1]}, new String[] {"cccccccccc", replace[idx2]});
                if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
                else Log.ignoreOut();
        } catch (Exception e){
                Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
        // try {
        //         // {
        //                 // String instr = Integer.toString(input);
        //                 // outstr2 = instr.replaceAll("1", "33333333");
        //                 outstr1 = StringUtils.replaceEach(instr, new String[] {"1", "2"}, new String[] {"33333333", replacement});
        //                 // final String out2 = outstr;
        //                 // Log.logOutIf(true, () -> new String[]{ out2 });
        //         // } else
        //         // {
        //         //         String instr = Integer.toString(input);
        //         //         String outstr = StringUtils.replaceEach(instr, new String[] {"1", "2"}, new String[] {"33333333", "2"});
        //         //         final String out2 = outstr;
        //         //         Log.logOutIf(true, () -> new String[]{ out2 });
        //         // }
        //         // outval = Integer.parseInt(outstr);
        //         // final int out2 = outval;
        //         // Log.logOutIf(true, () -> new Integer[]{ out2 });
        // } catch (Exception e) {
        //         // if ( !Log.runBuggyVersion ) outval = -1;
        //         // else {
        //         //         try {
        //         //                 outstr = instr.replaceAll("21", "33333333");
        //         //                 outval = Integer.parseInt(outstr);
        //         //         } catch (Exception e1) {
        //         //                 outval = -1;
        //         //         }
        //         // }
        //         // Log.ignoreOut();
        //         // String instr = Integer.toString(input);
        //         // String outstr = instr.replaceAll("1", "33333333");
        //         // final String out2 = outstr;
        //         // Log.logOutIf(true, () -> new String[]{ out2 });
        //         // Log.logOutIf(true, () -> new String[]{ "Exception" });
        //         outstr1 = null;
        // }
        // logger.logOut(outval);
        //assertEquals(StringUtils.replaceEach("aba", new String[]{"a", "b"}, new String[]{"c", null}),"cbc");
}
}
