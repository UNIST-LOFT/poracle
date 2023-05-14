package org.apache.commons.math3.fraction;

// PORACLE: Additional packages
import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_FractionTest {

    @Fuzz
    public void testDigitLimitConstructor(@InRange(minDouble = 0.4, maxDouble = 0.6152) double value,
                                          @InRange(minInt = 9, maxInt = 9999) int maxDenominator)  {
        final Fraction fraction = new Fraction(0.5000000001, 10);
        Log.logOutIf(true, () -> new Double[] { fraction.doubleValue(),
                Double.valueOf(fraction.getNumerator()),
                Double.valueOf(fraction.getDenominator())});
    }
}
