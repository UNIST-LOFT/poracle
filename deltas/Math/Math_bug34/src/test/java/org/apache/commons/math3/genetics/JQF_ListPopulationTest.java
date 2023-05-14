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
package org.apache.commons.math3.genetics;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
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


@RunWith(JQF.class)
public class JQF_ListPopulationTest {
    //@Test(expected = UnsupportedOperationException.class)
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testIterator(@InRange(minInt=1, maxInt=10) int i, @InRange(minDouble=0, maxDouble=1, isFixed=true) double check) {
        // [Parameters]
        // 1. The parameter of randomBinaryRepresentation
        // 2. We test the following cases with random input:
        // Case 1. It is expected that UnsupportedOperationException is thrown.
        // Case 2. It is not expected that an exception is thrown 
        // 
        // We randomly choose a case based on a random value of check.
        
        final ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
        final ListPopulation population = new ListPopulation(10) {
            public Population nextGeneration() {
                // not important
                return null;
            }
        };
        try {
            chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(i)));
            chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(i)));
            chromosomes.add(new DummyBinaryChromosome(BinaryChromosome.randomBinaryRepresentation(i)));
            population.addChromosomes(chromosomes);
            final Iterator<Chromosome> iter = population.iterator();

            while (iter.hasNext()) {
                iter.next();
                if (check >= 0.5)
                    iter.remove(); // unsupported operation
            }
            // [Preservation condition] The API document of ListPopulation's iterator specifies the following:
            // "Returns an iterator over the unmodifiable list of chromosomes"
            //
            // [Output] size of the population
            Log.logOutIf(check < 0.5, () -> new Integer[] { population.getPopulationSize() });
        } catch (UnsupportedOperationException e1){
            Log.logOutIf(check >= 0.5, () -> new String[] { e1.toString() });
        } catch (Exception e2) {
            Log.logOutIf(true, () -> new String[] { e2.toString() });
        }
    }
    
}
