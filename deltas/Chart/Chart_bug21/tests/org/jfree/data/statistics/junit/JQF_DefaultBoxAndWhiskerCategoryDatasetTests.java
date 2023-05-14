
package org.jfree.data.statistics.junit;

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

import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

// poracle
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;


/**
 * Tests for the {@link DefaultBoxAndWhiskerCategoryDataset} class.
 */
@RunWith(JQF.class)
public class JQF_DefaultBoxAndWhiskerCategoryDatasetTests {
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultBoxAndWhiskerCategoryDatasetTests.class);
    }


    private static final double EPSILON = 0.0000000001;

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testGetRangeBounds(@InRange(minDouble = -2.5, maxDouble = 7.5,isFixed = true) double x1,
                                   @InRange(minDouble = -1.5, maxDouble = 8.5,isFixed = true) double x2,
                                   @InRange(minDouble = -0.5, maxDouble = 9.5,isFixed = true) double x3,
                                   @InRange(minDouble = 0.5, maxDouble = 10.5,isFixed = true) double x4,
                                   @InRange(minDouble = 1.5, maxDouble = 11.5,isFixed = true) double x5,
                                   @InRange(minDouble = 2.5, maxDouble = 12.5,isFixed = true) double x6,
                                   @InRange(minDouble = 3.5, maxDouble = 13.5,isFixed = true) double x7,
                                   @InRange(minDouble = 4.5, maxDouble = 14.5,isFixed = true) double x8,
                                   @InRange(minDouble = 3.5, maxDouble = 6.5,isFixed = true) double y1,
                                   @InRange(minDouble = -2.5, maxDouble = 7.5,isFixed = true) double y2,
                                   @InRange(minDouble = -1.5, maxDouble = 8.5,isFixed = true) double y3,
                                   @InRange(minDouble = -0.5, maxDouble = 9.5,isFixed = true) double y4,
                                   @InRange(minDouble = 0.5, maxDouble = 10.5,isFixed = true) double y5,
                                   @InRange(minDouble = 1.5, maxDouble = 11.5, isFixed = true) double y6,
                                   @InRange(minDouble = 3.4, maxDouble = 13.6, isFixed = true) double y7,
                                   @InRange(minDouble = 4.4, maxDouble = 14.6, isFixed = true) double y8,
                                   @InRange(minDouble = -20, maxDouble = 20, isFixed = true) double a,
                                   @InRange(minDouble = 0, maxDouble = 1, isFixed = true) double ch
                                    ) {

        try {
            DefaultBoxAndWhiskerCategoryDataset d1 = new DefaultBoxAndWhiskerCategoryDataset();
            // [Parameters]
            // Randomizing all the parameters of BoxAndWhiskerItem constructor
            d1.add(new BoxAndWhiskerItem(x1, x2, x3, x4, x5, x6, x7, x8, new ArrayList()), "R1", "C1");
            // we found that if item is added in different row then we expect wrong behavior
            if(ch>0.5)d1.add(new BoxAndWhiskerItem(y1, y2, y3, y4, y5, y6, y7, y8, new ArrayList()), "R1", "C1");
            else d1.add(new BoxAndWhiskerItem(y1, y2, y3, y4, y5, y6, y7, y8, new ArrayList()), "R2", "C1");
            // [Preservation condition] we check whether wrong behavior branch is taken or not
            // [Output] range bounds (which is the return values of the method under testing) and result from equals function
            Log.logOutIf(ch>0.5, () -> new Double[]{d1.getRangeBounds(false).getLowerBound(), d1.getRangeBounds(false).getUpperBound(),
                    d1.getRangeBounds(true).getLowerBound(), d1.getRangeBounds(true).getUpperBound()});
            Log.logOutIf(ch>0.5, () -> new Integer[]{compare(new Range(a,d1.getRangeBounds(false).getUpperBound()),d1.getRangeBounds(false)),
                                                    compare(new Range(a,d1.getRangeBounds(true).getUpperBound()), d1.getRangeBounds(true))});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
    private int compare(Range a, Range b){
        return a.equals(b)?1:0;
    }
}
