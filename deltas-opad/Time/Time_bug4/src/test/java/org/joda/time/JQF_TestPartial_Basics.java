/*
 *  Copyright 2001-2013 Stephen Colebourne
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Locale;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * This class is a Junit unit test for Partial.
 *
 * @author Stephen Colebourne
 */
@RunWith(JQF.class)
public class JQF_TestPartial_Basics  {

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");
    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(PARIS);
    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TOKYO);
    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();
    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();
    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(LONDON);
    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TOKYO);
    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();
    
    private long TEST_TIME_NOW =
            10L * DateTimeConstants.MILLIS_PER_HOUR
            + 20L * DateTimeConstants.MILLIS_PER_MINUTE
            + 30L * DateTimeConstants.MILLIS_PER_SECOND
            + 40L;
    private long TEST_TIME2 =
        1L * DateTimeConstants.MILLIS_PER_DAY
        + 5L * DateTimeConstants.MILLIS_PER_HOUR
        + 6L * DateTimeConstants.MILLIS_PER_MINUTE
        + 7L * DateTimeConstants.MILLIS_PER_SECOND
        + 8L;
        
    private DateTimeZone zone = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestPartial_Basics.class);
    }

    @Fuzz
    public void testWith3(@InRange(minInt=1999, maxInt=2009, isFixed = true) int year,
                          @InRange(minInt=1, maxInt=12, isFixed=true) int month,
                          @InRange(minInt=1, maxInt=30, isFixed=true) int day,
                          @InRange(minInt=1, maxInt=24, isFixed=true) int hour1,
                          @InRange(minInt=1, maxInt=12, isFixed=true) int hour2,
                          @InRange(minInt=1, maxInt=60, isFixed=true) int min1,
                          @InRange(minInt=1, maxInt=60, isFixed=true) int min2,
                          @InRange(minInt=1, maxInt=5, isFixed=true) int z) {

        DateTimeFieldType time =  DateTimeFieldType.clockhourOfDay();;
        int value = hour2;
        switch(z) {
            case 2:
                time = DateTimeFieldType.year();
                value = year;
            case 1:
                time = DateTimeFieldType.clockhourOfDay();
                value = hour2;
            case 3:
                time = DateTimeFieldType.minuteOfHour();
                value = min2;
            case 4:
                time = DateTimeFieldType.dayOfMonth();
                value = day;
            case 5:
                time = DateTimeFieldType.monthOfYear();
                value = month;
        }
        try {
            Partial test = createHourMinPartial(hour1, min1, ISO_UTC);
            Partial updated = test.with(time, value);
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }

    private void dump(Partial test){
        Log.logOutIf(true, () -> new String[]{java.util.Arrays.toString(test.getValues())});
        for( DateTimeField field: test.getFields()) {
            Log.logOutIf(true, () -> new String[]{String.valueOf(field.getMinimumValue(test,test.getValues())), String.valueOf(field.getMaximumValue(test,test.getValues())), field.getDurationField().toString(), field.getType().toString(), field.toString()});
        }
    }
    private Partial createHourMinPartial() {
        return createHourMinPartial(ISO_UTC);
    }

    private Partial createHourMinPartial(Chronology chrono) {
        return createHourMinPartial(10, 20, chrono);
    }

    private Partial createHourMinPartial2(Chronology chrono) {
        return createHourMinPartial(15, 20, chrono);
    }

    private Partial createHourMinPartial(int hour, int min, Chronology chrono) {
        return new Partial(
            new DateTimeFieldType[] {DateTimeFieldType.clockhourOfDay(), DateTimeFieldType.minuteOfHour()},
            new int[] {hour, min},
            chrono);
    }

    private Partial createTODPartial(Chronology chrono) {
        return new Partial(
            new DateTimeFieldType[] {
                    DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(),
                    DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond()},
            new int[] {10, 20, 30, 40},
            chrono);
    }

    private void check(Partial test, int hour, int min) {
        assertEquals(test.toString(), hour, test.get(DateTimeFieldType.hourOfDay()));
        assertEquals(test.toString(), min, test.get(DateTimeFieldType.minuteOfHour()));
    }

    private boolean check2(Partial test, int hour, int min) {
        return hour == test.get(DateTimeFieldType.clockhourOfDay()) &&
                min == test.get(DateTimeFieldType.minuteOfHour());
    }
}
