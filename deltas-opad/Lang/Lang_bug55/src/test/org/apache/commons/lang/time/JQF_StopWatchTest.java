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
import anonymous.log.Log;

/**
 * TestCase for StopWatch.
 *
 * @author Stephen Colebourne
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_StopWatchTest /*extends TestCase*/ {

public static void main(String[] args) {
        TestRunner.run(suite());
}

public static Test suite() {
        TestSuite suite = new TestSuite(StopWatchTest.class);
        suite.setName("StopWatch Tests");
        return suite;
}




@Fuzz
public void testLang315(@InRange(minInt = 0, maxInt = 2) int interval) {

        try{
                StopWatch watch = new StopWatch();
                watch.start();
                try {Thread.sleep(interval);} catch (InterruptedException ex) {}
                watch.suspend();
                long suspendTime = watch.getTime();
                try {Thread.sleep(interval);} catch (InterruptedException ex) {}
                watch.stop();
                long totalTime = watch.getTime();
                long diff = totalTime - suspendTime;


                // if(Log.runBuggyVersion) diff = 0;

                final long out2 = diff;
                if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
                else Log.ignoreOut();
        } catch (Exception e){
                Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
        }
}

}
