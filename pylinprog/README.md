Linear Programming: Simplex Method
==================================

Pure python implementation of the [simplex method](https://en.wikipedia.org/wiki/Simplex_algorithm) solver for linear programming (LP) problem, supporting floating-point and exact rational computations.

In short, it solves constrained optimization problems, where objective function is linear, and is subject to a number of linear constraints, equalities and/or inequalities.
```
  c1 x1 + c2 x2 + .... + cn xn -> min

⎧ a11 x1 + a12 x2 + .... + a1n xn <= b1
⎪     ...
⎪ am1 x1 + am2 x2 + .... + amn xn <= b1
⎨ 
⎪ e11 x1 + e12 x2 + .... + e1n xn  = f1
⎪     ...
⎩ ek1 x1 + ek2 x2 + .... + ekn xn  = f1
```

Current implementation does not use dual method.

Usage
-----

The library provides one core function:

```python
def linsolve( c, ineq_left=(), ineq_right=(), eq_left=(), eq_right=(),
              nonneg_variables = None, num=None, verbose=False, do_coerce=True):
```
Arguments are:

* `c`: defines objective function. List of coefficients.
* `ineq_left`, `ineq_right` define set of inequality constraints.
* `eq_left`, `eq_right` define equality constraints
* `nonneg_variables`: list of variables (0-based integers) that are known to be non-negative.
* `num` Numeric system implementation (see "Generic computation" below). Defaults to finite-precision floating point numbers.

### Example
Consider the following problem: find maximum of the function `8x + 12y` given the constraints:

* `2x + 4y <= 440`
* `x/2 + y/4 <= 65`
* `2x + 2.5y <= 320`

This can be solved by the code:

```python
A = [[2,   4   ],
     [0.5, 0.25],
     [2,   2.5 ]]

C = [-8,   -14]

B = [440, 65, 320]

resolution, sol = linsolve( C, ineq_left = A, ineq_right = B))
```
The resolution is RESOLUTION_SOLVED, and solution is `[60, 80]`.
		
Features
--------

### Generic computation
An attempt is done to generalize calculations. This allows to solve LP problems, using arbitrary number implementations, not limiting to floating-point arithmetic. This is done by defining *Typeclass* objects, that are collections of methods for operations on numbers (naming inspired by Haskell).

Currently, 2 complete *Typeclasses* are provided:

* `RealFiniteTolerance` - uses `float` data type, default for numeric computations. This data type is imprecise by its nature, so tolerance value should be specified (default is 1e-6).
* `RationalNumbers` - performs exact, no rounding computations, using Python's built-in implementation of rational numbers: `feactions.Fraction`. Since computations are done exactly, no tolerance value is required.

Library user can define other type classes to work with different number implementations. Plausible extensions are **sympy** expressions, **mpl** numbers.

Each *Typeclass* is an object, providing a set of methods, defined in a base `NumberTypeclass`:

```python
class NumberTypeclass:
    def zero(self): return 0
    def one(self): return 1
    def positive(self,x): return x > 0
    def iszero(self,x): return x == 0
    def nonnegative(self,x): return self.positive(x) or self.iszero(x)
    def coerce(self, x): return x
    def coerce_vec(self, x): return [self.coerce(xi) for xi in x]
    def coerce_mtx(self, x): return [self.coerce_vec(xi) for xi in x]
```

Redefine them to return and operate on values or required numeric type.

The `coerce` method is provided for testing convenience and not actually required by the algorithm. It converts value of arbitrary type to the numeric type, supported by the *Typeclass*.

### Implementation details
The implementation is pure python, using lists internally. This could lead to inferior performance, when working with large-scale problems, using floating point numbers.

No serious attempt to optimize performance was done.
Usage Tips
----------

### Non-negative variables
When possible, specify which variables of the problem are known to be non-negative. This improves performance and reduces memory use, because each variable or arbitrary sign is replaced by a difference of 2 non-negative variables.

Known Problems
--------------

Currently, purely redundant conditions of the form:

```
0 x1 + 0 x2 + ... + 0 xn = 0
```

are not supported, algorithm incorrectly concludes that conditions are not satisfiable.
