#!/usr/bin/python
from datetime import datetime
import os
import shutil, sys
from os.path import join
import json

i = 0
configs_dir = '/poracle-experiments/extra_configs/'
for subroot, dirs, files in os.walk(configs_dir):
    # run('{}/cleanup'.format(root_dir))
    for config_file in files:
        read_file = open(join(configs_dir, config_file), "r")
        data = json.load(read_file)
        if data['correctness'] == 'Correct':
            continue
        str = data['project'] + ' ' + data['bug_id'] + ' ' + data['ID']
        cmd = 'timeout 3h python3 run.py ' + str
        print(cmd)
        #os.system(cmd)
        i = i + 1
        print(i)
    for config_file in files:
        read_file = open(join(configs_dir, config_file), "r")
        data = json.load(read_file)
        if data['correctness'] == 'Incorrect':
            continue
        str = data['project'] + ' ' + data['bug_id'] + ' ' + data['ID']
        cmd = 'timeout 3h python3 run.py ' + str
        print(cmd)
        #os.system(cmd)
        i = i + 1
        print(i)
now = datetime.now()

current_time = now.strftime("%H:%M:%S")
print("Current Time =", current_time)
