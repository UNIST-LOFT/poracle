package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem3;
import org.apache.commons.math3.ode.TestProblem4;
import org.apache.commons.math3.ode.TestProblem5;
import org.apache.commons.math3.ode.TestProblemHandler;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
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
public class JQF_DormandPrince853IntegratorTest {
    
    // The specified ranges of the parameters are only initial ones, and the actual ranges are 
    // adjusted during the fuzzing process, as described in the paper.  
    @Fuzz
    public void testEventsScheduling(@InRange(minDouble = 0, maxDouble = 1) double maxCheckInterval,
                                     @InRange(minInt = 95, maxInt = 105) int maxIterationCount,
                                     @InRange(minDouble = 0, maxDouble = 1) double t0,
                                     @InRange(minDouble = 5, maxDouble = 15) double t) {
        // [Parameters]
        // Randomizing all parameters of the addEventHandler and integrate function
        FirstOrderDifferentialEquations sincos = new FirstOrderDifferentialEquations() {

            public int getDimension() {
                return 2;
            }

            public void computeDerivatives(double t, double[] y, double[] yDot) {
                yDot[0] =  y[1];
                yDot[1] = -y[0];
            }

        };

        SchedulingChecker sinChecker = new SchedulingChecker(0); // events at 0, PI, 2PI ...
        SchedulingChecker cosChecker = new SchedulingChecker(1); // events at PI/2, 3PI/2, 5PI/2 ...

        FirstOrderIntegrator integ =
                new DormandPrince853Integrator(0.001, 1.0, 1.0e-12, 0.0);
        integ.addEventHandler(sinChecker, maxCheckInterval, 1.0e-7, maxIterationCount);
        integ.addStepHandler(sinChecker);
        integ.addEventHandler(cosChecker, maxCheckInterval, 1.0e-7, maxIterationCount);
        integ.addStepHandler(cosChecker);
        double[] y0 = new double[] { FastMath.sin(t0), FastMath.cos(t0) };
        double[] y  = new double[2];
        try {
            double ret = integ.integrate(sincos, t0, y0, t, y);
            // [Preservation condition] Preserve when assertionError does not occur.
            //
            // [Output] we monitor the return value of the method under testing.
            Log.logOutIf(true, () -> new Double[] { ret });
        } catch (AssertionError e) {
            Log.ignoreOut();
        }
    }


    private static class SchedulingChecker implements StepHandler, EventHandler {

        int index;
        double tMin;

        public SchedulingChecker(int index) {
            this.index = index;
        }

        public void init(double t0, double[] y0, double t) {
            tMin = t0;
        }

        public void handleStep(StepInterpolator interpolator, boolean isLast) {
            tMin = interpolator.getCurrentTime();
        }

        public double g(double t, double[] y) {
            final double temp1 = t;
            final double temp2 = y[index];
            Assert.assertTrue(t >= tMin);
            return y[index];
        }

        public Action eventOccurred(double t, double[] y, boolean increasing) {
            return Action.RESET_STATE;
        }

        public void resetState(double t, double[] y) {
            // in fact, we don't need to reset anything for the test
        }

    }
    
}


