#!/usr/bin/python

import os
import shutil, sys
from os.path import join
import json
from prettytable import PrettyTable

i = 0
array_of_results = []
array_of_labels = []
array_of_consistency = []
array_of_patch_name = []
path = '/poracle-experiments/benchmark/experiment_9/DefectRepairing/tool/source/'
directory_contents = os.listdir(path)

# print(directory_contents)
for item in directory_contents:
    if os.path.isdir(item):

        # print (item)
        path = join(item, 'result')
        if os.path.exists(path):
            f = open(path, "r")
            # print(item + ' ' + f.read())
            b = f.read().rstrip("\n")
            if b == '':
                b = 'N/A'
            i = i + 1
            array_of_results.append(b)
            array_of_patch_name.append(item)
#print(array_of_patch_name)
print(array_of_results)
t = PrettyTable(['index' ,'Patch_Name', 'Result', 'Label', 'Consistency'])
configs_dir = '/poracle-experiments/extra_configs/'
for i2 in range(0, len(array_of_patch_name)):
    name_of_config_file = array_of_patch_name[i2] + '.json'
    read_file = open(join(configs_dir, name_of_config_file), "r")
    data = json.load(read_file)
    correctness = data['correctness']
    array_of_labels.append(correctness)
    if correctness == array_of_results[i2]:
        array_of_consistency.append('C')
    elif array_of_results[i2] == 'N/A':
        array_of_consistency.append('N/A')
    else:
        array_of_consistency.append('I')
    Patch_Name = array_of_patch_name[i2]
    Result = array_of_results[i2]
    Label = array_of_labels[i2]
    Consistency = array_of_consistency[i2]
    t.add_row([i2 ,Patch_Name, Result, Label, Consistency])
with open("result_patchsim.csv", "w") as f:
    f.write(t.get_csv_string())
#print(t.get_csv_string())
#print(t)

#print(i)
#print(i2)
#print(len(array_of_patch_name))
