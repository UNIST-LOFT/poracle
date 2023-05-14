#!/usr/bin/env python3

import logging
import csv
import os
from os.path import join
import json
import pandas as pd
import argparse

failures_dir = join(os.getcwd(), 'failures')
result_file = join(os.getcwd(), 'result.csv')
temp_file = join(os.getcwd(), 'temp.csv')

logger = logging.getLogger('summary')


def extract_result():
    rows = []
    for root1, exp_ids, files in os.walk(results_dir):
        for exp_id in exp_ids:
            for root2, exp_nums, _ in os.walk(join(root1, exp_id)):
                for exp_num in exp_nums:
                    for root3, results, _ in os.walk(join(root2, exp_num)):
                        for result in results:
                            stat_file = join(root3, result, 'statistics.json')
                            rows.append(handle(stat_file, int(exp_num)))
    # for root1, exp_ids, files in os.walk(failures_dir):
    #     for exp_id in exp_ids:
    #         for root2, exp_nums, _ in os.walk(join(root1, exp_id)):
    #             for exp_num in exp_nums:
    #                 for root3, results, _ in os.walk(join(root2, exp_num)):
    #                     for result in results:
    #                         stat_file = join(root3, result, 'statistics.json')
    #                         rows.append(handle(stat_file))
    return rows


def handle(stat_file, exp_num):
    with open(stat_file) as f:
        data = json.load(f)
        return [data['project'],
                data['bug_ID'],
                data['patch_ID'],
                data['label'],
                data['verdict'],
                data['consistency'],
                sum(list(data['time'].values())[3:])]


if __name__ == "__main__":
    rootLogger = logging.getLogger()
    FORMAT = logging.Formatter('%(levelname)-8s %(name)-15s %(message)s')
    rootLogger.setLevel(logging.INFO)
    consoleHandler = logging.StreamHandler()
    consoleHandler.setFormatter(FORMAT)
    rootLogger.addHandler(consoleHandler)

    parser = argparse.ArgumentParser('summary')
    parser.add_argument('results_dir', metavar='RESULTS_DIR', help='results directory', type=str)
    args = parser.parse_args()
    results_dir = join(os.getcwd(), args.results_dir)

    head = ['project', 'bug_ID', 'patch_ID', 'label', 'verdict', 'consistency','time']

    rows = extract_result()

    with open(temp_file, 'w', newline='') as csvfile:
        w = csv.writer(csvfile, delimiter=',')
        w.writerow(head)
        for row in rows:
            # if row[0] != 'Lang':
            #     continue
            w.writerow(row)

    df = pd.read_csv(temp_file)
    df = df.sort_values(by=['project', 'bug_ID', 'patch_ID'])
    df.to_csv(result_file, index=False)
    os.remove(temp_file)

    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    pd.set_option('display.width', None)
    pd.set_option('display.max_colwidth', None)
    print(df.to_string(index=False))
