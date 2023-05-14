package org.apache.commons.math3.distribution;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assume.*;

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
public class JQF_HypergeometricDistributionTest extends IntegerDistributionAbstractTest {
  
  //-------------- Implementations for abstract methods -----------------------

  /** Creates the default discrete distribution instance to use in tests. */
  @Override
  public IntegerDistribution makeDistribution() {
    return new HypergeometricDistribution(10, 5, 5);
  }

  /** Creates the default probability density test input values */
  @Override
  public int[] makeDensityTestPoints() {
    return new int[] {-1, 0, 1, 2, 3, 4, 5, 10};
  }

  /** Creates the default probability density test expected values */
  @Override
  public double[] makeDensityTestValues() {
    return new double[] {0d, 0.003968d, 0.099206d, 0.396825d, 0.396825d,
                         0.099206d, 0.003968d, 0d};
  }

  /** Creates the default cumulative probability density test input values */
  @Override
  public int[] makeCumulativeTestPoints() {
    return makeDensityTestPoints();
  }

  /** Creates the default cumulative probability density test expected values */
  @Override
  public double[] makeCumulativeTestValues() {
    return new double[] {0d, .003968d, .103175d, .50000d, .896825d, .996032d,
                         1.00000d, 1d};
  }

  /** Creates the default inverse cumulative probability test input values */
  @Override
  public double[] makeInverseCumulativeTestPoints() {
    return new double[] {0d, 0.001d, 0.010d, 0.025d, 0.050d, 0.100d, 0.999d,
                         0.990d, 0.975d, 0.950d, 0.900d, 1d};
  }

  /** Creates the default inverse cumulative probability density test expected values */
  @Override
  public int[] makeInverseCumulativeTestValues() {
    return new int[] {0, 0, 1, 1, 1, 1, 5, 4, 4, 4, 4, 5};
  }
  

  // The specified ranges of the parameters are only initial ones, and the actual ranges are 
  // adjusted during the fuzzing process, as described in the paper.  
  @Fuzz
  public void testMath1021(@InRange(minInt = 42976360, maxInt = 42976370) int sampleSize,
                           @InRange(minInt = 1, maxInt = 100) int n,
                           @InRange(minInt = 43130563, maxInt = 43130573) int popSize,
                           @InRange(minDouble = 0, maxDouble = 1, isFixed = true) double p) {
    assumeTrue(sampleSize <= popSize);
    // [Parameters]
    // Randomizing all the parameters of HypergeometricDistribution
    HypergeometricDistribution dist = new HypergeometricDistribution(popSize, sampleSize, n);

    // the original test stmt: int sample = dist.sample();
    // the body of sample(): inverseCumulativeProbability(random.nextDouble());
    int actual = dist.inverseCumulativeProbability(p);
    // [Preservation condition] We use this condition according to the explanation given in this link: https://issues.apache.org/jira/browse/MATH-1021
    // In particular, the bug report mentions the following: 
    // "In the debugger, I traced it as far as an integer overflow in HypergeometricDistribution.getNumericalMean()"
    // Note that in a buggy version, method getNumericalMean() returns a negative number due to an overflow. 
    // 
    // [Output] sample (actual)
    Log.logOutIf(dist.getNumericalMean() > 0, () -> new Integer[] { actual });
  }

}
