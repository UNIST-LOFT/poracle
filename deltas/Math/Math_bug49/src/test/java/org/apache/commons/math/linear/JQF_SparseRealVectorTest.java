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
package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.analysis.function.Abs;
import org.apache.commons.math.analysis.function.Acos;
import org.apache.commons.math.analysis.function.Asin;
import org.apache.commons.math.analysis.function.Atan;
import org.apache.commons.math.analysis.function.Cbrt;
import org.apache.commons.math.analysis.function.Cosh;
import org.apache.commons.math.analysis.function.Cos;
import org.apache.commons.math.analysis.function.Exp;
import org.apache.commons.math.analysis.function.Expm1;
import org.apache.commons.math.analysis.function.Inverse;
import org.apache.commons.math.analysis.function.Log10;
import org.apache.commons.math.analysis.function.Log1p;
import org.apache.commons.math.analysis.function.Log;
import org.apache.commons.math.analysis.function.Sinh;
import org.apache.commons.math.analysis.function.Sin;
import org.apache.commons.math.analysis.function.Sqrt;
import org.apache.commons.math.analysis.function.Tanh;
import org.apache.commons.math.analysis.function.Tan;
import org.apache.commons.math.analysis.function.Floor;
import org.apache.commons.math.analysis.function.Ceil;
import org.apache.commons.math.analysis.function.Rint;
import org.apache.commons.math.analysis.function.Signum;
import org.apache.commons.math.analysis.function.Ulp;
import org.apache.commons.math.analysis.function.Power;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;
import static org.junit.Assert.*;
import anonymous.log.*;
import java.util.Arrays;


import com.pholser.junit.quickcheck.random.*;

