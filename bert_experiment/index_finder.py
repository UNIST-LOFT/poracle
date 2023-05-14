#!/usr/bin/python

import os
import shutil, sys
from os.path import join
import json
# from prettytable import PrettyTable
import pandas as pd


def extract_result(filename, indexes, correctnesses, subjects,specified=-1):
    df = pd.read_csv(filename, header=None)
    c_all = {"Chart": 0, "Lang": 0, "Math": 0, "Time": 0}
    in_all = {"Chart": 0, "Lang": 0, "Math": 0, "Time": 0}
    in_all_correct = {"Chart": 0, "Lang": 0, "Math": 0, "Time": 0}
    for t in range(len(correctnesses)):
        res = int(df[df[df.columns[0]] == indexes[t]][df.columns[1]])
        if res == correctnesses[t]:
            if specified == t:
                print("The result for specified patch is consistent")
                print(f"Line number is {indexes[t]} (line number starts from zero)")
            # if subjects[t] == 'Lang':
            #     print(indexes[t])
            c_all[subjects[t]] += 1
        else:
            if specified == t:
                print("The result for specified patch is inconsistent")
            if correctnesses[t] == 1:
                in_all_correct[subjects[t]] += 1
                # if subjects[t] == 'Lang':
                #     print(indexes[t])

            in_all[subjects[t]] += 1
    return c_all, in_all, in_all_correct


def start_process(path, csv_file, model_info, sw=0,arg=None):
    i = 0
    specified_index = -1
    indexes = []
    correctnesses = []
    subjects = []
    for subdir, dirs, files in os.walk(path):
        for filename in files:
            if filename.endswith(".json"):
                if "PFL" in filename or "bug" in filename or filename.startswith("Patch"):
                    continue
                f = open(join(path, filename), 'r')
                pattern1 = ""
                data = json.load(f)
                m = data['ID']
                n = m.split('_')
                o = f"{n[len(n) - 3]}_{n[len(n) - 2]}_{n[len(n) - 1]}"
                # print(o)
                pattern1 = f"{data['tool']}_{data['project']}-{data['bug_id']}_{'P' if data['correctness'] == 'Incorrect' else 'C'}_{o}"
                pattern2 = f"{data['project']}_{data['bug_id']}.src.patch"
                pattern3 = f"{data['project']}{data['bug_id']}b_{data['ID']}"
                switch = {0: pattern1, 1: pattern2, 2: pattern3}
                pattern = switch[sw]
                exists = False
                for j in range(len(model_info)):
                    if pattern in model_info[j]:
                        i = i + 1
                        exists = True
                        indexes.append(j)

                        correctnesses.append(0 if data['correctness'] == 'Incorrect' else 1)
                        if arg == data['ID']:
                            specified_index = len(correctnesses)-1
                        subjects.append(data['project'])
                if not exists:
                    print(filename)
                    # if data['project'] == 'Math':
                    #     if data['correctness'] == 'Incorrect':
                    #         math_inc += 1
                    #     else:
                    #         math_c += 1
                    # elif data['project'] == 'Lang':
                    #     if data['correctness'] == 'Incorrect':
                    #         lang_inc += 1
                    #     else:
                    #         lang_c += 1
                    # os.system(f"mv {join(path, filename)} ../duplicate_patches/")
    return extract_result(csv_file, indexes, correctnesses, subjects, specified_index)

import sys
if __name__ == "__main__":
    path = '../configs/'
    f = open("kui_data_for_cc2v.txt", 'r')
    model_info = f.readlines()
    file = 'cv_prediction_result_th_219.csv'
    c_all, in_all, in_all_correct = start_process(path, file, model_info,0,sys.argv[1])
    # print(f"Consistent: {c_all}")
    # print(f"InConsistent: {in_all}")
    # print(f"Incorrectly rejected correct patch: {in_all_correct}")
