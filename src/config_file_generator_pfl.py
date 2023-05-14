#!/usr/bin/python
import os
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
for subdir, dirs, files in os.walk(r'/poracle-experiments/benchmark/APR-Efficiency/Patches/PFL'):
    for filename in files:
        #subdir = '/poracle-experiments/benchmark/APR-Efficiency/Patches/NFL/Cardumem'
        print(subdir)
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
                       #substring = 'src/main/java/org/apache/commons/math/analysis/solvers/BisectionSolverjava'
                       split_substring = substring.split('/')
                       last_part = split_substring[len(split_substring)-1] #BisectionSolverjava
                       #print last_part
                       new_substring = substring[0:len(substring) -4] + "." + "java"
                       split_new_substring = new_substring.split('/')
                       last_part_1 = split_new_substring[len(split_new_substring)-1]
                       #print last_part_1
                       #find = substring.find(last_part)
                       #print find
                       #fileToSearch = '/poracle-experiments/src/sampletext.txt'

                       # f = open('/poracle-experiments/src/sampletext.txt','rb+')
                       # for line in f:
                       #     line.replace(last_part_1,last_part)
                       #     f.write(line)
                       # f.close()
                       #read input file
                       fin = open(path, "rt")
                       #read file contents to string
                       data = fin.read()
                       #replace all occurrences of the required string
                       data = data.replace(last_part, last_part_1)
                       #close the input file
                       fin.close()
                       #open the input file in write mode
                       fin = open(path, "wt")
                       #overrite the input file with the resulting data
                       fin.write(data)
                       #close the file
                       fin.close()
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
                       #substring = 'src/main/java/org/apache/commons/math/analysis/solvers/BisectionSolverjava'
                       split_substring = substring.split('/')
                       last_part = split_substring[len(split_substring)-1] #BisectionSolverjava
                       #print last_part
                       new_substring = substring[0:len(substring) -4] + "." + "java"
                       split_new_substring = new_substring.split('/')
                       last_part_1 = split_new_substring[len(split_new_substring)-1]
                       #print last_part_1
                       #find = substring.find(last_part)
                       #print find
                       #fileToSearch = '/poracle-experiments/src/sampletext.txt'

                       # f = open('/poracle-experiments/src/sampletext.txt','rb+')
                       # for line in f:
                       #     line.replace(last_part_1,last_part)
                       #     f.write(line)
                       # f.close()
                       #read input file
                       fin = open(path, "rt")
                       #read file contents to string
                       data = fin.read()
                       #replace all occurrences of the required string
                       data = data.replace(last_part, last_part_1)
                       #close the input file
                       fin.close()
                       #open the input file in write mode
                       fin = open(path, "wt")
                       #overrite the input file with the resulting data
                       fin.write(data)
                       #close the file
                       fin.close()
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
            if y == 'Math' or y == 'Time':
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
            #result = sample_name.find('Math')
            #print ("Substring 'Math' found at index:", result)
            subject = y
            b_id = sample_name[length+1:len(sample_name) -2]
            poracle_path = "/poracle-experiments/.poracle_PFL"
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
            ID = str(i) + "_" +"PFL_"+ tool + "_" + new_file_name
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

            if os.path.exists(poracle_path2):
                shutil.rmtree(poracle_path2)
            cmd = 'defects4j checkout -p ' + subject + ' -v ' + b_id + 'b' + \
                                    ' -w ' + poracle_path2
            os.system(cmd)

#
            if tool in ['Cardumem','ARJA','GenProg-A','jGenProg','jKali','jMutRepair','Kali-A','RSRepair-A']:
                pvalue = '3'
            else:
                pvalue = '1'
            print path
            print self_dfj4_patch_dir
            # Apply_patch
            cmd = 'patch -p' + pvalue+ ' -d {}  < {}'.format(self_dfj4_patch_dir, path)
            print(cmd)
            #exit(1)
            #failure_list = "/poracle-experiments/benchmark/failure_list"+tool+".txt"
            code = os.system(cmd)
            if code == 0:
                print("applied patch============")
            else:
                print("Loop")
                f = open("/poracle-experiments/benchmark/failure_list_PFL.txt", "a")
                f.write(path +'\n')
                f.close()

                #open and read the file after the appending:
                #f = open("/poracle-experiments/benchmark/failure_list.txt", "r")
                #print(f.read())
                continue
            #print poracle_path1
