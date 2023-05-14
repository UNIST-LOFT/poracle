// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math3.optimization.fitting;

import java.util.Random;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import org.junit.Test;
import org.junit.Assert;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

@RunWith(JQF.class)
public class JQF_HarmonicFitterTest {

    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testMath844(@InRange(minInt=-3, maxInt=3, isFixed=true) int y00,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y01,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y02,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y03,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y04,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y05,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y10,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y11,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y12,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y13,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y14,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y15,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y20,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y21,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y22,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y23,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y24,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y25,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y30,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y31,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y32,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y33,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y34,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y35,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y40,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y41,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y42,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y43,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y44,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y45,
                            @InRange(minInt=-3, maxInt=3, isFixed=true) int y46) {
        // [Parameters]
        // Randomizing all the points for HarmonicFitter.ParameterGuesser
        final double[] y = { y00, y01, y02, y03, y04, y05,
                             y10, y11, y12, y13, y14, y15,
                             y20, y21, y22, y23, y24, y25,
                             y30, y31, y32, y33, y34, y35,
                             y40, y41, y42, y43, y44, y45, y46 };

        final int len = y.length;
        final WeightedObservedPoint[] points = new WeightedObservedPoint[len];
        for (int i = 0; i < len; i++) {
            points[i] = new WeightedObservedPoint(1, i, y[i]);
        }

        final HarmonicFitter.ParameterGuesser guesser
                = new HarmonicFitter.ParameterGuesser(points);

        double[] ret = guesser.guess();
        // [Preservation condition]
        // According to the bug report (https://markmail.org/message/42yuec5lk5wr6gbo#query:+page:1+mid:4lq4brc35vzgi7so+state:results),
        // ret is not supposed to contain infinity. We ignore such cases.
        // 
        // [Output] the elements of array ret
        Log.logOutIf(!isIllegalInput(ret), () -> DoubleStream.of(ret).boxed().toArray(Double[]::new));
    }

    private boolean isIllegalInput(double[] x) {
        for (int i = 0; i < x.length; i++) {
            if (x[i] == Double.POSITIVE_INFINITY) return true;
        }
        return false;
    }
}
