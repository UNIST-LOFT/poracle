#!/usr/bin/python
import os
import pdb
import re
import json
import shutil, sys
from os.path import join
from sys import argv
#results_dir = os.getcwd()
i = 0
argument = argv[1]
newpath = join(os.getcwd(), argument)
#print(newpath)
if os.path.exists(newpath):
                print("Directory already exists")
                exit(0)
os.mkdir(newpath)
for subdir, dirs, files in os.walk(r'/poracle-experiments/benchmark/APR-Efficiency/Patches/NFL/jMutRepair/Closure-63_C'):
    for filename in files:
#         print(subdir)
        i += 1
        filepath = subdir + os.sep + filename
        #print(filename)
        if filepath.endswith(".txt"):
            path = filepath
            with open(path, 'r') as file:
                info = file.read()
            #print(info)
            x = (path + "/")
            tool = os.path.dirname(x).split('/')[6]
            sample_name = os.path.dirname(x).split('/')[7]
            y = sample_name.split('-')[0]
            substring = 'empty'
            if y == 'Closure':
                result1 = info.find('com')
                #print ("Substring 'org' found at index:", result1 )
                #result = info.find('.java', result1)
                needle = "java"
                result_t = []
                for i1, _ in enumerate(info):
                     if info[i1:i1 + len(needle)] == needle:
                          t = i1
                          result_t.append(t)

                result = result_t[1]
                #print ("Substring 'java' found at index:", result )
                substring = info[result1:result+4]
                if substring[-5] != '.':
                       new_substring = substring[0:len(substring) -4] + "." + "java"
                else:
                    new_substring = substring
                #print new_substring
                #print (substring[0:len(substring) -4] + "   " +y)
                #print substring
                #print (y)
            else:
                result1 = info.find('org')
                #print ("Substring 'org' found at index:", result1 )
                result = info.find('java', result1)
                #print ("Substring 'java' found at index:", result )
                substring = info[result1:result+4]
                if substring[-5] != '.':
                    new_substring = substring[0:len(substring) -4] + "." + "java"
                else:
                    new_substring = substring
                #print new_substring
                #print (substring[0:len(substring) -4] + "   " +y )
            #x1 = (new_substring + "/")
            #print x1
            y1 = new_substring.split('/')
            #print y1
            len_of_y1 = len(y1)
            y2 = y1[len_of_y1-1]
            #print i
            #print substring
            #print (y)
            if y == 'Math':
                length = 4

            elif y == 'Lang':
                length = 4

            elif y == 'Chart':
                length = 5

            elif y == 'Closure':
                length = 7

            elif y == 'Mockito':
                length = 7
            last_char = sample_name[-1]
            #print(last_char)
            #if last_char() == 'C':
            #    print("Correct")
            #else:
            #    print("InCorrect")
            if last_char == 'C':
                C = "Correct"
            else:
                C = "Incorrect"


            # returns first occurrence of Substring
            result = sample_name.find('Math')
            #print ("Substring 'Math' found at index:", result)
            subject = y
            b_id = sample_name[length+1:len(sample_name) -2]
            poracle_path = "/poracle-experiments/.poracle"
            poracle_path_postfix = subject + b_id + 'b'
            poracle_path_postfix2 = subject + b_id + 'p'
            poracle_path1 = join(poracle_path,poracle_path_postfix)
            poracle_path2 = join(poracle_path,poracle_path_postfix2)
            self_patch_dir_name = subject + b_id + 'p'
            self_dfj4_patch_dir = join(poracle_path, self_patch_dir_name)
            # Open a file
            #file_name = tool+"_"+filename+"_"+"bug"+sample_name[length+1:len(sample_name) -2]+".json"
            new_file_name =  filename.rstrip('.txt')
            #print(new_file_name)
            ID = str(i) + "_" + tool + "_" + new_file_name
            file_name = ID + ".json"
            #print(file_name)
            #path1 = '/poracle-experiments/newfolder/'
            config_file = join(newpath,file_name)
            #print(config_file)
            if os.path.exists(config_file):
                #print("File already exists")
                continue





            #checkout_buggy
            if os.path.exists(poracle_path1):
                    shutil.rmtree(poracle_path1)
            cmd = 'defects4j checkout -p ' + subject + ' -v ' + b_id + 'b' + \
                        ' -w ' + poracle_path1
            os.system(cmd)


            if os.path.exists(poracle_path1):
                        shutil.rmtree(poracle_path2)
            cmd = 'defects4j checkout -p ' + subject + ' -v ' + b_id + 'b' + \
                                    ' -w ' + poracle_path2
            os.system(cmd)

#
            print path
            print self_dfj4_patch_dir
             #Apply_patch
            #pdb.set_trace()
            cmd = 'patch -p1 -d {}  < {}'.format(self_dfj4_patch_dir, path)
            code = os.system(cmd)
            if code==0:
                print("applied patch============")
            else:
                print("Loop")
                f = open("/poracle-experiments/benchmark/failure_list.txt", "a")
                f.write(path +'\n')
                f.close()

                #open and read the file after the appending:
                f = open("/poracle-experiments/benchmark/failure_list.txt", "r")
                print(f.read())
                continue
            #print poracle_path1
#             for file in os.listdir(poracle_path1):
#                 if file.endswith(".java"):
#                     print(os.path.join(poracle_path1+ "/", file))
#                 else:
#                     print(file)
            for subdir, dirs, files in os.walk(poracle_path1):
                for filename in files:
            #         print(subdir)
                    filepath2 = subdir + os.sep + filename
        #print(filename)
                    if filepath2.endswith(y2):
                        path2 = filepath2
                        #print path2


            for subdir, dirs, files in os.walk(poracle_path2):
                for filename in files:
               #         print(subdir)
                    filepath3 = subdir + os.sep + filename
                    #print(filename)
                    if filepath3.endswith(y2):
                        path3 = filepath3
                        #print path2
            cmd = 'diff {} {} > log50.txt'.format(path2,path3)
            os.system(cmd)

            #searching for matching patter after diff
            pattern = '[0-9a-f0-9]'
            # Using readlines()
            file1 = open('log50.txt', 'r')
            Lines = file1.readlines()
            array_of_targets = []
            count = 0
            # Strips the newline character
            for line in Lines:
                result = re.match(pattern, line)
                if result:
                   if line.find('c') != -1:
                        result = line.find('c')
                        sub500 = line[result+1:]
                   elif line.find('a') != -1:
                        result = line.find('a')
                        sub500 = line[result+1:]
                   elif line.find('d') != -1:
                        result = line.find('d')
                        sub500 = line[:result]
                   new_substring2 = (new_substring +":" +sub500)
                   new_substring1 = new_substring2.rstrip('\n')

                   array_of_targets.append(new_substring1)
                   #print array_of_targets
                #print("{}: {}".format(count, line.strip()))
                count  += 1

            data = dict()
            data['ID'] = ID
            data['project'] = subject
            data['bug_id'] = b_id
            data['correctness'] = C
            data['tool'] = tool
            data['target'] = array_of_targets
            with open(config_file, 'w') as f:
                 json.dump(data, f)
     #return 0
