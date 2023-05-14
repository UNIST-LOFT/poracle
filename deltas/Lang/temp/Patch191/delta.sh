#!/bin/bash

# This script is executed when the delta is applied.

root=$1

rm -rf ${root}/src/test/org/apache/commons/lang/enum
rm -f ${root}/src/test/org/apache/commons/lang/AllLangTestSuite.java
