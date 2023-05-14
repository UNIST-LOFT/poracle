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
    @Fuzz
    public void testIterator(@InRange(minInt=1, maxInt=10)int i, @InRange(minInt=0, maxInt=1, isFixed=true) int check) {
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
                if(check==1)
                    iter.remove();
            }
            if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
            else Log.ignoreOut();
        } catch (Exception e){
            Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
    }
    
}
