#!/usr/bin/python3

import sys
import ast

from linprog import *

args = sys.argv
constraints = ast.literal_eval(args[1])
rhs = ast.literal_eval(args[2])
function = ast.literal_eval(args[3])
resolution, sol = linsolve(function, ineq_left=constraints,
                            ineq_right=rhs,
                           do_coerce=False,
                           num=RationalNumbers())

if resolution == RESOLUTION_SOLVED:
    for frac in sol:
        print(frac)
