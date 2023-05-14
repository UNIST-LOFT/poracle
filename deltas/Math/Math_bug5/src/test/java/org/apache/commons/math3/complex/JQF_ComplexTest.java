package org.apache.commons.math3.complex;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_ComplexTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testReciprocalZero(@InRange(minDouble=-10, maxDouble=10) double a1,
                                   @InRange(minDouble=-10, maxDouble=10) double a2
                                   ) {
            // [Parameters]
            // Randomizing all the parameters of Complex
            Complex temp = new Complex(a1,a2);
            Complex test = temp.reciprocal();
            // [Preservation condition] In the original test, "Complex.ZERO.reciprocal()" returns Complex(Double.NaN, Double.NaN)
            // and subsequently causes a test failure. We expect behavior to be preserved in other cases.
            //
            // [Output] Real and imaginary part of complex number
            Log.logOutIf(!test.equals(new Complex(Double.NaN, Double.NaN)), () -> new Double[]{test.getReal(),test.getImaginary()});
    }
}
