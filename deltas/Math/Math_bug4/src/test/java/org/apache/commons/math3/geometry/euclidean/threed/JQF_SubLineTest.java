package org.apache.commons.math3.geometry.euclidean.threed;

import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
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
public class JQF_SubLineTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper. 
    @Fuzz
    public void testIntersectionNotIntersecting(@InRange(minDouble=-5, maxDouble=5) double x) throws MathIllegalArgumentException {
        try {
            // [Parameters]
            // We want to allow both of the following:
            // 1. intersection
            // 2. non-intersection
            // The original test allows only non-intersection
            // As x moves, we can have both scenarios.
            final SubLine sub1 = new SubLine(new Vector3D(0, 0, 0), new Vector3D(0, 3, 0));
            final SubLine sub2 = new SubLine(new Vector3D(x, 0, 0), new Vector3D(1, 3, 0));
            Vector3D ret = sub1.intersection(sub2, true);

            // [Preservation condition] we use true for an unexpected exception case.
            // [Output] coordinate of ret (which is the return value of the method under testing)
            Log.logOutIf(true, () -> new Double[]{ret.getX(), ret.getY(), ret.getZ()});
        } catch (Exception e) {
            Log.ignoreOut();
        }
    }

}
