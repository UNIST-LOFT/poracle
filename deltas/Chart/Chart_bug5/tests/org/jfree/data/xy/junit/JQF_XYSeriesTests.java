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
 * ------------------
 * XYSeriesTests.java
 * ------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 23-Dec-2003 : Version 1 (DG);
 * 15-Jan-2007 : Added tests for new toArray() method (DG);
 * 30-Jan-2007 : Fixed some code that won't compile with Java 1.4 (DG);
 * 31-Oct-2007 : New hashCode() test (DG);
 * 01-May-2008 : Added testAddOrUpdate3() (DG);
 * 24-Nov-2008 : Added testBug1955483() (DG);
 *
 */

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.*;

/**
 * Tests for the {@link XYSeries} class.
 */
@RunWith(JQF.class)
public class JQF_XYSeriesTests {
    public static Test suite() {
        return new TestSuite(XYSeriesTests.class);
    }
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testBug1955483(@InRange(minInt=-4, maxInt=6) int i1,
                               @InRange(minInt=-4, maxInt=6) int i2,
                               @InRange(minInt=-4, maxInt=6) int i3,
                               @InRange(minDouble=0, maxDouble=1) double ch1,
                               @InRange(minDouble=0, maxDouble=1) double ch2
    ) {
        try {
            // Randomizing all the parameters of addOrUpdate function and XYSeries constructor
            XYSeries series = new XYSeries("Series", ch1>0.5?true:false, ch2>0.5?true:false);
            series.addOrUpdate(i1, i2);
            series.addOrUpdate(i2, i1);
            series.addOrUpdate(i3, i2);
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] x and y values of each data element
            Log.logOutIf(true, () -> new Double[] { series.getX(0).doubleValue(), series.getY(0).doubleValue(), series.getX(1).doubleValue(), series.getY(1).doubleValue(),
                                                    series.getX(2).doubleValue(), series.getY(2).doubleValue()});
        }

        catch (Exception e) {
            Log.ignoreOut();
        }
    }

}
