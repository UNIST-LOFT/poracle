package org.jfree.chart.block.junit;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.jfree.chart.block.*;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.Size2D;
import org.jfree.data.Range;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static junit.framework.Assert.assertEquals;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import com.thoughtworks.xstream.XStream;

@RunWith(JQF.class)
public class JQF_BorderArrangementTests {

    public static Test suite() {
        return new TestSuite(BorderArrangementTests.class);
    }

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testSizingWithWidthConstraint(
                                              @InRange(minDouble = 7.3, maxDouble = 47.3) double d1,
                                              @InRange(minDouble = 40.6, maxDouble = 80.6) double d2,
                                              @InRange(minDouble = 100, maxDouble = 110) double d3,
                                              @InRange(minDouble = 5, maxDouble = 15) double d4,
                                              @InRange(minDouble = 15, maxDouble = 25) double d5,
                                              @InRange(minDouble = 0, maxDouble = 10) double d6,
                                              @InRange(minDouble = 0, maxDouble = 10) double d7,
                                              @InRange(minDouble = 0, maxDouble = 10) double d8
                                                ) {
        // [Parameters]
        // Randomizing all the parameters of Range and EmptyBlock constructors
        RectangleConstraint constraint = new RectangleConstraint(
                d3, new Range(d3, d3), LengthConstraintType.FIXED,
                d8, new Range(d8, d8), LengthConstraintType.NONE
        );
        BlockContainer container = new BlockContainer(new BorderArrangement());
        BufferedImage image = new BufferedImage(
                200, 100, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2 = image.createGraphics();
        XStream stream = new XStream();
        try {
            // TBLRC
            // 00110 - left and right items
            container.add(new EmptyBlock(d4, d5), RectangleEdge.RIGHT);
            container.add(new EmptyBlock(d1, d2), RectangleEdge.LEFT);
            Size2D size = container.arrange(g2, constraint);
            double ret1 = size.width;
            double ret2 = size.height;
            String s1 = stream.toXML(container.getArrangement());
            // TBLRC
            // 00111 - left, right and center items
            container.clear();
            container.add(new EmptyBlock(d4, d5));
            container.add(new EmptyBlock(d1, d2), RectangleEdge.LEFT);
            container.add(new EmptyBlock(d6, d7), RectangleEdge.RIGHT);
            size = container.arrange(g2, constraint);
            double ret3 = size.width;
            double ret4 = size.height;
            String s2 = stream.toXML(container.getArrangement());

            // TBLRC
            // 10110 - top, left and right
            container.clear();
            container.add(new EmptyBlock(d6, d7), RectangleEdge.RIGHT);
            container.add(new EmptyBlock(d4, d5), RectangleEdge.TOP);
            container.add(new EmptyBlock(d1, d2), RectangleEdge.LEFT);
            size = container.arrange(g2, constraint);
            String s3 = stream.toXML(container.getArrangement());
            double ret5 = size.width;
            double ret6 = size.height;

            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] width, height (which is the return value of the method under testing) and state of container arrangement
            Log.logOutIf(true, () -> new Double[]{ret1,ret2,ret3,ret4, ret5, ret6});
            Log.logOutIf(true, () -> new String[]{s1,s2,s3});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
}
