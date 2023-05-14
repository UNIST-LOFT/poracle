

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.MonthConstants;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Year;

// poracle
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;



@RunWith(JQF.class)
public class JQF_TimeSeriesTests implements SeriesChangeListener {

    /** A time series. */
    private TimeSeries seriesA;

    /** A time series. */
    private TimeSeries seriesB;

    /** A time series. */
    private TimeSeries seriesC;

    /** A flag that indicates whether or not a change event was fired. */
    private boolean gotSeriesChangeEvent = false;
    
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TimeSeriesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    protected void setUp() {

        this.seriesA = new TimeSeries("Series A", Year.class);
        try {
            this.seriesA.add(new Year(2000), new Integer(102000));
            this.seriesA.add(new Year(2001), new Integer(102001));
            this.seriesA.add(new Year(2002), new Integer(102002));
            this.seriesA.add(new Year(2003), new Integer(102003));
            this.seriesA.add(new Year(2004), new Integer(102004));
            this.seriesA.add(new Year(2005), new Integer(102005));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

        this.seriesB = new TimeSeries("Series B", Year.class);
        try {
            this.seriesB.add(new Year(2006), new Integer(202006));
            this.seriesB.add(new Year(2007), new Integer(202007));
            this.seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

        this.seriesC = new TimeSeries("Series C", Year.class);
        try {
            this.seriesC.add(new Year(1999), new Integer(301999));
            this.seriesC.add(new Year(2000), new Integer(302000));
            this.seriesC.add(new Year(2002), new Integer(302002));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

    }
    

    public void seriesChanged(SeriesChangeEvent event) {
        this.gotSeriesChangeEvent = true;   
    }

    @Fuzz
    public void testBug1832432(@InRange(minInt=1200, maxInt=2009, isFixed = true) int year1,
                               @InRange(minInt=1, maxInt=12, isFixed=true) int month1,
                               @InRange(minInt=1, maxInt=28, isFixed=true) int day1,
                               @InRange(minDouble=95, maxDouble=105, isFixed=true) int z1,
                               @InRange(minInt=1200, maxInt=2009, isFixed = true) int year2,
                               @InRange(minInt=1, maxInt=12, isFixed=true) int month2,
                               @InRange(minInt=1, maxInt=28, isFixed=true) int day2,
                               @InRange(minDouble=95, maxDouble=105, isFixed=true) int z2) {
        setUp();
        // [Parameters]
        // Randomizing all parameters of add function
        TimeSeries s1 = new TimeSeries("Series");
        TimeSeries s2 = null;
        s1.add(new Day(day1, month1, year1), z1);
        s1.add(new Day(day2, month2, year2), z2);

        try {
            s2 = (TimeSeries) s1.clone();
        }
        catch (Exception e) {
            Log.ignoreOut();
        }
        final int count = s2.getItemCount();
        // [Preservation condition] we use true for an unexpected exception case.
        // [Output] the number of elements in s2
        Log.logOutIf(true, () -> new Integer[] { count });
    }

}
