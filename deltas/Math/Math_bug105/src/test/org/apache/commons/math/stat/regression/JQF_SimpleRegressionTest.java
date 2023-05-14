/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.regression;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
//Additional Packages
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import java.util.Random;
import org.junit.runner.RunWith;
import anonymous.log.Log;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision$ $Date$
 */

@RunWith(JQF.class)
public final class JQF_SimpleRegressionTest  {
    /*
     * NIST "Norris" refernce data set from
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
     * Strangely, order is {y,x}
     */


    private double[][] data = { { 0.1, 0.2 }, {338.8, 337.4 }, {118.1, 118.2 },
            {888.0, 884.6 }, {9.2, 10.1 }, {228.1, 226.5 }, {668.5, 666.3 }, {998.5, 996.3 },
            {449.1, 448.6 }, {778.9, 777.0 }, {559.2, 558.2 }, {0.3, 0.4 }, {0.1, 0.6 }, {778.1, 775.5 },
            {668.8, 666.9 }, {339.3, 338.0 }, {448.9, 447.5 }, {10.8, 11.6 }, {557.7, 556.0 },
            {228.3, 228.1 }, {998.0, 995.8 }, {888.8, 887.6 }, {119.6, 120.2 }, {0.3, 0.3 },
            {0.6, 0.3 }, {557.6, 556.8 }, {339.3, 339.1 }, {888.0, 887.2 }, {998.5, 999.0 },
            {778.9, 779.0 }, {10.2, 11.1 }, {117.6, 118.3 }, {228.9, 229.2 }, {668.4, 669.1 },
            {449.2, 448.9 }, {0.2, 0.5 }
    };

    /*
     * Correlation example from
     * http://www.xycoon.com/correlation.htm
     */
    private double[][] corrData = { { 101.0, 99.2 }, {100.1, 99.0 }, {100.0, 100.0 },
            {90.6, 111.6 }, {86.5, 122.2 }, {89.7, 117.6 }, {90.6, 121.1 }, {82.8, 136.0 },
            {70.1, 154.2 }, {65.4, 153.6 }, {61.3, 158.5 }, {62.5, 140.6 }, {63.6, 136.2 },
            {52.6, 168.0 }, {59.7, 154.3 }, {59.5, 149.0 }, {61.3, 165.5 }
    };

    /*
     * From Moore and Mcabe, "Introduction to the Practice of Statistics"
     * Example 10.3
     */
    private double[][] infData = { { 15.6, 5.2 }, {26.8, 6.1 }, {37.8, 8.7 }, {36.4, 8.5 },
            {35.5, 8.8 }, {18.6, 4.9 }, {15.3, 4.5 }, {7.9, 2.5 }, {0.0, 1.1 }
    };

    /*
     * Data with bad linear fit
     */
    private double[][] infData2 = { { 1, 1 }, {2, 0 }, {3, 5 }, {4, 2 },
            {5, -1 }, {6, 12 }
    };


    public void setUp() {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SimpleRegressionTest.class);
        suite.setName("BivariateRegression Tests");
        return suite;
    }
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testSSENonNegative(
            @InRange(minDouble =8910, maxDouble =8920) double d1,
            @InRange(minDouble =8914, maxDouble =8924) double d2,
            @InRange(minDouble =8918, maxDouble =8928) double d3,
            @InRange(minDouble =1E2, maxDouble =2E2) double d4,
            @InRange(minDouble =1E2, maxDouble =2E2) double d5,
            @InRange(minDouble =1E2, maxDouble =2E2) double d6

            )
    {
        // [Parameters]
        // Randomizing all parameters of the data
        double[] y = { d1, d2, d3 };
        double[] x = { d4, d5, d6 };
        SimpleRegression reg = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            reg.addData(x[i], y[i]);
        }
        final double ret1 = reg.getSumSquaredErrors();
        // [Preservation condition]
        // We reuse the same assertion condition as a preservation condition.
        //
        // [Output] ret1 (which is the return values of the method under testing)
        Log.logOutIf(ret1>=0.0,() -> new Double[]{ret1});
    }
}
