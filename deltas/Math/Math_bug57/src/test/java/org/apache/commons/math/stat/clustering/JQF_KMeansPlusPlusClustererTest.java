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

package org.apache.commons.math.stat.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import com.pholser.junit.quickcheck.random.*;
import anonymous.log.Log;
import com.thoughtworks.xstream.XStream;

@RunWith(JQF.class)
public class JQF_KMeansPlusPlusClustererTest {
    /**
     * A helper class for testSmallDistances(). This class is similar to EuclideanIntegerPoint, but
     * it defines a different distanceFrom() method that tends to return distances less than 1.
     */
    private class CloseIntegerPoint implements Clusterable<CloseIntegerPoint> {
        public CloseIntegerPoint(EuclideanIntegerPoint point) {
            euclideanPoint = point;
        }

        public double distanceFrom(CloseIntegerPoint p) {
            return euclideanPoint.distanceFrom(p.euclideanPoint) * 0.001;
        }

        public CloseIntegerPoint centroidOf(Collection<CloseIntegerPoint> p) {
            Collection<EuclideanIntegerPoint> euclideanPoints =
                new ArrayList<EuclideanIntegerPoint>();
            for (CloseIntegerPoint point : p) {
                euclideanPoints.add(point.euclideanPoint);
            }
            return new CloseIntegerPoint(euclideanPoint.centroidOf(euclideanPoints));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CloseIntegerPoint)) {
                return false;
            }
            CloseIntegerPoint p = (CloseIntegerPoint) o;

            //System.out.println("euclideanPoint = " + euclideanPoint);
            //System.out.println("p.euclideanPoint = " + p.euclideanPoint);
            boolean ret = euclideanPoint.equals(p.euclideanPoint);
            //System.out.println("ret = " + ret);
            return ret;
        }

        @Override
        public int hashCode() {
            return euclideanPoint.hashCode();
        }

        private EuclideanIntegerPoint euclideanPoint;
    }

    /**
     * Test points that are very close together. See issue MATH-546.
     */
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testSmallDistances(@InRange(minInt=1, maxInt=10) int p1,
                                   @InRange(minInt=1, maxInt=10) int r1,
                                   @InRange(minDouble = 0, maxDouble = 1, isFixed = true) double ch) {
        try {
            // [Parameters]
            // Randomizing all the points to be added
            int[] uniqueArray = {p1};
            CloseIntegerPoint uniquePoint =
                    new CloseIntegerPoint(new EuclideanIntegerPoint(uniqueArray));

            Collection<CloseIntegerPoint> points = new ArrayList<CloseIntegerPoint>();
            final int NUM_REPEATED_POINTS = 10 * 1000;
            // Depending on the random value of ch, the following two cases are tested.
            // 1. The first 10000 elements of points have the same value (the same as in the original test). 
            // 2. The first 10000 elements of points are widely spread apart (the opposite case).
            Random rand = new Random();
            for (int i = 0; i < NUM_REPEATED_POINTS; ++i) {
                if(ch > 0.5) {                    
                    points.add(new CloseIntegerPoint(new EuclideanIntegerPoint(new int[]{ r1 * i })));
                }
                else {
                    points.add(new CloseIntegerPoint(new EuclideanIntegerPoint(new int[]{ r1 })));
                }
            }
            points.add(uniquePoint);

            // Ask a KMeansPlusPlusClusterer to run zero iterations (i.e., to simply choose initial
            // cluster centers).
            final long RANDOM_SEED = 0;
            final int NUM_CLUSTERS = 2;
            final int NUM_ITERATIONS = 0;
            KMeansPlusPlusClusterer<CloseIntegerPoint> clusterer =
                    new KMeansPlusPlusClusterer<CloseIntegerPoint>(new Random(RANDOM_SEED));
            List<Cluster<CloseIntegerPoint>> clusters =
                    clusterer.cluster(points, NUM_CLUSTERS, NUM_ITERATIONS);

            // [Preservation condition] We check whether we are in first or second scenario and if it is second scenario we print the output
            // [Output] we print whether center equals to uniquePoint or not.
            boolean uniquePointIsCenter = false;
            for (Cluster<CloseIntegerPoint> cluster : clusters) {
                Log.logOutIf(ch > 0.5,() -> new Integer[]{ cluster.getCenter().equals(uniquePoint)? 1 : 0 });
            }
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }
}
