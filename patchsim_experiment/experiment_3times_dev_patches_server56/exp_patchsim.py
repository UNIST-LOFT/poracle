#!/usr/bin/python
from datetime import datetime
import os
import shutil, sys
from os.path import join
import json
import time
i = 0
configs_dir = '/poracle-experiments/configs/'
for subroot, dirs, files in os.walk(configs_dir):
    # run('{}/cleanup'.format(root_dir))
    for config_file in files:
        read_file = open(join(configs_dir, config_file), "r")
        data = json.load(read_file)
        if (config_file.startswith('Patch') == True): continue
        #if data['correctness'] == 'Correct':
            #continue
        str1 = data['project'] + ' ' + data['bug_id'] + ' ' + data['ID']
        for j in range(1,5):
            cmd = 'timeout 10m python3 run.py ' + str1
            print(cmd)
            os.system(cmd)
            time.sleep(1)
            cmd = 'mv ' + data['ID'] + ' '+ data['ID'] + '_exp'+ str(j)
            os.system(cmd)
            time.sleep(1)
            cmd = 'rm -r ../traces/'
            os.system(cmd)
            time.sleep(1)
            cmd = 'rm -r ../test_coverage/'
            os.system(cmd)
            cmd = 'rm -r ../test_gen_randoop/' + data['project']
            os.system(cmd)
            cmd = 'rm -r ../test_gen_randoop/logs'
            os.system(cmd)
            time.sleep(1)
            print('j == ' + str(j))
        i = i + 1
        print('i == ' + str(i))
#     for config_file in files:
#         read_file = open(join(configs_dir, config_file), "r")
#         data = json.load(read_file)
#         if data['correctness'] == 'Incorrect':
#             continue
#         str = data['project'] + ' ' + data['bug_id'] + ' ' + data['ID']
#         cmd = 'timeout 2h python3 run.py ' + str
#         print(cmd)
#         os.system(cmd)
#         i = i + 1
#         print(i)
# now = datetime.now()
#
# current_time = now.strftime("%H:%M:%S")
# print("Current Time =", current_time)

