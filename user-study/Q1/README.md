## Bug Information

- Defect4J bug ID: Math73
- The correct patch: Q1_Patch8.
- [The ground-truth generalized test](https://github.com/PLaSE-UNIST/poracle/blob/master/deltas/Math/Math_bug73/src/test/java/org/apache/commons/math/analysis/solvers/JQF_BrentSolverTest.java)

## Information Provided to the Participants

### The Method Under Testing (MUT)

`solver.solve`

### Bug Description

When yMin, yMax, and yInitial have the same sign value (i.e., either all three values are positive or all three values are negative), MUT (method under test) should always throw an IllegalArgumentException. However, in the original version, IllegalArgumentException sometimes was not thrown.

### Domain Knowledge

- According to the API documentation of the `solve` method, `IllegalArgumentException` should be thrown if `yMin`, `yMax`, and `yInitial` have the same sign value.
- Otherwise, the same value should be returned by MUT.
