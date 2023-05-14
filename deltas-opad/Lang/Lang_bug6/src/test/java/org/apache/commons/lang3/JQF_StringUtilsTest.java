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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils}.
 *
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_StringUtilsTest {

    // static final String WHITESPACE;
    // static final String NON_WHITESPACE;
    // static final String TRIMMABLE;
    // static final String NON_TRIMMABLE;
    // static {
    //     String ws = "";
    //     String nws = "";
    //     String tr = "";
    //     String ntr = "";
    //     for (int i = 0; i < Character.MAX_VALUE; i++) {
    //         if (Character.isWhitespace((char) i)) {
    //             ws += String.valueOf((char) i);
    //             if (i > 32) {
    //                 ntr += String.valueOf((char) i);
    //             }
    //         } else if (i < 40) {
    //             nws += String.valueOf((char) i);
    //         }
    //     }
    //     for (int i = 0; i <= 32; i++) {
    //         tr += String.valueOf((char) i);
    //     }
    //     WHITESPACE = ws;
    //     NON_WHITESPACE = nws;
    //     TRIMMABLE = tr;
    //     NON_TRIMMABLE = ntr;
    // }
    //
    // private static final String[] ARRAY_LIST = { "foo", "bar", "baz" };
    // private static final String[] EMPTY_ARRAY_LIST = {};
    // private static final String[] NULL_ARRAY_LIST = {null};
    // private static final Object[] NULL_TO_STRING_LIST = {
    //     new Object(){
    //         @Override
    //         public String toString() {
    //             return null;
    //         }
    //     }
    // };
    // private static final String[] MIXED_ARRAY_LIST = {null, "", "foo"};
    // private static final Object[] MIXED_TYPE_LIST = {"foo", Long.valueOf(2L)};
    // private static final long[] LONG_PRIM_LIST = {1, 2};
    // private static final int[] INT_PRIM_LIST = {1, 2};
    // private static final byte[] BYTE_PRIM_LIST = {1, 2};
    // private static final short[] SHORT_PRIM_LIST = {1, 2};
    // private static final char[] CHAR_PRIM_LIST = {'1', '2'};
    // private static final float[] FLOAT_PRIM_LIST = {1, 2};
    // private static final double[] DOUBLE_PRIM_LIST = {1, 2};
    //
    // private static final String SEPARATOR = ",";
    // private static final char   SEPARATOR_CHAR = ';';
    //
    // private static final String TEXT_LIST = "foo,bar,baz";
    // private static final String TEXT_LIST_CHAR = "foo;bar;baz";
    // private static final String TEXT_LIST_NOSEP = "foobarbaz";
    //
    // private static final String FOO_UNCAP = "foo";
    // private static final String FOO_CAP = "Foo";
    //
    // private static final String SENTENCE_UNCAP = "foo bar baz";
    // private static final String SENTENCE_CAP = "Foo Bar Baz";

    @Fuzz
    public void testEscapeSurrogatePairs(@InRange(minInt = 55357, maxInt = 55400, isFixed=true) int ch1,
                                            @InRange(minInt = 56880, maxInt = 56900, isFixed=true) int ch2) throws Exception {

        String src = Character.toString((char)ch1) + Character.toString((char)ch2);
        Log.infoLog("test lang bug 6 " + String.format("%04X %04X", ch1, ch2));
        Log.infoLog("test lang bug 6 " + src);
        String dest = "";
        int outval = 0;
        try {
            dest = StringEscapeUtils.escapeCsv(src);
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }
}
