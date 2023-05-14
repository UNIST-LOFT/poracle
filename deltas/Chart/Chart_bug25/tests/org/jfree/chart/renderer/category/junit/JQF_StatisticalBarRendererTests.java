/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * --------------------------------
 * JQF_StatisticalBarRendererTests.java
 * --------------------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: JQF_StatisticalBarRendererTests.java,v 1.1.2.1 2006/10/03 15:41:34 mungady Exp $
 *
 * Changes
 * -------
 * 25-Mar-2003 : Version 1 (DG);
 * 28-Aug-2007 : Added tests for bug 1779941 (DG);
 *
 */

package org.jfree.chart.renderer.category.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import java.awt.image.BufferedImage;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import com.thoughtworks.xstream.XStream;

/**
 * Tests for the {@link StatisticalBarRenderer} class.
 */
@RunWith(JQF.class)
public class JQF_StatisticalBarRendererTests {
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(StatisticalBarRendererTests.class);
    }
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testDrawWithNullMeanVertical(@InRange(minDouble = 0, maxDouble=10) double num1,
                                             @InRange(minDouble = 0, maxDouble=10) double num2,
                                             @InRange(minDouble = 0, maxDouble=10) double num3,
                                             @InRange(minDouble = 0, maxDouble=10) double num4,
                                             @InRange(minInt = 295, maxInt = 305) int d1,
                                             @InRange(minInt = 195, maxInt = 205) int d2
    ) {
        try {
            // [Parameters]
            // Randomizing parameters of createBufferedImage function
            XStream stream = new XStream();
            DefaultStatisticalCategoryDataset dataset
                    = new DefaultStatisticalCategoryDataset();
            dataset.add(num1, num2, "S1", "C1");
            dataset.add(num3, num4, "S1", "C2");
            CategoryPlot plot = new CategoryPlot(dataset,
                    new CategoryAxis("Category"), new NumberAxis("Value"),
                    new StatisticalBarRenderer());
            JFreeChart chart = new JFreeChart(plot);
            ChartRenderingInfo info = new ChartRenderingInfo();
            BufferedImage image = chart.createBufferedImage(d1,d2,info);
            Number mean1 = dataset.getMeanValue("S1", "C1");
            Number std1 = dataset.getStdDevValue( "S1", "C1");
            Number mean2 = dataset.getMeanValue("S1", "C2");
            Number std2 = dataset.getStdDevValue( "S1", "C2");
            Rectangle2D area = info.getChartArea();
            PlotRenderingInfo plotInfo = info.getPlotInfo();
            Rectangle2D dataArea = plotInfo.getDataArea();
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] mean, std of dataset and state of image
            Log.logOutIf(true,()-> new Double[]{
                    mean1.doubleValue(),std1.doubleValue(),
                    mean2.doubleValue(),std2.doubleValue()
            });Log.logOutIf(true,()-> new String[]{
                    area.toString(),
                    dataArea.toString(),
                    stream.toXML(image.getData())
            });

        }
        catch (Exception e) {
            Log.ignoreOut();
        };
    }

}