/**
 * Test cases for the {@link OpenMapRealVector} class.
 *
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_SparseRealVectorTest {



    //
    protected double[][] ma1 = {{1d, 2d, 3d}, {4d, 5d, 6d}, {7d, 8d, 9d}};
    protected double[] vec1 = {1d, 2d, 3d};
    protected double[] vec2 = {4d, 5d, 6d};
    protected double[] vec3 = {7d, 8d, 9d};
    protected double[] vec4 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[] vec5 = { -4d, 0d, 3d, 1d, -6d, 3d};
    protected double[] vec_null = {0d, 0d, 0d};
    protected Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[][] mat1 = {{1d, 2d, 3d}, {4d, 5d, 6d},{ 7d, 8d, 9d}};

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    // Testclass to test the RealVector interface
    // only with enough content to support the test
    public static class SparseRealVectorTestImpl extends AbstractRealVector implements Serializable {

        private static final long serialVersionUID = -6251371752518113791L;
        /** Entries of the vector. */
        protected double data[];

        public SparseRealVectorTestImpl(double[] d) {
            data = d.clone();
        }

        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }

        @Override
        public RealVector map(UnivariateRealFunction function) {
            throw unsupported();
        }

        @Override
        public RealVector mapToSelf(UnivariateRealFunction function) {
            throw unsupported();
        }

        @Override
        public Iterator<Entry> iterator() {
            throw unsupported();
        }

        @Override
        public AbstractRealVector copy() {
            return new SparseRealVectorTestImpl(data);
        }

        @Override
        public RealVector add(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector add(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector subtract(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector subtract(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector mapAdd(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapAddToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtract(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapSubtractToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapMultiply(double d) {
            double[] out = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i] * d;
            }
            return new OpenMapRealVector(out);
        }

        @Override
        public RealVector mapMultiplyToSelf(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivide(double d) {
            throw unsupported();
        }

        @Override
        public RealVector mapDivideToSelf(double d) {
            throw unsupported();
        }

        public RealVector ebeMultiply(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector ebeMultiply(double[] v) {
            throw unsupported();
        }

        public RealVector ebeDivide(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector ebeDivide(double[] v) {
            throw unsupported();
        }

        @Override
        public double[] getData() {
            return data.clone();
        }

        @Override
        public double dotProduct(RealVector v) {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v.getEntry(i);
            }
            return dot;
        }

        @Override
        public double dotProduct(double[] v) {
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * v[i];
            }
            return dot;
        }

        @Override
        public double getNorm() {
            throw unsupported();
        }

        @Override
        public double getL1Norm() {
            throw unsupported();
        }

        @Override
        public double getLInfNorm() {
            throw unsupported();
        }

        @Override
        public double getDistance(RealVector v) {
            throw unsupported();
        }

        @Override
        public double getDistance(double[] v) {
            throw unsupported();
        }

        @Override
        public double getL1Distance(RealVector v) {
            throw unsupported();
        }

        @Override
        public double getL1Distance(double[] v) {
            throw unsupported();
        }

        @Override
        public double getLInfDistance(RealVector v) {
            throw unsupported();
        }

        @Override
        public double getLInfDistance(double[] v) {
            throw unsupported();
        }

        @Override
        public RealVector unitVector() {
            throw unsupported();
        }

        @Override
        public void unitize() {
            throw unsupported();
        }

        public RealVector projection(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealVector projection(double[] v) {
            throw unsupported();
        }

        @Override
        public RealMatrix outerProduct(RealVector v) {
            throw unsupported();
        }

        @Override
        public RealMatrix outerProduct(double[] v) {
            throw unsupported();
        }

        public double getEntry(int index) {
            return data[index];
        }

        public int getDimension() {
            return data.length;
        }

        public RealVector append(RealVector v) {
            throw unsupported();
        }

        public RealVector append(double d) {
            throw unsupported();
        }

        public RealVector append(double[] a) {
            throw unsupported();
        }

        public RealVector getSubVector(int index, int n) {
            throw unsupported();
        }

        public void setEntry(int index, double value) {
            data[index] = value;
        }

        @Override
        public void setSubVector(int index, RealVector v) {
            throw unsupported();
        }

        @Override
        public void setSubVector(int index, double[] v) {
            throw unsupported();
        }

        @Override
        public void set(double value) {
            throw unsupported();
        }

        @Override
        public double[] toArray() {
            throw unsupported();
        }

        public boolean isNaN() {
            throw unsupported();
        }

        public boolean isInfinite() {
            throw unsupported();
        }

    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i],tolerance);
        }
    }
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testConcurrentModification(
            @InRange(minInt = -50, maxInt = 50) int a1,
            @InRange(minInt = -50, maxInt = 50) int a2,
            @InRange(minInt = -50, maxInt = 50) int a3,
            @InRange(minInt = -50, maxInt = 50) int a4,
            @InRange(minInt = -50, maxInt = 50) int a5,
            @InRange(minInt = -50, maxInt = 50) int a6,
            @InRange(minInt = -50, maxInt = 50) int a7,
            @InRange(minInt = -50, maxInt = 50) int a8,
            @InRange(minInt = -50, maxInt = 50) int a9
            ) {
        // [Parameters]
        // Randomizing all entries of real vectors
        try {
            final RealVector u = new OpenMapRealVector(3, 1e-6);
            u.setEntry(0, a1);
            u.setEntry(1, a2);
            u.setEntry(2, a3);

            final RealVector v1 = new OpenMapRealVector(3, 1e-6);
            final double[] v2 = new double[3];
            v1.setEntry(0, a4);
            v2[0] = a7;
            v1.setEntry(1, a5);
            v2[1] = a8;
            v1.setEntry(2, a6);
            v2[2] = a9;

            RealVector w;

            w = u.ebeMultiply(v1);
            final double[] ret1 = w.getData();
            w = u.ebeMultiply(v2);
            final double[] ret2 = w.getData();
            w = u.ebeDivide(v1);
            final double[] ret3 = w.getData();
            w = u.ebeDivide(v2);
            final double[] ret4 = w.getData();
            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] data of result vectors
            anonymous.log.Log.logOutIf(true, () ->
                    new String[]{Arrays.toString(ret1),Arrays.toString(ret2),
                                Arrays.toString(ret3), Arrays.toString(ret4)});
        } catch (Exception e) {
            anonymous.log.Log.ignoreOut();
        }
    }

}
