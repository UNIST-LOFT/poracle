/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------------
 * TimePeriodValueTests.java
 * -------------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 30-Jul-2003 : Version 1 (DG);
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 * 07-Apr-2008 : Added new tests for min/max-start/middle/end
 *               index updates (DG);
 *
 */

package org.jfree.data.time.junit;

import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.MonthConstants;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.Year;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/** A collection of test cases for the {@link TimePeriodValues} class. */
@RunWith(JQF.class)
public class JQF_TimePeriodValuesTests {
  /**
   * Series A.
   */
  private TimePeriodValues seriesA;

  /**
   * Series B.
   */
  private TimePeriodValues seriesB;

  /**
   * Series C.
   */
  private TimePeriodValues seriesC;

  /**
   * Returns the tests as a test suite.
   *
   * @return The test suite.
   */
  public static Test suite() {
    return new TestSuite(TimePeriodValuesTests.class);
  }

  /**
   * Common test setup.
   */
  protected void setUp() {

    this.seriesA = new TimePeriodValues("Series A");
    try {
      this.seriesA.add(new Year(2000), new Integer(102000));
      this.seriesA.add(new Year(2001), new Integer(102001));
      this.seriesA.add(new Year(2002), new Integer(102002));
      this.seriesA.add(new Year(2003), new Integer(102003));
      this.seriesA.add(new Year(2004), new Integer(102004));
      this.seriesA.add(new Year(2005), new Integer(102005));
    } catch (SeriesException e) {
      System.err.println("Problem creating series.");
    }

    this.seriesB = new TimePeriodValues("Series B");
    try {
      this.seriesB.add(new Year(2006), new Integer(202006));
      this.seriesB.add(new Year(2007), new Integer(202007));
      this.seriesB.add(new Year(2008), new Integer(202008));
    } catch (SeriesException e) {
      System.err.println("Problem creating series.");
    }

    this.seriesC = new TimePeriodValues("Series C");
    try {
      this.seriesC.add(new Year(1999), new Integer(301999));
      this.seriesC.add(new Year(2000), new Integer(302000));
      this.seriesC.add(new Year(2002), new Integer(302002));
    } catch (SeriesException e) {
      System.err.println("Problem creating series.");
    }
  }

  /**
   * Some tests for the getMaxMiddleIndex() method.
   */
   // The specified ranges of the parameters are only initial ones, and the actual ranges are 
   // adjusted during the fuzzing process, as described in the paper. 
  @Fuzz
  public void testGetMaxMiddleIndex(
          @InRange(minLong = 195, maxLong = 205, isFixed = true) long l1,
          @InRange(minLong = 205, maxLong = 215, isFixed = true) long l2,
          @InRange(minDouble = 0, maxDouble = 5) double d1,
          @InRange(minDouble = 0, maxDouble = 5) double d2,
          @InRange(minDouble = 0, maxDouble = 5) double d3,
          @InRange(minDouble = 0, maxDouble = 5) double d4,
          @InRange(minDouble = 0, maxDouble = 1) double ch
  ) {
    setUp();
    // [Parameters]
    // Randomizing all parameters of add function
    TimePeriodValues s = new TimePeriodValues("Test");
    // If different time period is added, we expect wrong behavior
    if(ch>0.5) {
      s.add(new SimpleTimePeriod(l1, l2), d1);
      s.add(new SimpleTimePeriod(l1, l2), d2);
      s.add(new SimpleTimePeriod(l1, l2), d3);
      s.add(new SimpleTimePeriod(l1, l2), d4);
    }else{
      s.add(new SimpleTimePeriod((long)(l1+d1), (long)(l2+d1)), d1);
      s.add(new SimpleTimePeriod((long)(l1+d2), (long)(l2+d2)), d2);
      s.add(new SimpleTimePeriod((long)(l1+d3), (long)(l2+d3)), d3);
      s.add(new SimpleTimePeriod((long)(l1+d4), (long)(l2+d4)), d4);
    }
    try {
      int ret = s.getMaxMiddleIndex();
      // [Preservation condition] we check whether the program will produce wrong output
      // [Output] ret (which is the return value of method under testing)
      Log.logOutIf(ch>0.5, () -> new Integer[]{ret});
    } catch (Exception e) {
      Log.ignoreOut();
    }
  }
}

