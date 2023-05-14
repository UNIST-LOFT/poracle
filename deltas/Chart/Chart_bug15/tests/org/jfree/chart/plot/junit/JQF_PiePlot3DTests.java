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
 * -------------------
 * Pie3DPlotTests.java
 * -------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 * 22-Mar-2007 : Added testEquals() (DG);
 * 05-Oct-2007 : Modified testEquals() for new field (DG);
 * 19-Mar-2008 : Added test for null dataset (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.general.DefaultPieDataset;

// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Tests for the {@link PiePlot3D} class.
 */
@RunWith(JQF.class)
public class JQF_PiePlot3DTests {
    public static Test suite() {
        return new TestSuite(PiePlot3DTests.class);
    }

    /**
     * Draws a pie chart where the label generator returns null.
     */
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.     
    @Fuzz
    public void testDrawWithNullDataset(
                                        @InRange(minInt = 195, maxInt = 205) int ds1,
                                        @InRange(minInt = 95, maxInt = 105) int ds2
                                        ) {
        // [Parameters]
        // Randomizing the parameters of Buffered Image and Rectangle2D
        DefaultPieDataset data = new DefaultPieDataset();
        ChartRenderingInfo info = new ChartRenderingInfo();
        try {
            JFreeChart chart = ChartFactory.createPieChart3D("Test", data, true,
                    false, false);
            BufferedImage image = new BufferedImage(ds1 , ds2,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, ds1, ds2), null, info);

            Rectangle2D area = info.getChartArea();
            double areaWidth = area.getWidth();
            double areaHeight = area.getHeight();
            PlotRenderingInfo plotInfo = info.getPlotInfo();
            Rectangle2D dataArea = plotInfo.getDataArea();
            double dataAreaWidth = dataArea.getWidth();
            double dataAreaHeight = dataArea.getHeight();

            g2.dispose();
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] information about drawn area
            Log.logOutIf(true, ()->new Double[]{areaWidth, areaHeight, dataAreaWidth, dataAreaHeight});
        }
        catch (Exception e) {
            Log.ignoreOut();
        }
    }

}
