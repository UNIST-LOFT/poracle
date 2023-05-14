#!/usr/bin/python
import os
import json
from os.path import join
from sys import argv
#results_dir = os.getcwd()
i=0
argument = argv[1]
newpath =join(os.getcwd(), argument)
#print(newpath)
if os.path.exists(newpath):
                print("Directory already exists")
                exit(0)
os.mkdir(newpath)
for subdir, dirs, files in os.walk(r'/poracle-experiments/benchmark/APR-Efficiency/Patches/NFL'):
    for filename in files:
#         print(subdir)
        i=i+1
        filepath = subdir + os.sep + filename
        print filepath
        if filepath.endswith(".txt"):
            path = filepath
            x = (path + "/")
            tool = os.path.dirname(x).split('/')[6]
            sample_name = os.path.dirname(x).split('/')[7]
            y = sample_name.split('-')[0]
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
                C = "InCorrect"


            print i
            # returns first occurrence of Substring
            result = sample_name.find('Math')
            #print ("Substring 'Math' found at index:", result)
            subject = y




            # Open a file
            #file_name = tool+"_"+filename+"_"+"bug"+sample_name[length+1:len(sample_name) -2]+".json"
            new_file_name =  filename.rstrip('.txt')
            #print(new_file_name)
            file_name = str(i)+"_"+ tool+"_"+new_file_name+".json"
            #print(file_name)
            #path1 = '/poracle-experiments/newfolder/'
            config_file = join(newpath,file_name)
            #print(config_file)
            if os.path.exists(config_file):
                #print("File already exists")
                continue


            data = dict()
            data['ID'] = file_name
            data['project'] = subject
            data['bug_id'] = sample_name[length+1:len(sample_name) -2]
            data['correctness'] = C
            data['tool'] = tool
            data['target'] = 'empty'
            with open(config_file, 'w') as f:
                 json.dump(data, f)
     #return 0