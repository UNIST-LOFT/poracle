package org.apache.commons.math3.util;

import java.util.Arrays;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.Well1024a;
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
public class JQF_MathArraysTest {
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testLinearCombinationWithSingleElementArray(@InRange(minDouble=-5.23456789, maxDouble=6.23456789) double v1,
                                                            @InRange(minDouble=98765427.1, maxDouble=98765437.1) double v2) {
        try {
            // [Parameters]
            // Randomizing all the parameters of linearCombination
            double[] a = new double[]{v1};
            double[] b = new double[]{v2};
            double result = MathArrays.linearCombination(a, b);
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] result
            Log.logOutIf(true, () -> new Double[]{ result });
        } catch(Throwable e) {
            Log.ignoreOut();
        }
    }

}