#             for file in os.listdir(poracle_path1):
#                 if file.endswith(".java"):
#                     print(os.path.join(poracle_path1+ "/", file))
#                 else:
#                     print(file)

            with open("test101.txt", "a") as myfile:
                myfile.write(poracle_path)
            with open("test102.txt", "a") as myfile:
                myfile.write(poracle_path)
            for subdir1, dirs1, files in os.walk(poracle_path1):
                for filename in files:
                    filepath2 = subdir1 + os.sep + filename
                   # if filepath2.find(desired_file_name) != -1:
                    if filepath2.endswith(y2):
                        path2_origin = filepath2
                        path2_split = path2_origin.split('/')
                        #print path2_split
                        path2_version = path2_split[3]
                        #print join(poracle_path,path2_version,)
                        aftersplit = substring.split('/')
                        #print len(aftersplit)
                        #print (path2_split)
                        difference_of_length = len(path2_split) - len(aftersplit)
            for subdir2, dirs2, files in os.walk(poracle_path2):
                for filename in files:
               #         print(subdir)
                    filepath3 = subdir2 + os.sep + filename
                    #print(filename)
                    #desired_file_name = '/'+y2
                    if filepath3.endswith(y2):
                        path3_origin = filepath3
                        path3_split = path3_origin.split('/')
                        #print path3_split
                        path3_version = path3_split[3]
                        #print join (poracle_path,path3_version)
                        aftersplit1 = substring.split('/')
                        #print len(path3_split)
                        #print len(aftersplit1)
                        difference_of_length1 = len(path3_split) - len(aftersplit1)
                        #print difference_of_length1
            for x1 in range(3, difference_of_length):
                #looping = join(poracle_path,path2_split[x1])
                with open("test101.txt", "a") as myfile:
                    myfile.write("/"+path2_split[x1])
            with open("test101.txt", "a") as myfile:
                myfile.write("\n")
            with open("test101.txt", "r") as myfile:
                lines = myfile.readlines()
                first_line = (lines[0])
            strip_first_line = first_line.rstrip('\n')
            #print strip_first_line
            path2 = join(strip_first_line,new_substring)
            #print path2
            for x2 in range(3, difference_of_length1):
                #looping = join(poracle_path,path2_split[x2])
                with open("test102.txt", "a") as myfile:
                    myfile.write("/"+path3_split[x2])
            with open("test102.txt", "a") as myfile:
                myfile.write("\n")
            with open("test102.txt", "r") as myfile:
                lines = myfile.readlines()
                first_line1 = (lines[0])
            strip_first_line1 = first_line1.rstrip('\n')
            #print strip_first_line
            path3 = join(strip_first_line1,new_substring)
            #print path3
            cmd = 'diff {} {} > log50.txt'.format(path2,path3)
            print cmd
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
                        if sub500.find(',') != -1:
                            linenumber = sub500.split(',')[0]
                        else:
                            linenumber = sub500
                   elif line.find('a') != -1:
                        result = line.find('a')
                        sub500 = line[result+1:]
                        if sub500.find(',') != -1:
                             linenumber = sub500.split(',')[0]
                        else:
                             linenumber = sub500
                   elif line.find('d') != -1:
                        result = line.find('d')
                        sub500 = line[:result]
                        if sub500.find(',') != -1:
                              linenumber = sub500.split(',')[0]
                        else:
                              linenumber = sub500

                   new_substring2 = (new_substring +":" +linenumber)
                   new_substring1 = new_substring2.rstrip('\n')
                   array_of_targets.append(new_substring1)
                   print array_of_targets
                #print("{}: {}".format(count, line.strip()))
                count  += 1
            print ('array_of_targets')
            print(array_of_targets)
            data = dict()
            data['ID'] = ID
            data['project'] = subject
            data['bug_id'] = b_id
            data['correctness'] = C
            data['tool'] = tool
            data['target'] = array_of_targets
            with open(config_file, 'w') as f:
                 json.dump(data, f)
            cmd = 'cp '+ path + ' '+'/poracle-experiments/ext_patches/' + ID
            os.system(cmd)
            cmd = 'rm test101.txt'
            os.system(cmd)
            cmd = 'rm test102.txt'
            os.system(cmd)
     #return 0
