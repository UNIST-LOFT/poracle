/*
 *  Copyright 2001-2006 Stephen Colebourne
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GregorianChronology;
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
 * This class is a Junit unit test for LocalDate.
 *
 * @author Stephen Colebourne
 */
@RunWith(JQF.class)
public class JQF_TestLocalDate_Constructors /*extends TestCase*/ {

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();
    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();
    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();
    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(PARIS);
    
    private long TEST_TIME_NOW =
            (31L + 28L + 31L + 30L + 31L + 9L -1L) * DateTimeConstants.MILLIS_PER_DAY;
            
    private long TEST_TIME1 =
        (31L + 28L + 31L + 6L -1L) * DateTimeConstants.MILLIS_PER_DAY
        + 12L * DateTimeConstants.MILLIS_PER_HOUR
        + 24L * DateTimeConstants.MILLIS_PER_MINUTE;
    private long TEST_TIME1_ROUNDED =
        (31L + 28L + 31L + 6L -1L) * DateTimeConstants.MILLIS_PER_DAY;
    private long TEST_TIME2 =
        (365L + 31L + 28L + 31L + 30L + 7L -1L) * DateTimeConstants.MILLIS_PER_DAY
        + 14L * DateTimeConstants.MILLIS_PER_HOUR
        + 28L * DateTimeConstants.MILLIS_PER_MINUTE;

    private DateTimeZone zone = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestLocalDate_Constructors.class);
    }

    protected void setUp() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(TEST_TIME_NOW);
        zone = DateTimeZone.getDefault();
        DateTimeZone.setDefault(LONDON);
    }

    protected void tearDown() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
        DateTimeZone.setDefault(zone);
        zone = null;
    }
     // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.
    @Fuzz
    public void testFactory_fromCalendarFields_beforeYearZero1(@InRange(minInt=1, maxInt=2000) int year,
                                                               @InRange(minInt=1, maxInt=12, isFixed=true) int month,
                                                               @InRange(minInt=1, maxInt=31, isFixed=true) int day,
                                                               @InRange(minInt=1, maxInt=12, isFixed=true) int hour,
                                                               @InRange(minInt=1, maxInt=60, isFixed=true) int minute,
                                                               @InRange(minInt=1, maxInt=60, isFixed=true) int second,
                                                               @InRange(minInt=1, maxInt=999, isFixed=true) int msecond) throws Exception {

        setUp();
        // [Parameters]
        // Randomizing all parameters of GregorianCalendar constructor
        GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute, second);
        LocalDateTime ldt = LocalDateTime.fromCalendarFields(cal);

        cal.set(Calendar.ERA, GregorianCalendar.BC);
        cal.set(Calendar.MILLISECOND, msecond);       

        LocalDateTime act = LocalDateTime.fromCalendarFields(cal);
        // [Preservation condition] The original assertion condition is generalized.
        // [Output] act (which is the return value of method under testing)
        LocalDateTime expected = new LocalDateTime(ldt.getYear() - 1, ldt.getMonthOfYear(), ldt.getDayOfMonth(), 
                                                   ldt.getHourOfDay(), ldt.getMinuteOfHour(), ldt.getSecondOfMinute(),
                                                   msecond);
        Log.logOutIf(act.equals(expected), () -> new String[] { act.toString() });
        tearDown();
    }

}
