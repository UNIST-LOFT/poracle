/*
 *  Copyright 2001-2011 Stephen Colebourne
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
package org.joda.time.format;

import java.io.CharArrayWriter;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;

// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import com.thoughtworks.xstream.XStream;

/**
 * This class is a Junit unit test for DateTime Formating.
 *
 * @author Stephen Colebourne
 */

@RunWith(JQF.class)
public class JQF_TestDateTimeFormatter {


    private static final DateTimeZone UTC = DateTimeZone.UTC;
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");
    private static final DateTimeZone NEWYORK = DateTimeZone.forID("America/New_York");
    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();
    private static final Chronology ISO_PARIS = ISOChronology.getInstance(PARIS);
    private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(PARIS);

    long y2002days = 365 + 365 + 366 + 365 + 365 + 365 + 366 + 365 + 365 + 365 +
            366 + 365 + 365 + 365 + 366 + 365 + 365 + 365 + 366 + 365 +
            365 + 365 + 366 + 365 + 365 + 365 + 366 + 365 + 365 + 365 +
            366 + 365;
    // 2002-06-09
    private long TEST_TIME_NOW =
            (y2002days + 31L + 28L + 31L + 30L + 31L + 9L -1L) * DateTimeConstants.MILLIS_PER_DAY;

    private DateTimeZone originalDateTimeZone = null;
    private TimeZone originalTimeZone = null;
    private Locale originalLocale = null;
    private DateTimeFormatter f = null;
    private DateTimeFormatter g = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestDateTimeFormatter.class);
    }


    protected void setUp() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(TEST_TIME_NOW);
        originalDateTimeZone = DateTimeZone.getDefault();
        originalTimeZone = TimeZone.getDefault();
        originalLocale = Locale.getDefault();
        DateTimeZone.setDefault(LONDON);
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Locale.setDefault(Locale.UK);
        f = new DateTimeFormatterBuilder()
                .appendDayOfWeekShortText()
                .appendLiteral(' ')
                .append(ISODateTimeFormat.dateTimeNoMillis())
                .toFormatter();
        g = ISODateTimeFormat.dateTimeNoMillis();
    }

    protected void tearDown() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
        DateTimeZone.setDefault(originalDateTimeZone);
        TimeZone.setDefault(originalTimeZone);
        Locale.setDefault(originalLocale);
        originalDateTimeZone = null;
        originalTimeZone = null;
        originalLocale = null;
        f = null;
        g = null;
    }
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.    
    @Fuzz
    public void testParseInto_monthOnly_baseStartYear(@InRange(minInt=1999, maxInt=2009) int year,
                                                      @InRange(minInt=1, maxInt=12, isFixed=true) int month,
                                                      @InRange(minInt=1, maxInt=12, isFixed=true) int month2,
                                                      @InRange(minInt=1, maxInt=31, isFixed=true) int day,
                                                      @InRange(minInt=1, maxInt=12, isFixed=true) int hour,
                                                      @InRange(minInt=1, maxInt=60, isFixed=true) int minute,
                                                      @InRange(minInt=1, maxInt=60, isFixed=true) int second,
                                                      @InRange(minInt=1, maxInt=1000, isFixed=true) int msecond,
                                                      @InRange(minInt=1, maxInt=5, isFixed=true) int z) {
        // [Parameters]
        // Randomize all parameters of MutableDateTime
        try {
            setUp();
                        
            // randomly choose DateTimeZone
            DateTimeZone d = null;
            switch(z) {
                case 1:
                    d = UTC;
                    break;
                case 2:
                    d = TOKYO;
                    break;
                case 3:
                    d = PARIS;
                    break;
                case 4:
                    d = LONDON;
                    break;
                case 5:
                    d = NEWYORK;
                    break;
            }
            
            DateTimeFormatter f = DateTimeFormat.forPattern("M").withLocale(Locale.UK);
            MutableDateTime result = new MutableDateTime(year, month, day, hour, minute, second, msecond, d);
            String month2Str = (new Integer(month2)).toString();
            int ret1 = f.parseInto(result, month2Str, 0);
            MutableDateTime expected = new MutableDateTime(year, month2, day, hour, minute, second, msecond, d);
            // [Preservation condition]
            // The original assertion conditions are generalized.
            //
            // [Output] the output of the methods under testing, parseInto.
            // Note that parseInto makes a side effect on result.
            Log.logOutIf(month2Str.length() == ret1, () -> new Integer[] { ret1 });
            Log.logOutIf(result.equals(expected), () -> new String[] { result.toString() });
            
            tearDown();
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }

}
