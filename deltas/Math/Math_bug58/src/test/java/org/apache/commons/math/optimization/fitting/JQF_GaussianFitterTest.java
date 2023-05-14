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

package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;

import org.junit.Assert;
import org.junit.Test;

//Additional packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;
import java.util.Arrays;

/**
 * Tests {@link GaussianFitter}.
 *
 * @since 2.2
 * @version $Revision$ $Date$
 */
@RunWith(JQF.class)
public class JQF_GaussianFitterTest {
    public static class DoubleArrayGenerator extends Generator<Double[]> {
        public DoubleArrayGenerator() {
            super(Double[].class);
        }

        public Double[] generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            Double[] data = {
                    1.1143831578403364E-29,
                    4.95281403484594E-28,
                    1.1171347211930288E-26,
                    1.7044813962636277E-25,
                    1.9784716574832164E-24,
                    1.8630236407866774E-23,
                    1.4820532905097742E-22,
                    1.0241963854632831E-21,
                    6.275077366673128E-21,
                    3.461808994532493E-20,
                    1.7407124684715706E-19,
                    8.056687953553974E-19,
                    3.460193945992071E-18,
                    1.3883326374011525E-17,
                    5.233894983671116E-17,
                    1.8630791465263745E-16,
                    6.288759227922111E-16,
                    2.0204433920597856E-15,
                    6.198768938576155E-15,
                    1.821419346860626E-14,
                    5.139176445538471E-14,
                    1.3956427429045787E-13,
                    3.655705706448139E-13,
                    9.253753324779779E-13,
                    2.267636001476696E-12,
                    5.3880460095836855E-12,
                    1.2431632654852931E-11
            };
            for (int i = 0; i < data.length; i++) {
                double delta = random.nextDouble(-5, 5);
                data[i] += delta;
            }

            return data;
        }
    }
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testMath519(@From(DoubleArrayGenerator.class) Double[] data) {
        // [Parameters]
        // Randomizing all data points
        try {
            LevenbergMarquardtOptimizer lv = new LevenbergMarquardtOptimizer();
            GaussianFitter fitter = new GaussianFitter(lv);
            for (int i = 0; i < data.length; i++) {
                fitter.addObservedPoint(i, data[i]);
            }

            final double[] p = fitter.fit();
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] elements of p (which is the return value of the method under testing)
            Log.logOutIf(true, () -> Arrays.stream(p).boxed().toArray());
        } catch(Exception e){
            Log.ignoreOut();
        }
    }

    /**
     * Adds the specified points to specified <code>GaussianFitter</code>
     * instance.
     *
     * @param points data points where first dimension is a point index and
     *        second dimension is an array of length two representing the point
     *        with the first value corresponding to X and the second value
     *        corresponding to Y
     * @param fitter fitter to which the points in <code>points</code> should be
     *        added as observed points
     */
    protected static void addDatasetToGaussianFitter(double[][] points,
                                                     GaussianFitter fitter) {
        for (int i = 0; i < points.length; i++) {
            fitter.addObservedPoint(points[i][0], points[i][1]);
        }
    }
}
