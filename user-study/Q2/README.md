## Bug Information

- Defect4J bug ID: Math105
- The correct patch: Q2_Patch7.
- [The ground-truth generalized test](https://github.com/PLaSE-UNIST/poracle/blob/master/deltas/Math/Math_bug105/src/test/org/apache/commons/math/stat/regression/JQF_SimpleRegressionTest.java)

## Information Provided to the Participants

### The Method Under Testing (MUT)

`reg.getSumSquaredErrors`

### Bug Description

MUT should always return a non-negative value. However, in the original version, MUT sometimes returned a negative value.

### Domain Knowledge

- When MUT returns a **non-negative** value in the original version, the return value is correct. In the patched version, MUT should return the same correct value.
