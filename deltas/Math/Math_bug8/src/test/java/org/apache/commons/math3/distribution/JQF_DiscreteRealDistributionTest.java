package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;
//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_DiscreteRealDistributionTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testIssue942(@InRange(minInt=1, maxInt=10) int n) {
        List<Pair<Double,Double>> list = new ArrayList<Pair<Double, Double>>();
        // [Parameters]
        // Randomizing random double n that is added to the list
        list.add(new Pair<Double, Double>(new Double(n), new Double(n)));
        list.add(new Pair<Double, Double>(new Double(n), new Double(n)));
        final Object[] x = (new DiscreteDistribution<Double>(list)).sample(1);
        // [Preservation condition] we use true for an unexpected exception case.
        // [Output] result array x that will have one element according to the information given in this link: https://issues.apache.org/jira/browse/MATH-942 and original test
        Log.logOutIf(true, () -> new Object[]{ x[0] });
    }
}
