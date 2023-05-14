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
package org.apache.commons.lang;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Unit tests {@link org.apache.commons.lang.NumberUtils}.
 *
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Eric Pugh
 * @author Phil Steitz
 * @author Stephen Colebourne
 * @version $Id$
 */
@RunWith(JQF.class)
public class JQF_NumberUtilsTest {

	@Fuzz
	public void testLang457(@InRange(minInt = 0, maxInt = 11, isFixed = true) int ch) {
		Log.infoLog("testLang457 is called");
		// long outval = (long)ch;
		Number num = null;
		boolean nfe = false;
		boolean eee = false;

		String[] inputs = new String[] { "l", "L", "f", "F", "junk",
		"bobL", "-+L", "+1l", "10L", "3.0f",
		"3f", "222"};



		// String instr = ""+(char)ch;
		// if(ch < 68) instr += "l"; else instr = "l";
		try {
			num = NumberUtils.createNumber(inputs[ch]);
			// Log.logOutIf(ch >= 48 && ch <= 57, () -> new Long[]{ out2 });
			// Log.logOutIf(true, () -> new String[]{ instr });
			if (Log.runBuggyVersion) Log.logOutIf(true, () -> new String[]{ "No exception" });
			else Log.ignoreOut();
		} catch (Exception e){
			Log.logOutIf(true, () -> new String[] {e.getClass().toString()});
		}
		/*
		    String[] badInputs = new String[] { "l", "L", "f", "F", "junk", "bobL"};
		       for(int i=0; i<badInputs.length; i++) {
		           try {
		               NumberUtils.createNumber(badInputs[i]);
		               fail("NumberFormatException was expected for " + badInputs[i]);
		           } catch (NumberFormatException e) {
		               return; // expected
		           }
		       }
		 */
}

}
