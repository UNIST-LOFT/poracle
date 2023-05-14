#!/usr/bin/python

import os
import shutil, sys
from os.path import join
import json
from prettytable import PrettyTable

i = 0
iic_math = 0
iic_lang = 0
iic_time = 0
ici_math = 0
ici_lang = 0
ici_time = 0
iic_chart = 0
ici_chart = 0
array_of_results = []
array_of_time = []
array_of_labels = []
array_of_consistency = []
array_of_patch_name = []
array_of_subjects = []
path = os.getcwd()
directory_contents = os.listdir(path)

# print(directory_contents)
for item in directory_contents:
    if os.path.isdir(item):
        if "NFL" in item:
            continue
        # print (item)
        path = join(item, 'result')
        path2 = join(item, 'time')
        if os.path.exists(path):
            f = open(path, "r")
            # print(item + ' ' + f.read())
            b = f.read().rstrip("\n")
            f2 = open(path2, "r")
            # print(item + ' ' + f.read())
            b2 = f2.read().rstrip("\n").split(' ')[0]
            if b == '':
                b = 'N/A'
            i = i + 1
            array_of_results.append(b)
            array_of_time.append(b2)
            return_a = item.split('_');
            array_of_patch_name.append(item[:-5])
#print(array_of_patch_name)
print(array_of_results)
t = PrettyTable(['index' ,'Patch_Name', 'Result', 'Label', 'Consistency', 'Time'])
configs_dir = '/poracle-experiments/pfl_all/'
for i2 in range(0, len(array_of_patch_name)):
    name_of_config_file = array_of_patch_name[i2] + '.json'
    read_file = open(join(configs_dir, name_of_config_file), "r")
    data = json.load(read_file)
    correctness = data['correctness']
    array_of_labels.append(correctness)
    subject = data['project']
    array_of_subjects.append(subject)
    if correctness == array_of_results[i2]:
        array_of_consistency.append('C')
    elif array_of_results[i2] == 'N/A':
        array_of_consistency.append('N/A')
    else:
        array_of_consistency.append('I')
    Patch_Name = array_of_patch_name[i2]
    Result = array_of_results[i2]
    Label = array_of_labels[i2]
    subject_1 = array_of_subjects[i2]
    Consistency = array_of_consistency[i2]
    Time = array_of_time[i2]
    if data['project'] == "Math":
        if Result == "Incorrect" and Label == "Incorrect":
            iic_math = iic_math + 1
        elif Result == "Incorrect" and Label == "Correct":
            ici_math = ici_math + 1
    if data['project'] == "Lang":
        if Result == "Incorrect" and Label == "Incorrect":
            iic_lang = iic_lang + 1
        elif Result == "Incorrect" and Label == "Correct":
            ici_lang = ici_lang + 1
    if data['project'] == "Time":
        if Result == "Incorrect" and Label == "Incorrect":
            iic_time = iic_time + 1
        elif Result == "Incorrect" and Label == "Correct":
            ici_time = ici_time + 1
    if data['project'] == "Chart":
        if Result == "Incorrect" and Label == "Incorrect":
            iic_chart = iic_chart + 1
        elif Result == "Incorrect" and Label == "Correct":
            ici_chart = ici_chart + 1

    t.add_row([subject ,Patch_Name, Result, Label, Consistency, Time])
with open("result_patchsim.csv", "w") as f:
    f.write(t.get_csv_string())
print(f"Lang: iic = {iic_lang}, ici = {ici_lang}")
print(f"Math: iic = {iic_math}, ici = {ici_math}")
print(f"Time: iic = {iic_time}, ici = {ici_time}")
print(f"Chart: iic = {iic_chart}, ici = {ici_chart}")
#print(t.get_csv_string())
#print(t)

#print(i)
#print(i2)
#print(len(array_of_patch_name))
