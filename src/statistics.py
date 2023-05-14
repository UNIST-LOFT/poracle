import json
from os.path import join

data = dict()


def init(working_dir, config_file, poracle_config, config_data):
    data['file'] = join(working_dir, 'statistics.json')
    data['poracle_config'] = poracle_config
    data['config_file'] = config_file
    data['patch_ID'] = config_data['ID']
    data['apr_tool'] = config_data['tool']
    data['project'] = config_data['project']
    data['bug_ID'] = config_data['bug_id']
    data['label'] = config_data['correctness']
    data['model'] = []
    data['total_time'] = 'UNKNOWN'
    data['time'] = {'CHECKOUT_BUGGY': 0,
                    'DELTA_BUGGY': 0,
                    'COMPILE_BUGGY': 0,
                    'ZEST_BUGGY': 0,
                    'PREP_DATA': 0,
                    'SANITIZE_DATA': 0,
                    'TRAIN_MODEL': 0,
                    'CHECKOUT_PATCH': 0,
                    'PATCH': 0,
                    'DELTA_PATCH': 0,
                    'COMPILE_PATCH': 0,
                    'PATCH_TEST': 0,
                    'REPRO_FOR_FIND_DIFF': 0,
                    'FIND_DIFF': 0,
                    'PREP_DIFF_DATA': 0,
                    'SANITIZE_DIFF_DATA': 0,
                    'PREDICT': 0,
                    'CHECKOUT_FIX': 0,
                    'DELTA_FIX': 0,
                    'COMPILE_FIX': 0,
                    'REPRO_FOR_VALIDATE': 0,
                    'VALIDATE': 0}
    data['verdict'] = 'UNKNOWN'
    data['consistency'] = 'UNKNOWN'
    data['patch_plausible'] = 'UNKNOWN'
    data['test_results'] = []


def save():
    with open(data['file'], 'w') as output_file:
        json.dump(data, output_file, indent=2)
