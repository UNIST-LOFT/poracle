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
package org.apache.commons.lang.time;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

//Additional Packages
import static org.junit.Assert.*;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import edu.berkeley.cs.jqf.fuzz.reach.Log;

/**
 * TestCase for StopWatch.
 *
 * @author Stephen Colebourne
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_StopWatchTest /*extends TestCase*/ {

    Log logger = new Log();

    public static class IntGenerator extends Generator<Integer> {
        public IntGenerator() {
            super(Integer.class);
        }

        public Integer generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            int m = random.nextInt(0, 2);
            return m;
        }
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(StopWatchTest.class);
        suite.setName("StopWatch Tests");
        return suite;
    }


    //-----------------------------------------------------------------------
    public void testStopWatchSimple() {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.stop();
        long time = watch.getTime();
        assertEquals(time, watch.getTime());

        assertTrue(time >= 500);
        assertTrue(time < 700);

        watch.reset();
        assertEquals(0, watch.getTime());
    }

    public void testStopWatchSimpleGet() {
        StopWatch watch = new StopWatch();
        assertEquals(0, watch.getTime());
        assertEquals("0:00:00.000", watch.toString());

        watch.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        assertTrue(watch.getTime() < 2000);
    }

    public void testStopWatchSplit() {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.split();
        long splitTime = watch.getSplitTime();
        String splitStr = watch.toSplitString();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.unsplit();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.stop();
        long totalTime = watch.getTime();

        assertEquals("Formatted split string not the correct length",
                splitStr.length(), 11);
        assertTrue(splitTime >= 500);
        assertTrue(splitTime < 700);
        assertTrue(totalTime >= 1500);
        assertTrue(totalTime < 1900);
    }

    public void testStopWatchSuspend() {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.suspend();
        long suspendTime = watch.getTime();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.resume();
        try {
            Thread.sleep(550);
        } catch (InterruptedException ex) {
        }
        watch.stop();
        long totalTime = watch.getTime();

        assertTrue(suspendTime >= 500);
        assertTrue(suspendTime < 700);
        assertTrue(totalTime >= 1000);
        assertTrue(totalTime < 1300);
    }

    @Fuzz
    public void testLang315(@From(IntGenerator.class) int interval) {
        logger.logIn(interval);
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
        }
        watch.suspend();
        long suspendTime = watch.getTime();
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
        }
        watch.stop();
        long totalTime = watch.getTime();
        long diff = totalTime - suspendTime;
        if (Log.runBuggyVersion) diff = 0;
        logger.logOut(diff);
        //assertTrue( suspendTime == totalTime );
    }

    // test bad states
    public void testBadStates() {
        StopWatch watch = new StopWatch();
        try {
            watch.stop();
            fail("Calling stop on an unstarted StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.stop();
            fail("Calling stop on an unstarted StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.suspend();
            fail("Calling suspend on an unstarted StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.split();
            fail("Calling split on a non-running StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.unsplit();
            fail("Calling unsplit on an unsplit StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.resume();
            fail("Calling resume on an unsuspended StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        watch.start();

        try {
            watch.start();
            fail("Calling start on a started StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.unsplit();
            fail("Calling unsplit on an unsplit StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.getSplitTime();
            fail("Calling getSplitTime on an unsplit StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        try {
            watch.resume();
            fail("Calling resume on an unsuspended StopWatch should throw an exception. ");
        } catch (IllegalStateException ise) {
            // expected
        }

        watch.stop();

        try {
            watch.start();
            fail("Calling start on a stopped StopWatch should throw an exception as it needs to be reset. ");
        } catch (IllegalStateException ise) {
            // expected
        }

    }

}
