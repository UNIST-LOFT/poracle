/*
 *  Copyright 2001-2005 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.tz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.ZoneInfoCompiler.DateTimeOfYear;

// Additional Packages
import static org.junit.Assert.*;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.random.*;
import edu.berkeley.cs.jqf.fuzz.*;
import org.junit.runner.RunWith;
import anonymous.log.Log;

/**
 * Test cases for ZoneInfoCompiler.
 *
 * @author Brian S O'Neill
 */
@RunWith(JQF.class)
public class JQF_TestCompiler {

    // Log logger = new Log();
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        return new TestSuite(TestCompiler.class);
    }

    public static class IntArrayGenerator extends Generator<Integer[]> {
        public IntArrayGenerator() {
            super(Integer[].class);
        }

        public Integer[] generate(SourceOfRandomness random, GenerationStatus __ignore__) {
            Integer temp = random.nextInt(1,10);
            return new Integer[]{temp, random.nextInt(0,temp-1)};
        }
    }

    static final String AMERICA_LOS_ANGELES_FILE =
            "# Rules for building just America/Los_Angeles time zone.\n" +
                    "\n" +
                    "Rule    US  1918    1919    -   Mar lastSun 2:00    1:00    D\n" +
                    "Rule    US  1918    1919    -   Oct lastSun 2:00    0   S\n" +
                    "Rule    US  1942    only    -   Feb 9   2:00    1:00    W # War\n" +
                    "Rule    US  1945    only    -   Aug 14  23:00u  1:00    P # Peace\n" +
                    "Rule    US  1945    only    -   Sep 30  2:00    0   S\n" +
                    "Rule    US  1967    max -   Oct lastSun 2:00    0   S\n" +
                    "Rule    US  1967    1973    -   Apr lastSun 2:00    1:00    D\n" +
                    "Rule    US  1974    only    -   Jan 6   2:00    1:00    D\n" +
                    "Rule    US  1975    only    -   Feb 23  2:00    1:00    D\n" +
                    "Rule    US  1976    1986    -   Apr lastSun 2:00    1:00    D\n" +
                    "Rule    US  1987    max -   Apr Sun>=1  2:00    1:00    D\n" +
                    "\n" +
                    "Rule    CA  1948    only    -   Mar 14  2:00    1:00    D\n" +
                    "Rule    CA  1949    only    -   Jan  1  2:00    0   S\n" +
                    "Rule    CA  1950    1966    -   Apr lastSun 2:00    1:00    D\n" +
                    "Rule    CA  1950    1961    -   Sep lastSun 2:00    0   S\n" +
                    "Rule    CA  1962    1966    -   Oct lastSun 2:00    0   S\n" +
                    "\n" +
                    "Zone America/Los_Angeles -7:52:58 - LMT 1883 Nov 18 12:00\n" +
                    "            -8:00   US  P%sT    1946\n" +
                    "            -8:00   CA  P%sT    1967\n" +
                    "            -8:00   US  P%sT";

    private DateTimeZone originalDateTimeZone = null;

    protected void setUp() throws Exception {
        originalDateTimeZone = DateTimeZone.getDefault();
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    protected void tearDown() throws Exception {
        DateTimeZone.setDefault(originalDateTimeZone);
    }

    //-----------------------------------------------------------------------
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.
    @Fuzz
    public void testDateTimeZoneBuilder(
            @InRange(minInt = 10, maxInt = 20, isFixed=true) int size) throws Exception {
        setUp();
        // [Parameters]
        // Randomizing the size of the array zone
        try {
            DateTimeZone dz = getTestDataTimeZoneBuilder().toDateTimeZone("TestDTZ1", true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));
            final DateTimeZone[] zone = new DateTimeZone[size];
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        // [Preservation condition] We reuse the same assertion condition as a preservation condition.
                        // [Output] the output from console
                        zone[0] = getTestDataTimeZoneBuilder().toDateTimeZone("TestDTZ2", true);
                        Log.logOutIf(zone[0]!=null,() -> new String[]{baos.toString()});
                    }catch (Exception e1) {}
                }
            });
            t.setName("testThread");
            t.start();
            t.join();

        } catch (Exception e2){
            Log.ignoreOut();
        } finally {
            tearDown();
        }
    }

    private DateTimeZoneBuilder getTestDataTimeZoneBuilder() {
        try{Provider provider = compileAndLoad(AMERICA_LOS_ANGELES_FILE);} catch (Exception e){}
        return new DateTimeZoneBuilder()
                .addCutover(1601, 'w', 1, 1, 1, false, 7200000)
                .setStandardOffset(3600000)
                .addRecurringSavings("", 3600000, 1601, Integer.MAX_VALUE, 'w', 3, -1, 1, false, 7200000)
                .addRecurringSavings("", 0, 1601, Integer.MAX_VALUE, 'w', 10, -1, 1, false, 10800000);
    }
    private Provider compileAndLoad(String data) throws Exception {
        File tempDir = createDataFile(data);
        File destDir = makeTempDir();

        ZoneInfoCompiler.main(new String[] {
                "-src", tempDir.getAbsolutePath(),
                "-dst", destDir.getAbsolutePath(),
                "-verbose",
                "tzdata"

        });

        // Mark all files to be deleted on exit.
        deleteOnExit(destDir);

        return new ZoneInfoProvider(destDir);
    }
    private File createDataFile(String data) throws IOException {
        File tempDir = makeTempDir();

        File tempFile = new File(tempDir, "tzdata");
        tempFile.deleteOnExit();

        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buf = new byte[1000];
        int amt;
        while ((amt = in.read(buf)) > 0) {
            out.write(buf, 0, amt);
        }
        out.close();
        in.close();

        return tempDir;
    }

    private File makeTempDir() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        tempDir = new File(tempDir, "joda-test-" + (new java.util.Random().nextInt() & 0xffffff));
        tempDir.mkdirs();
        tempDir.deleteOnExit();
        return tempDir;
    }

    private void deleteOnExit(File tempFile) {
        tempFile.deleteOnExit();
        if (tempFile.isDirectory()) {
            File[] files = tempFile.listFiles();
            for (int i=0; i<files.length; i++) {
                deleteOnExit(files[i]);
            }
        }
    }
}
