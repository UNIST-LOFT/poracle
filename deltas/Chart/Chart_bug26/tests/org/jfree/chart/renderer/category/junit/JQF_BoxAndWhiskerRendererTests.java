
package org.jfree.chart.renderer.category.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

//Additional packages
// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import com.thoughtworks.xstream.XStream;


import java.awt.image.BufferedImage;

@RunWith(JQF.class)
public class JQF_BoxAndWhiskerRendererTests {

    public static Test suite() {
        return new TestSuite(BoxAndWhiskerRendererTests.class);
    }

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testDrawWithNullInfo(@InRange(minDouble =-4, maxDouble=6) double x1,
                                     @InRange(minDouble =-3, maxDouble=7) double x2,
                                     @InRange(minDouble =-5, maxDouble=5) double x3,
                                     @InRange(minDouble =-1, maxDouble=9) double x4,
                                     @InRange(minDouble =-4.5, maxDouble=5.5) double x5,
                                     @InRange(minDouble =-0.5, maxDouble=9.5) double y1,
                                     @InRange(minDouble =-5.5, maxDouble=4.5) double y2,
                                     @InRange(minDouble =0.5, maxDouble=10.5) double y3,
                                     @InRange(minInt =195, maxInt=205) int y5,
                                     @InRange(minInt =295, maxInt=305) int y6
                                    ) {

        try {
            // [Parameters]
            // Randomizing all parameters of BoxAndWhiskerItem constructor and createBufferedImage function
            XStream stream = new XStream();
            DefaultBoxAndWhiskerCategoryDataset dataset
                = new DefaultBoxAndWhiskerCategoryDataset();
            dataset.add(new BoxAndWhiskerItem(new Double(x1), new Double(x2),
                    new Double(x3), new Double(x4), new Double(x5),
                    new Double(y1), new Double(y2), new Double(y3),
                    null), "S1", "C1");
            CategoryPlot plot = new CategoryPlot(dataset, 
                    new CategoryAxis("Category"), new NumberAxis("Value"), 
                    new BoxAndWhiskerRenderer());
            JFreeChart chart = new JFreeChart(plot);
            ChartRenderingInfo info = new ChartRenderingInfo();
            BufferedImage image = chart.createBufferedImage(y6,y5,info);
            String area = info.getChartArea().toString();
            PlotRenderingInfo plotInfo = info.getPlotInfo();
            String dataArea = plotInfo.getDataArea().toString();
            String plotArea = plotInfo.getPlotArea().toString();
            String xmlData = stream.toXML(image.getData());
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] information about drawn area and state of image
            Log.logOutIf(true, () -> new String[] { area, dataArea, plotArea, xmlData });
        }
        catch (Exception e) {
            Log.ignoreOut();
        }
    }
    

}
