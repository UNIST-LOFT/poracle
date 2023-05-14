## Bug Information

- Defect4J bug ID: Lang58
- The correct patch: Q4_Patch9.
- [The ground-truth generalized test](https://github.com/PLaSE-UNIST/poracle/blob/master/deltas/Lang/Lang_bug58/src/test/org/apache/commons/lang/math/JQF_NumberUtilsTest.java)

## Information Provided to the Participants

### The Method Under Testing (MUT)

`NumberUtils.createNumber`

### Bug Description

MUT throws an exception unexpectedly, while its reference implementation does not throw an exception.

### Domain Knowledge

- `NumberUtils.createNumber(inputStr).longValue()` should throw an exception when `NumberFormat.getInstance().parse(inputStr)` throws an exception.
- In the opposite case where `"NumberFormat.getInstance().parse(inputStr)` does not throw an exception, `NumberUtils.createNumber(inputStr).longValue()` should return the same value as `NumberFormat.getInstance().parse(inputStr)`.
