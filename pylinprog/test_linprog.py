from linprog import *
from unittest import TestCase

class TestLinsolve(TestCase):
    """Tests for the generic linsolve method"""
    num = RationalNumbers()

    def assertVectorEqual(self, a, b):
        a = self.num.coerce_vec(a)
        b = self.num.coerce_vec(b)        
        self.assertTrue( all(self.num.iszero(ai-bi) for ai, bi in zip(a,b)),
                         "Vector {a} not equal to {b}".format(a=self.showVector(a), b=self.showVector(b)))
        
    def showVector(self, v):
        return '[' + ", ".join(str(xi) for xi in v)+']'
    
    def testTrivialIneq(self):
        # X <= 2; X>=0; X -> min
        res, sol = linsolve( objective = [1], ineq_left=[[1]], ineq_right = [2], nonneg_variables=[0], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [0] )

        # X <= 2; X>=0; -X -> min
        res, sol = linsolve( objective = [-1], ineq_left=[[1]], ineq_right = [2], nonneg_variables=[0], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [2] )

    def testTrivialIneqUndefinedSign(self):
        # X <= -2; -X -> min
        res, sol = linsolve( objective = [-1], ineq_left=[[1]], ineq_right = [-2], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [-2] )

        # X >= 2; X -> min
        res, sol = linsolve( objective = [1], ineq_left=[[-1]], ineq_right = [-2], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [2] )

    def testTrivialEq(self):
        
        # X = 2; X>=0;  X -> min
        res, sol = linsolve( objective = [1], eq_left=[[1]], eq_right = [2], nonneg_variables=[0], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [2] )

        # X = 2; X -> min
        res, sol = linsolve( objective = [1], eq_left=[[1]], eq_right = [2], do_coerce=True )
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [2] )

    def testTrivialUnbound(self):
        # x >=0  -x -> min
        res, sol = linsolve( objective = [-1], nonneg_variables=[0], do_coerce=True )
        self.assertEqual(res, RESOLUTION_UNBOUNDED)

        # x >= -2  -x -> min
        res, sol = linsolve( objective = [-1], ineq_left=[[-1]], ineq_right=[2], do_coerce=True )
        self.assertEqual(res, RESOLUTION_UNBOUNDED)

    def testTrivialIncompatible(self):
        # x >= 1
        # x <= -2
        # x -> min
        res, sol = linsolve( objective = [1], ineq_left=[[-1],[1]], ineq_right=[-1, -2], do_coerce=True )
        self.assertEqual(res, RESOLUTION_INCOMPATIBLE)

    def testNontrivial(self):
        #A problem from the book
        A = [[1,-1,2,-2,1,-1],
            [-1,1,-1,1,1,-1],
            [2,-2,-3,3,1,-1]]

        C = [2,-2,6,-6,7,-7]

        B = [2,-1, -1]

        resolution, sol = linsolve( C, eq_left = A, eq_right = B,
                                    do_coerce=True,
                                    num = RationalNumbers())
        # print(sol[0].numerator/sol[0].denominator)
        print(sol)
        # self.assertEqual(resolution, RESOLUTION_SOLVED)
        # self.assertVectorEqual(sol, [60, 80])

        
    def testBivariatePositiveEq(self):
        # x+y = 1
        # x,y >= 0
        # x + 2y -> min

        res, sol = linsolve( objective = [1,2],
                             eq_left=[[1,1]],
                             eq_right = [1],
                             nonneg_variables=[0,1],
                             do_coerce=True )
        
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [1,0] )

        # x+y = 1
        # x,y >= 0
        # x + 2y -> max

        res, sol = linsolve( objective = [-1,-2],
                             eq_left=[[1,1]],
                             eq_right = [1],
                             nonneg_variables=[0,1],
                             do_coerce=True )
        
        self.assertEqual(res, RESOLUTION_SOLVED)
        self.assertVectorEqual(sol, [0,1] )
        
        
    
class TestSimplex(TestCase):
    def testInconsistent(self):
        # x < 1
        # x > 2
        # x -> max
        #
        # Bring to canform:
        #  x = x1-x2
        #
        #    x1 - x2 + z1 = 1
        #    x1 - x2 - z2 = 2
        #    -x1 + x2 -> min
        A = [[1,  -1,  1, 0],
             [1,  -1,  0, -1]]
        C = [-1, 1, 0, 0]
        B = [1, 2]
        basis = [None, None]
        num = RationalNumbers()
        resolution, sol = simplex_canonical_m(A,B,C,basis, num, verbose=False, do_coerce=True)
        self.assertEqual(resolution, RESOLUTION_INCOMPATIBLE)
    def testUnbound(self):
        # x > 1
        # y > 1
        # x + y -> max
        A = [[1, 0, -1, 0],
             [0, 1, 0, -1]]
        B = [1, 1]
        C = [-1, -1, 0, 0]
        basis = [None, None]
        num = RationalNumbers()
        resolution, sol = simplex_canonical_m(A,B,C,basis, num, verbose=False, do_coerce=True)
        self.assertEqual(resolution, RESOLUTION_UNBOUNDED)
        
    def testCanonical(self):
        A = [[2,   4,    1, 0, 0],
             [0.5, 0.25, 0, 1, 0],
             [2,   2.5,  0, 0, 1]]
    
        C = [-8,   -14,  0, 0, 0]
    
        B = [440, 65, 320]
        basis = [2,3,4]
        num = RationalNumbers()

        resolution, sol = simplex_canonical(A,B,C,basis, num, verbose=False, do_coerce=True)

        expect = num.coerce_vec([60,80,0,15,0])
        self.assertEqual(resolution, RESOLUTION_SOLVED)
        self.assertEqual(sol, expect)
        
    def testMMethod(self):
        A = [[2,   4,    1, 0, 0],
             [0.5, 0.25, 0, 1, 0],
             [2,   2.5,  0, 0, 1]]
    
        C = [-8,   -14,  0, 0, 0]
    
        B = [440, 65, 320]
        basis = [None, None, None]
        num = RationalNumbers()

        resolution, sol = simplex_canonical_m(A,B,C,basis, num, verbose=False, do_coerce=True)

        expect = num.coerce_vec([60,80,0,15,0])
        self.assertEqual(resolution, RESOLUTION_SOLVED)
        self.assertEqual(sol, expect)
    
    def testMMethodZeroRatios1(self):
        A = [[1,0,0,-1,0,0],
             [1,1,-1,0,1,0],
             [-1,-4,4,0,0,1]]

        C = [-1,-3,3,0,0,0]

        B = [1,4,0]
        basis = [None,None, None]
        #num = RealFiniteTolerance(eps=0.00001)
        num = RationalNumbers()

        resolution, sol = simplex_canonical_m(A,B,C, basis, num, verbose=False, do_coerce=True)

        expect = num.coerce_vec([1,3,0,0,0,13])
        self.assertEqual(resolution, RESOLUTION_SOLVED)
        self.assertEqual(sol, expect)
        
    def testMMethodZeroRatios1(self):
        A = [[1,0,0,-1,0,0],
             [1,1,-1,0,1,0],
             [1,-3,3,0,0,1]]

        C = [1,1,-1,0,0,0]

        B = [1,5,0]

        basis = [None,None, None]
        #num = RealFiniteTolerance(eps=0.00001)
        num = RationalNumbers()

        resolution, sol = simplex_canonical_m(A,B,C, basis, num, verbose=False, do_coerce=True)

        expect = num.coerce_vec([1,Fraction(1,3),0,0,Fraction(11,3),0])
        self.assertEqual(resolution, RESOLUTION_SOLVED)
        self.assertEqual(sol, expect)

