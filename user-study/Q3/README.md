## Bug Information

- Defect4J bug ID: Math28
- The correct patch: Q3_Patch10.
- [The ground-truth generalized test](https://github.com/PLaSE-UNIST/poracle/blob/master/deltas/Math/Math_bug28/src/test/java/org/apache/commons/math3/optimization/linear/JQF_SimplexSolverTest.java)

## Information Provided to the Participants

### The Method Under Testing (MUT)

`SimplexSolver().optimize`

### Bug Description

An exception is thrown unexpectedly when the test is run.

### Domain Knowledge

- When MUT does not throw an exception, MUT returns the correct value in the original version. Thus, in the patched version, the same correct value should be returned by MUT.
