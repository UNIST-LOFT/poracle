#!/usr/bin/python3
from numpy import array
from numpy.linalg import eig
from scipy.sparse import diags
import numpy as np
import sys
import ast

# define matrix
args = sys.argv
maind = ast.literal_eval(args[3])
second = ast.literal_eval(args[4])
k = array([
    second, maind, second
])
offset = [-1, 0, 1]
A = diags(k, offset).toarray()
values, vectors = eig(A)
for val in values:
    print(val)
