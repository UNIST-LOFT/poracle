#!/usr/bin/python

import os
import shutil, sys
from os.path import join
import json
from prettytable import PrettyTable

i = 0
array_of_results1 = []
array_of_results2 = []
array_of_results3 = []
array_of_results4 = []
array_of_results5 = []
array_of_results6 = []
array_of_results7 = []
array_of_results8 = []
array_of_results9 = []
array_of_results10 = []
array_of_labels = []
array_of_consistency = []
array_of_patch_name = []
#path = '/poracle-experiments/benchmark/experiment_10/DefectRepairing/tool/source/'
# Using readline()
file1 = open('ma.txt', 'r')
count = 0

while True:
    count += 1

    # Get next line from file
    line = file1.readline()

    # if line is empty
    # end of file is reached
    if not line:
        break
    #print("Line{}: {}".format(count, line.strip()))
    array_of_patch_name.append(line.strip())
file1.close()
print(array_of_patch_name)
for i1 in range(0, len(array_of_patch_name)):
    for j1 in range(1, 11):
        p = array_of_patch_name[i1]+ '_exp'+ str(j1)
        #print(p)
        path = join(p, 'result')
        if os.path.exists(path):
            f = open(path, "r")
            # print(item + ' ' + f.read())
            b = f.read().rstrip("\n")
            if b == '':
                b = 'N/A'
            #i = i + 1
            if p.endswith('1'):
                array_of_results1.append(b)
            elif p.endswith('2'):
                array_of_results2.append(b)
            elif p.endswith('3'):
                array_of_results3.append(b)
            elif p.endswith('4'):
                array_of_results4.append(b)
            elif p.endswith('5'):
                array_of_results5.append(b)
            elif p.endswith('6'):
                array_of_results6.append(b)
            elif p.endswith('7'):
                array_of_results7.append(b)
            elif p.endswith('8'):
                array_of_results8.append(b)
            elif p.endswith('9'):
                array_of_results9.append(b)
            elif p.endswith('10'):
                array_of_results10.append(b)
        else:
            if p.endswith('1'):
                array_of_results1.append('Processing')
            elif p.endswith('2'):
                array_of_results2.append('Processing')
            elif p.endswith('3'):
                array_of_results3.append('Processing')
            elif p.endswith('4'):
                array_of_results4.append('Processing')
            elif p.endswith('5'):
                array_of_results5.append('Processing')
            elif p.endswith('6'):
                array_of_results6.append('Processing')
            elif p.endswith('7'):
                array_of_results7.append('Processing')
            elif p.endswith('8'):
                array_of_results8.append('Processing')
            elif p.endswith('9'):
                array_of_results9.append('Processing')
            elif p.endswith('10'):
                array_of_results10.append('Processing')
t = PrettyTable(['Patch_Name', 'exp1','exp2','exp3','exp4','exp5','exp6','exp7','exp8','exp9','exp10'])
for i2 in range(0, len(array_of_patch_name)):


    Patch_Name = array_of_patch_name[i2]
    Result1 = array_of_results1[i2]
    Result2 = array_of_results2[i2]
    Result3 = array_of_results3[i2]
    Result4 = array_of_results4[i2]
    Result5 = array_of_results5[i2]
    Result6 = array_of_results6[i2]
    Result7 = array_of_results7[i2]
    Result8 = array_of_results8[i2]
    Result9 = array_of_results9[i2]
    Result10 = array_of_results10[i2]
    t.add_row([Patch_Name, Result1,Result2,Result3,Result4,Result5,Result6,Result7,Result8,Result9,Result10])
with open("result_patchsim.csv", "w") as f:
    f.write(t.get_csv_string())