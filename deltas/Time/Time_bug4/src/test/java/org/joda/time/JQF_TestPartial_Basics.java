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
    private static final DateTimeZone NEW_YORK = DateTimeZone.forID("America/New_York");
    private static final DateTimeZone LOS_ANGELES = DateTimeZone.forID("America/Los_Angeles");
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
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.
    @Fuzz
    public void testWith3(@InRange(minInt=1999, maxInt=2009, isFixed = true) int year,
                          @InRange(minInt=1, maxInt=12, isFixed=true) int month,
                          @InRange(minInt=1, maxInt=30, isFixed=true) int day,
                          @InRange(minInt=1, maxInt=24, isFixed=true) int hour1,
                          @InRange(minInt=1, maxInt=12, isFixed=true) int hour2,
                          @InRange(minInt=1, maxInt=60, isFixed=true) int min1,
                          @InRange(minInt=1, maxInt=60, isFixed=true) int min2,
                          @InRange(minInt=1, maxInt=5, isFixed=true) int z,
                          @InRange(minInt=1, maxInt=3, isFixed=true) int chro,
                          @InRange(minDouble = 0, maxDouble = 1, isFixed =true) double ch
                          ) {
        DateTimeZone zone = null;
        // [Parameters]
        // Randomizing all parameters of with function and Partial constructor
        // We want to allow both of the following:
        // 1. Expected exception
        // 2. Unexpected exception
        // The original test allows only expected exception
        // Depending on the random value of ch, we can test both scenarios with random values.
        DateTimeFieldType time =  DateTimeFieldType.clockhourOfDay();;
        int value = hour2;
        switch(z) {
            case 2:
                time = DateTimeFieldType.year();
                value = year;
                zone = PARIS;
            case 1:
                time = DateTimeFieldType.clockhourOfDay();
                value = hour2;
                zone = TOKYO;
            case 3:
                time = DateTimeFieldType.minuteOfHour();
                value = min2;
                zone = LONDON;
            case 4:
                time = DateTimeFieldType.dayOfMonth();
                value = day;
                zone = NEW_YORK;
            case 5:
                time = DateTimeFieldType.monthOfYear();
                value = month;
                zone = LOS_ANGELES;
        }
        try {
            // Randomize chronology
            Partial test = null;
            switch (chro){
                case 1:
                    test = createHourMinPartial(hour1, min1, ISOChronology.getInstance(zone), ch);
                case 2:
                    test = createHourMinPartial(hour1, min1, CopticChronology.getInstance(zone), ch);
                case 3:
                    test = createHourMinPartial(hour1, min1, BuddhistChronology.getInstance(zone), ch);
            }
            Partial updated = test.with(time, value);
            // [Preservation condition] we check whether exception is expected or not
            // [Output] we dump out state of updated Partial's fields
            // if the preservation condition (ch > 0.5) holds
            if (ch > 0.5) dump(updated);
        } catch (IllegalArgumentException ex) {
            Log.logOutIf(ch <= 0.5, () -> new String[]{ex.toString()});
        }catch (Exception e) {
            Log.ignoreOut();
        }
    }

    private void dump(Partial test) {
        Log.logOutIf(true, () -> new String[]{java.util.Arrays.toString(test.getValues())});
        Log.logOutIf(true, () -> new String[]{test.getChronology().toString()});
        for( DateTimeField field: test.getFields()) {
            Log.logOutIf(true, () -> new String[]{String.valueOf(field.getMinimumValue(test,test.getValues())),
                                                      String.valueOf(field.getMaximumValue(test,test.getValues())),
                                                      field.getDurationField().toString(),
                                                      field.getType().toString(), field.toString()});
        }
    }

    private Partial createHourMinPartial(int hour, int min, Chronology chrono, double ch) {
        return new Partial(
            ch > 0.5? new DateTimeFieldType[] {DateTimeFieldType.clockhourOfDay(), DateTimeFieldType.minuteOfHour()}:
            new DateTimeFieldType[] {DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour()},
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
