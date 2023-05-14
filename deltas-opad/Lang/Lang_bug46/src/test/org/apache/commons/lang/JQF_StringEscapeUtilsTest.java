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

import java.io.IOException;
import java.io.StringWriter;
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
 * Unit tests for {@link StringEscapeUtils}.
 *
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_StringEscapeUtilsTest {
        private final static String FOO = "foo";

        /**
         * https://issues.apache.org/jira/browse/LANG-421
         */
        @Fuzz
        public void testEscapeJavaWithSlash(@InRange(minInt = 41, maxInt = 51) int ch) {
         try{
                String instr = ""+(char)ch;
                String outstr = StringEscapeUtils.escapeJava(instr);
                final int out = outstr.length();
                final int out2 = instr.length();
                if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
                else Log.ignoreOut();
        } catch (Exception e){
                Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
        }
}
