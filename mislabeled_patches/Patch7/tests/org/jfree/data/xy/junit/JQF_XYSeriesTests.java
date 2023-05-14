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
 */

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;

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
public class JQF_XYSeriesTests extends TestCase {
    public static Test suite() {
        return new TestSuite(XYSeriesTests.class);
    }

    public JQF_XYSeriesTests(String name) {
        super(name);
    }

    public void testBug1955483() {
        try {
            XYSeries series = new XYSeries("Series", 0.8926526466663729>0.5?true:false, 0.9953236612191507>0.5?true:false);
            series.addOrUpdate(-1, -2);
            series.addOrUpdate(-2, -1);
            series.addOrUpdate(1, -2);
            Double[] results = new Double[] { series.getX(0).doubleValue(), series.getY(0).doubleValue(), series.getX(1).doubleValue(), series.getY(1).doubleValue(),
                        series.getX(2).doubleValue(), series.getY(2).doubleValue()};
            System.out.println(Arrays.toString(results));
        }

        catch (Exception e) {
        }
    }

}
