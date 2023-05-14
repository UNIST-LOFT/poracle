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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
 * Unit tests for {@link LocaleUtils}.
 *
 * @author Chris Hyzer
 * @author Stephen Colebourne
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_LocaleUtilsTest extends TestCase {

    // private static final Locale LOCALE_EN = new Locale("en", "");
    // private static final Locale LOCALE_EN_US = new Locale("en", "US");
    // private static final Locale LOCALE_EN_US_ZZZZ = new Locale("en", "US", "ZZZZ");
    // private static final Locale LOCALE_FR = new Locale("fr", "");
    // private static final Locale LOCALE_FR_CA = new Locale("fr", "CA");
    // private static final Locale LOCALE_QQ = new Locale("qq", "");
    // private static final Locale LOCALE_QQ_ZZ = new Locale("qq", "ZZ");

    private static Locale[] testLocales = new Locale[]{
            new Locale("en", ""),
            new Locale("en", "US"),
            new Locale("en", "US", "ZZZZ"),
            new Locale("fr", ""),
            new Locale("fr", "CA"),
            new Locale("qq", ""),
            new Locale("qq", "ZZ"),
            new Locale("kr", "AC"),
            new Locale("yy", "aa"),
            new Locale("bb", "Cc"),
            new Locale("tt", "ll")
    };

    // private static final int lenLocaleTest = testLocales.length-1;

    // public void setUp() throws Exception {
    //     super.setUp();
    //
    //     // Testing #LANG-304. Must be called before availableLocaleSet is called.
    //     LocaleUtils.isAvailableLocale(Locale.getDefault());
    // }

    //-----------------------------------------------------------------------

    /**
     * Test availableLocaleSet() method.
     */
     // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testIsAvailableLocale(@InRange(minInt = 0, maxInt = 10) int num) {

        // [Parameters]
        // Randomizing a locale instance (by selecting a random element in an array).
        // The original test case uses several different locale instances (also predefined).
        Set set = LocaleUtils.availableLocaleSet();

        boolean det1 = set.contains(testLocales[num]);
        boolean det2 = LocaleUtils.isAvailableLocale(testLocales[num]);

        final boolean out1 = det1;
        final boolean out2 = det2;
        // [Preservation condition] The same condition with the original; the results from both methods, LocaleUtils.availableLocaleSet().contains() and LocaleUtils.isAvailableLocale() should be same.
        // [Output] The results from LocaleUtils.availableLocaleSet().contains() and LocaleUtils.isAvailableLocale().
        Log.logOutIf(det1 == det2, () -> new Boolean[]{out1, out2});


        // [Original test case]
        // assertEquals(set.contains(LOCALE_EN), LocaleUtils.isAvailableLocale(LOCALE_EN));
        // assertEquals(set.contains(LOCALE_EN_US), LocaleUtils.isAvailableLocale(LOCALE_EN_US));
        // assertEquals(set.contains(LOCALE_EN_US_ZZZZ), LocaleUtils.isAvailableLocale(LOCALE_EN_US_ZZZZ));
        // assertEquals(set.contains(LOCALE_FR), LocaleUtils.isAvailableLocale(LOCALE_FR));
        // assertEquals(set.contains(LOCALE_FR_CA), LocaleUtils.isAvailableLocale(LOCALE_FR_CA));
        // assertEquals(set.contains(LOCALE_QQ), LocaleUtils.isAvailableLocale(LOCALE_QQ));
        // assertEquals(set.contains(LOCALE_QQ_ZZ), LocaleUtils.isAvailableLocale(LOCALE_QQ_ZZ));
    }

}
