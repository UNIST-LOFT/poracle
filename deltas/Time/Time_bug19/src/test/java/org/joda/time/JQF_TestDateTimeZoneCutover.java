/*
 *  Copyright 2001-2007 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.chrono.GregorianChronology;
import org.joda.time.tz.DateTimeZoneBuilder;

// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * This class is a JUnit test for DateTimeZone.
 *
 * @author Stephen Colebourne
 */
@RunWith(JQF.class)
public class JQF_TestDateTimeZoneCutover {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestDateTimeZoneCutover.class);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.    
    @Fuzz
    public void testDateTimeCreation_london(@InRange(minInt=1999, maxInt=2009, isFixed=true) int year,
                                            @InRange(minInt=1, maxInt=12, isFixed=true) int month,
                                            @InRange(minInt=1, maxInt=31, isFixed=true) int day,
                                            @InRange(minInt=1, maxInt=12, isFixed=true) int hour,
                                            @InRange(minInt=1, maxInt=60, isFixed=true) int min,
                                            @InRange(minInt=1, maxInt=5, isFixed=true) int z) {
        try {
            // [Parameters]
            // Randomizing all the parameters of DateTime constructor
            setUp();
            DateTimeZone zone = null;
            switch(z) {
                case 1:
                    zone = DateTimeZone.forID("America/New_York");
                case 2:
                    zone = DateTimeZone.forID("Europe/London");;
                case 3:
                    zone = DateTimeZone.forID("Europe/Paris");
                case 4:
                    zone = DateTimeZone.forID("Europe/Athens");
                case 5:
                    zone = DateTimeZone.forID("America/Los_Angeles");
            }
        DateTime base = new DateTime(year, month, day, hour, min, zone);
        DateTime actual = base.plusHours(1);
        // Based on understanding, this is the expected result from the function
        DateTime expected = new DateTime(year, month, day, hour+1, min, zone);
        // [Preservation condition] if the actual is same as expected, we expect correct output
        // [Output] actual (which is the return value of the method under testing)
        Log.logOutIf(actual.equals(expected),() -> new String[] { actual.toString()});
        Log.logOutIf(actual.equals(expected),() -> new Long[] { actual.getMillis()});
        tearDown();
        } catch (Exception e){
            Log.ignoreOut();
        }
    }
}
