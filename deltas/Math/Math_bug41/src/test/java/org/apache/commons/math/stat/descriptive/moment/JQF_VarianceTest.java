/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive.moment;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.math.util.FastMath;

//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import java.util.stream.Stream;
/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_VarianceTest {
    protected Variance stat;

    protected double mean = 12.404545454545455d;
    protected double geoMean = 12.070589161633011d;

    protected double var = 10.00235930735931d;
    protected double std = FastMath.sqrt(var);
    protected double skew = 1.437423729196190d;
    protected double kurt = 2.377191264804700d;

    protected double min = 8.2d;
    protected double max = 21d;
    protected double median = 12d;
    protected double percentile5 = 8.29d;
    protected double percentile95 = 20.82d;

    protected double product = 628096400563833396009676.9200400128d;
    protected double sumLog = 54.7969806116451507d;
    protected double sumSq = 3595.250d;
    protected double sum = 272.90d;
    protected double secondMoment = 210.04954545454547d;
    protected double thirdMoment = 868.0906859504136;
    protected double fourthMoment = 9244.080993773481;


    protected double weightedMean = 12.366995073891626d;
    protected double weightedVar =   9.974760968886391d;
    protected double weightedStd = FastMath.sqrt(weightedVar);
    protected double weightedProduct = 8517647448765288000000d;
    protected double weightedSum = 251.05d;

    protected double tolerance = 10E-12;

    public UnivariateStatistic getUnivariateStatistic() {
        return new Variance();
    }

    public double expectedValue() {
        return this.var;
    }

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    public double expectedWeightedValue() {
        return this.weightedVar;
    }

    protected double populationVariance(double[] v) {
        double mean = new Mean().evaluate(v);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
           sum += (v[i] - mean) * (v[i] - mean);
        }
        return sum / v.length;
    }

    public static class TestArrayGen extends Generator<Double[]> {
        public TestArrayGen() {
            super(Double[].class);
        }

        // mutate the original testArray
        public Double[] generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            Double[] testArray =
                    { 12.5, 12.0, 11.8, 14.2, 14.9, 14.5, 21.0,  8.2, 10.3, 11.3,
                            14.1,  9.9, 12.2, 12.0, 12.1, 11.0, 19.8, 11.0, 10.0,  8.8,
                            9.0, 12.3 };

            for (int i = 0; i < testArray.length; i++) {
                testArray[i] = testArray[i] + random.nextDouble(-5, 5);
                // Negative value is not allowed.
                if (testArray[i] < 0) testArray[i] = -testArray[i];
            }

            return testArray;
        }
    }

    public static class WeightsArrayGen extends Generator<Double[]> {
        public WeightsArrayGen() {
            super(Double[].class);
        }
        public Double[] generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            Double[] testWeightsArray =
                    {  1.5,  0.8,  1.2,  0.4,  0.8,  1.8,  1.2,  1.1,  1.0,  0.7,
                            1.3,  0.6,  0.7,  1.3,  0.7,  1.0,  0.4,  0.1,  1.4,  0.9,
                            1.1,  0.3 };

            for (int i = 0; i < testWeightsArray.length; i++) {
                testWeightsArray[i] = testWeightsArray[i] + random.nextDouble(-1, 1);
                // Negative value is not allowed.
                if (testWeightsArray[i] < 0) testWeightsArray[i] = -testWeightsArray[i];
            }

            return testWeightsArray;
        }
    }

    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testEvaluateArraySegmentWeighted(@From(TestArrayGen.class) Double[] testArray,
                                                 @From(WeightsArrayGen.class) Double[] testWeightsArray) {
        // [Parameters]
        // Randomize the original arrays
        try {
            final WeightedEvaluation stat = (WeightedEvaluation) getUnivariateStatistic();
            final double[] arrayZero = new double[5];
            final double[] weightZero = new double[5];
            System.arraycopy(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(), 0, arrayZero, 0, 5);
            System.arraycopy(Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(), 0, weightZero, 0, 5);

            final double ret1 = stat.evaluate(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(),
                    Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(),
                    0, 5);
            final double expected1 = stat.evaluate(arrayZero, weightZero);
            final double[] arrayOne = new double[5];
            final double[] weightOne = new double[5];
            System.arraycopy(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(), 5, arrayOne, 0, 5);
            System.arraycopy(Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(), 5, weightOne, 0, 5);

            final double ret2 = stat.evaluate(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(),
                    Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(),
                    5, 5);
            final double expected2 = stat.evaluate(arrayOne, weightOne);
            final double[] arrayEnd = new double[5];
            final double[] weightEnd = new double[5];
            System.arraycopy(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(), testArray.length - 5, arrayEnd, 0, 5);
            System.arraycopy(Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(), testArray.length - 5, weightEnd, 0, 5);

            final double expected3= stat.evaluate(arrayEnd, weightEnd);
            final double ret3 = stat.evaluate(Stream.of(testArray).mapToDouble(Double::valueOf).toArray(),
                    Stream.of(testWeightsArray).mapToDouble(Double::valueOf).toArray(),
                    testArray.length - 5, 5);

            // [Preservation condition]
            // We reuse the same assertion condition as a preservation condition.
            // expectedN == retN regardless of the contents of testArray and testWeightsArray
            //
            // [Output]
            // All observed evaluation values
            Log.logOutIf(ret1==expected1 && ret2==expected2 && ret3==expected3, () -> new Double[]{ ret1, ret2, ret3 });
        } catch (Exception e) {
            Log.ignoreOut(e.toString());
        }
    }
}
