import logging
import subprocess
import os
import shutil
from os import chmod, walk
from os.path import join, isdir, isfile, exists, basename
from shutil import copyfile
import sys
import tempfile
import pathlib
from concurrent.futures import ThreadPoolExecutor
from functools import reduce
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import threading
import time
import stat
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.layers import Dense
from enum import Enum
import statistics as st
from typing import Dict
import random

logger = logging.getLogger("poracle")
out_log_file = 'OUT.log'
in_log_file = 'IN.log'


class Judge(Enum):
    ACCEPT = 0
    REJECT = 1
    UNCLEAR = 2
    ERROR = 3

    def char(judge):
        if judge == Judge.ACCEPT:
            return 'A'
        elif judge == Judge.REJECT:
            return 'R'
        else:
            raise PoracleException('Unsupported judge item: {}'.format(judge))


class Validate(Enum):
    MATCH = 0
    MISMATCH = 1

    def char(val):
        if val == Validate.MATCH:
            return 'C'  # consistent
        elif val == Validate.MISMATCH:
            return 'I'  # inconsistent
        else:
            raise PoracleException('Unsupported enum item: {}'.format(val))


class TestUnit:

    def __init__(self, desc):
        self.desc = desc
        cls, self.mthd = desc.split('::')
        self.cls = self.get_jqf_cls(cls)

    def get_desc(self):
        return self.desc

    def get_cls(self):
        return self.cls

    def get_mthd(self):
        return self.mthd

    def get_jqf_cls(self, cls):
        names = cls.split('.')
        names[len(names) - 1] = 'JQF_' + names[len(names) - 1]
        return '.'.join(names)

    def set_test_id(self, id):
        self.test_id = id

    def get_test_id(self) -> str:
        assert self.test_id is not None
        return self.test_id


class PoracleException(Exception):
    def __init__(self, msg):
        self.msg = msg

    def get_msg(self):
        return self.msg


class Poracle:

    def __init__(self, config, workdir, deltas_dir, project,
                 bug_id, patch_id, target, delta_allowed, correctness, threadName):
        self.config = config
        self.workdir = workdir
        self.project = project
        self.bug_id = bug_id
        self.patch_id = patch_id
        self.correctness = correctness
        self.threadName = threadName
        self.target = target
        self.delta_allowed = delta_allowed
        self.buggy_dir_name = project + bug_id + 'b'
        self.patch_dir_name = project + bug_id + 'p'
        self.fix_dir_name = project + bug_id + 'f'
        if config['verbose']:
            self.subproc_output = sys.stderr
        else:
            self.subproc_output = subprocess.DEVNULL

        self.deltas_dir = deltas_dir
        if not isdir(self.deltas_dir):
            raise PoracleException('{} does not exist'.format(self.deltas_dir))

        self.patches_dir = join(workdir, "..", "patches")
        if not isdir(self.patches_dir):
            raise PoracleException('{} does not exist'.format(self.patches_dir))

        self.dfj4_buggy_dir = join(workdir, self.buggy_dir_name)
        self.dfj4_patch_dir = join(workdir, self.patch_dir_name)
        self.dfj4_fix_dir = join(workdir, self.fix_dir_name)
        self.fuzz_out_dir = join(workdir, 'fuzz-results')

        self.data_dir = self.prep_dir(join(workdir, 'data'))
        self.data_patch_dir = self.prep_dir(join(self.data_dir, 'patch'))
        self.data_orig_dir = self.prep_dir(join(self.data_dir, 'orig'))
        self.data_buggy_dir = self.prep_dir(join(self.data_dir, 'buggy'))

        self.log_dir = self.prep_dir(join(workdir, 'log'))
        self.log_buggy_dir = self.prep_dir(join(self.log_dir, 'buggy'))
        self.log_patch_dir = self.prep_dir(join(self.log_dir, 'patch'))
        self.log_orig_dir = self.prep_dir(join(self.log_dir, 'orig'))
        self.log_expected_dir = self.prep_dir(join(self.log_dir, 'expected'))

        self.val_dir = self.prep_dir(join(workdir, 'validation'))
        self.val_patch_dir = self.prep_dir(join(self.val_dir, 'patch'))
        self.val_fix_dir = self.prep_dir(join(self.val_dir, 'fix'))

        self.model_dir = self.prep_dir(join(workdir, 'model'))
        self.input_dir = self.prep_dir(join(workdir, 'input'))

    def __call__(self):
        self.diff_output_found = False
        judges = []
        matches = []
        predict_stat = dict()

        # To make the system more deterministic, we start with a fixed seed
        random.seed(0)

        if 'CHECKOUT_BUGGY' not in self.config['skip']:
            start_time = time.time()
            logger.info('Check out (buggy) {} {}'.format(self.project, self.bug_id))
            code = self.checkout_buggy(self.project, self.bug_id)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['CHECKOUT_BUGGY'] = elapsed_time
            if code != 0:
                raise PoracleException('check-out-buggy failed')

        if 'DELTA_BUGGY' not in self.config['skip']:
            start_time = time.time()
            logger.info('Apply deltas for buggy')
            self.apply_deltas_root(self.project, self.bug_id,
                                   self.dfj4_buggy_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['DELTA_BUGGY'] = elapsed_time

        if 'COMPILE_BUGGY' not in self.config['skip']:
            start_time = time.time()
            logger.info('Compile the buggy version')
            code = self.compile(self.dfj4_buggy_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['COMPILE_BUGGY'] = elapsed_time
            if code != 0:
                raise PoracleException('compiling buggy src failed')

        if self.set_failing_tests() != 0:
            raise PoracleException('Failed to retrieve a faling test')

        assert self.failing_tests is not None
        for ft in self.failing_tests:
            logger.info('Failing test: {}'.format(ft))

        self.set_test_units()
        assert len(self.test_units) > 0
        if not self.exists_JQF_classes(self.test_units):
            raise PoracleException('A JQF class does not exist')

        self.set_buggy_project_cp()
        assert self.buggy_project_cp is not None

        self.set_patch_project_cp()
        assert self.patch_project_cp is not None

        self.set_fix_project_cp()
        assert self.fix_project_cp is not None

        if 'CHECKOUT_PATCH' not in self.config['skip']:
            start_time = time.time()
            code = self.checkout_patch(self.project, self.bug_id)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['CHECKOUT_PATCH'] += elapsed_time
            if code != 0:
                raise PoracleException('check-out failed (patch)')

        if 'DELTA_PATCH' not in self.config['skip']:
            start_time = time.time()
            logger.info('Apply deltas for patch')
            self.apply_deltas_root(self.project, self.bug_id,
                                   self.dfj4_patch_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['DELTA_PATCH'] += elapsed_time

        if 'PATCH' not in self.config['skip']:
            start_time = time.time()
            patch_file = join(self.patches_dir, self.patch_id)
            code = self.apply_patch(patch_file)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['PATCH'] += elapsed_time
            if code != 0:
                cmd = 'patch -p1 -d {}  < {}'.format(self.dfj4_patch_dir,
                                                     patch_file)
                raise PoracleException('apply-patch failed (cmd: {})'.format(cmd))
        if 'COMPILE_PATCH' not in self.config['skip']:
            start_time = time.time()
            logger.info('Compile the patched version')
            code = self.compile(self.dfj4_patch_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['COMPILE_PATCH'] += elapsed_time
            if code != 0:
                raise PoracleException('compiling patch src failed')

        patch_plausible = True
        if 'PATCH_TEST' not in self.config['skip']:
            start_time = time.time()
            logger.info('Check whether the patch passes the test')
            code = self.patch_test(self.dfj4_patch_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['PATCH_TEST'] += elapsed_time
            if code != 0:
                logger.warning('patch test failed')
                patch_plausible = False
                judges.append(Judge.REJECT)
                matches.append(Validate.MATCH)
            else:
                patch_plausible = True

        if patch_plausible:
            for test_unit in self.test_units:
                test_id = test_unit.get_test_id().replace('test', '')
                if test_id in self.config['skip_tests']:
                    logger.info('skip test ID: {}'.format(test_id))
                    continue
                logger.info('Handle test unit: {}'.format(test_unit.get_desc()))
                judge, predict_stat = self.test_patch(test_unit)

                # if 'patch_pass' in predict_stat and not predict_stat['patch_pass']:
                #     judges.append(judge)
                #     matches.append(Validate.MATCH)
                #     test_stat = {'test_ID': test_id, 'judge': Judge.char(judge),
                #                  'consistency': Validate.char(Validate.MATCH)}
                #     test_stat = {**test_stat, **predict_stat}
                #     st.data['test_results'].append(test_stat)
                #     break

                # if 'PREDICT' not in self.config['skip'] and judge is None:
                #     raise PoracleException('judge should not be None')
                # if 'PREDICT' not in self.config['skip'] and predict_stat is None:
                #     raise PoracleException('predict_stat should not be None')
                match = ''
                val_stat = {}
                if judge == judge.ACCEPT and self.correctness == 'Incorrect':
                    self.repro_for_validate(test_unit, judge)
                    match = Validate.MISMATCH
                else:
                    match, val_stat = self.validate_judge(test_unit, judge, predict_stat)
                if match is None:
                    raise PoracleException('match should not be None')
                if val_stat is None:
                    raise PoracleException('val_stat should not be None')

                matches.append(match)
                judges.append(judge)
                test_stat = {'test_ID': test_id, 'judge': Judge.char(judge),
                             'consistency': Validate.char(match)}
                test_stat = {**test_stat, **val_stat}
                st.data['test_results'].append(test_stat)

        verdict = Judge.ACCEPT
        for judge in judges:
            if judge == Judge.REJECT:
                verdict = Judge.REJECT
                break

        consistency = Validate.MATCH
        for match in matches:
            if match == Validate.MISMATCH:
                consistency = Validate.MISMATCH
                break

        test_results = list(zip(judges, matches))
        st.data['patch_plausible'] = patch_plausible
        st.data['verdict'] = Judge.char(verdict)
        st.data['consistency'] = Validate.char(consistency)
        logger.info('Done successfully')
        logger.info('Results: {}'.format(test_results))
        return verdict

    def exists_JQF_classes(self, test_units):
        for test_unit in test_units:
            cls = test_unit.get_cls()
            JQF_file = join(self.deltas_dir, self.project,
                            '{}_bug{}'.format(self.project, self.bug_id),
                            self.get_project_test_dir(),
                            cls.replace('.', '/') + '.java')
            if exists(JQF_file):
                logger.info('Existing JQF file: {}'.format(JQF_file))
                return True
            else:
                logger.warning("Non-existing JQF file: {}".format(JQF_file))

        return False

    def test_patch(self, test_unit):
        judge = None
        test_patch_stat = None

        # Iterate until change-revaling input is found
        if 'FIND_DIFF' not in self.config['skip']:
            logger.info('Run zest to find diff-revealing input')
            start_time = time.time()
            input_dir = self.prep_dir(join(self.input_dir, test_unit.get_test_id()))
            if exists(input_dir):
                shutil.rmtree(input_dir)
            self.prep_dir(input_dir)

            fuzz_out_dir = self.prep_dir(
                join(self.fuzz_out_dir, test_unit.get_test_id()))
            if exists(fuzz_out_dir):
                shutil.rmtree(fuzz_out_dir)

            self.diff_output_found = self.find_diff(test_unit)

            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['FIND_DIFF'] += elapsed_time
        else:
            logger.info('FIND_DIFF is skipped and diff_output_found is assumed to be true')
            self.diff_output_found = True

        if not self.diff_output_found:
            logger.info('failed to find diff-revealing input')
            judge = Judge.ACCEPT
        else:
            judge = Judge.REJECT

        return judge, test_patch_stat

    def validate_judge(self, test_unit, judge, predict_stat):
        result = None
        val_stat = None
        if judge == Judge.ACCEPT and self.correctness == "Correct":
            return Validate.MATCH, {'validation_size': 0}
        if 'CHECKOUT_FIX' not in self.config['skip']:
            start_time = time.time()
            logger.info('Check out (fix) {} {}'.format(self.project, self.bug_id))
            code = self.checkout_fix(self.project, self.bug_id)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['CHECKOUT_FIX'] += elapsed_time
            if code != 0:
                raise PoracleException('check-out failed (fix)')

        if 'DELTA_FIX' not in self.config['skip']:
            start_time = time.time()
            logger.info('Apply deltas for patch')
            self.apply_deltas_root(self.project, self.bug_id,
                                   self.dfj4_fix_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['DELTA_FIX'] += elapsed_time

        if 'COMPILE_FIX' not in self.config['skip']:
            start_time = time.time()
            logger.info('Compile the fixed version')
            code = self.compile(self.dfj4_fix_dir)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['COMPILE_FIX'] += elapsed_time
            if code != 0:
                raise PoracleException('compiling patch src failed')

        if 'REPRO_FOR_VALIDATE' not in self.config['skip']:
            start_time = time.time()
            logger.info('Run test input for validation')
            self.repro_for_validate(test_unit, judge)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['REPRO_FOR_VALIDATE'] += elapsed_time

        if 'VALIDATE' not in self.config['skip']:
            start_time = time.time()
            logger.info('Validate with fixed version')
            result, val_stat = self.validate(test_unit, judge, val_stat)
            end_time = time.time()
            elapsed_time = end_time - start_time
            st.data['time']['VALIDATE'] += elapsed_time

        return result, val_stat

    def checkout_buggy(self, project, bug_id):
        if exists(self.dfj4_buggy_dir):
            shutil.rmtree(self.dfj4_buggy_dir)
        cmd = 'defects4j checkout -p ' + project + ' -v ' + bug_id + 'b' + \
              ' -w ' + self.dfj4_buggy_dir
        return self.run(cmd)

    def checkout_patch(self, project, bug_id):
        logger.info('Check out (patch) {} {}'.format(project, bug_id))
        if exists(self.dfj4_patch_dir):
            shutil.rmtree(self.dfj4_patch_dir)
        cmd = 'defects4j checkout -p ' + project + ' -v ' + bug_id + 'b' + \
              ' -w ' + self.dfj4_patch_dir
        return self.run(cmd)

    def checkout_fix(self, project, bug_id):
        cmd = 'defects4j checkout -p ' + project + ' -v ' + bug_id + 'f' + \
              ' -w ' + self.dfj4_fix_dir
        return self.run(cmd)

    def apply_deltas_root(self, project, bug_id, dst_dir_root):
        self.apply_deltas(join(self.deltas_dir, project,
                               '{}_bug{}'.format(project,
                                                 bug_id)),
                          dst_dir_root)

    def apply_deltas(self, src_dir_root, dst_dir_root):
        if not isdir(dst_dir_root):
            os.mkdir(dst_dir_root)
        for src_dir, dirs, files in walk(src_dir_root):
            # visit only the current (.) directory
            if src_dir == src_dir_root:
                for file in files:
                    logger.debug('copy {} to {}'.
                                 format(join(src_dir_root, file),
                                        join(dst_dir_root, file)))
                    copyfile(join(src_dir_root, file), join(dst_dir_root, file))
                for dir in dirs:
                    self.apply_deltas(join(src_dir_root, dir),
                                      join(dst_dir_root, dir))

        if exists(join(dst_dir_root, 'delta.sh')):
            chmod(join(dst_dir_root, 'delta.sh'), stat.S_IXUSR | stat.S_IWUSR | stat.S_IRUSR)
            cmd = '{} {}'.format(join(dst_dir_root, 'delta.sh'), dst_dir_root)
            if self.run(cmd) != 0:
                raise PoracleException('failed to run delta.sh')

    def apply_patch(self, patch_file):
        logger.info('Apply patch file {}'.format(patch_file))
        cmd = 'patch -p1 -d {}  < {}'.format(self.dfj4_patch_dir, patch_file)
        return self.run(cmd)

    def compile(self, workdir):
        cmd = 'defects4j compile -w ' + workdir
        return self.run(cmd)

    def patch_test(self, workdir):
        ft_file = join(workdir, 'failing_tests')

        # ret = 1
        for ft in self.failing_tests:
            cmd = 'rm -f {}'.format(ft_file)
            self.run(cmd)

            cmd = 'defects4j test -t {} -w {}'.format(ft, workdir)
            self.run(cmd)
            if not self.file_empty(ft_file):
                # test passed
                # ret = 0
                return 1

        return 0

    def set_failing_tests(self):
        logger.info('Retrieving a failing test')
        with tempfile.NamedTemporaryFile() as fp:
            cmd = 'defects4j export -p tests.trigger -w ' + self.dfj4_buggy_dir + \
                  ' -o ' + fp.name
            code = self.run(cmd)
            if code == 0:
                with open(fp.name, mode='r') as f:
                    failing_tests = f.read().splitlines()
                    self.failing_tests = failing_tests
            return code

    def set_test_units(self):
        logger.info('Retrieving a test unit')
        self.test_units = [TestUnit(test) for test in self.failing_tests]
        idx = 1
        for test_unit in self.test_units:
            test_unit.set_test_id('test' + str(idx))
            idx += 1

    def get_project_cp(self, dir):

        def add_prefix(path):
            return join(dir, path)

        if self.project in ['Math']:
            cp = ['target/test-classes', 'target/classes']
        elif self.project in ['Time']:
            cp = ['target/test-classes', 'target/classes', 'build/tests', 'build/classes']
        elif self.project in ['Lang']:
            cp = ['target/tests', 'target/classes']
        elif self.project in ['Chart']:
            cp = ['build', 'build-tests']
        else:
            raise PoracleException('Unsupported project: {}'.format(self.project))
        return ':'.join(map(add_prefix, cp))

    def get_project_test_dir(self):
        if self.project in ['Math', 'Time']:
            cands = ['src/test/java', 'src/test/']
            for cand in cands:
                if exists(join(self.dfj4_buggy_dir, cand)):
                    return cand
            raise PoracleException('Failed to get the test dir from project: {}'.
                                   format(self.project))
        elif self.project in ['Lang']:
            cands = ['src/test/java', 'src/test']
            for cand in cands:
                if exists(join(self.dfj4_buggy_dir, cand)):
                    return cand
            raise PoracleException('Failed to get the test dir from project: {}'.
                                   format(self.project))
        elif self.project in ['Chart']:
            return 'tests'
        else:
            raise PoracleException('Failed to get the test dir from project: {}'.
                                   format(self.project))

    def set_buggy_project_cp(self):
        logger.info('Retrieving a buggy project class path')
        self.buggy_project_cp = self.get_project_cp(self.dfj4_buggy_dir)
        logger.debug('buggy_project_cp: {}'.format(self.buggy_project_cp))

    def set_patch_project_cp(self):
        logger.info('Retrieving a patch project class path')
        self.patch_project_cp = self.get_project_cp(self.dfj4_patch_dir)
        logger.debug('patch_project_cp: {}'.format(self.patch_project_cp))

    def set_fix_project_cp(self):
        logger.info('Retrieving a fix project class path')
        self.fix_project_cp = self.get_project_cp(self.dfj4_fix_dir)
        logger.debug('fix_project_cp: {}'.format(self.fix_project_cp))

    '''
    return true when different output is found between the two versions.
    '''
    def find_diff(self, test_unit):
        num_of_workers = self.config['num_of_threads']
        with ThreadPoolExecutor(max_workers=num_of_workers) as executor:
            args = ((test_unit, thread_id)
                    for thread_id in range(1, num_of_workers + 1))
            results = executor.map(lambda p: self.zest_patch(*p), args)
            diff_found = reduce(lambda x, y: x or y, results, False)
            return diff_found

    def zest_patch(self, test_unit, thread_id):
        logger.info('Run zest_patch with thread {}'.format(thread_id))

        logdir = self.prep_dir(join(self.log_dir,
                                    test_unit.get_test_id(),
                                    str(thread_id)))
        fuzz_out_dir = self.prep_dir(join(self.fuzz_out_dir, test_unit.get_test_id()))
        cmd = 'zest --target {} \
        --logdir {} \
        --seed {} \
        --threadName {} \
        --max-corpus-size {} \
        --widening-plateau-threshold {} \
        --max-mutations {} \
        --timeout {} \
        --aop {} \
        --duration {} \
        --exploreDuration {} \
        --delta {} \
        {} \
        -o {} {} {} {} {}'.format(','.join(self.target),
                                  logdir,
                                  random.randint(1, 1000000),
                                  self.threadName,
                                  self.config['max_corpus_size'],
                                  self.config['widening_plateau_threshold'],
                                  self.config['max_mutations'],
                                  self.config['timeout'],
                                  self.config['aop'],
                                  self.config['duration'],
                                  self.config['exploreDuration'],
                                  self.delta_allowed,
                                  '--opad' if self.config['use_opad'] else '',
                                  join(fuzz_out_dir, str(thread_id)),
                                  ':'.join([self.buggy_project_cp,
                                            self.config['tracing_jar']]),
                                  ':'.join([self.patch_project_cp,
                                            self.config['tracing_jar']]),
                                  test_unit.get_cls(), test_unit.get_mthd())

        if self.run(cmd) != 0:
            raise PoracleException('zest-patch failed with thread {}'.format(thread_id))

        # check whether target_hit directory is empty or not
        # If the directory is not empty, we return true.
        # check whether diff_out directory is empty or not
        # If the directory is not empty, we return true.
        target_hit_dir = join(fuzz_out_dir, str(thread_id), 'target_hit')
        diff_out_dir = join(fuzz_out_dir, str(thread_id), 'diff_out')
        if isdir(target_hit_dir):
            if len(os.listdir(target_hit_dir)) == 0:
                logger.debug("{} is empty".format(target_hit_dir))
                return False
            else:
                if isdir(diff_out_dir):
                    if len(os.listdir(diff_out_dir)) == 0:
                        logger.debug("{} is empty".format(diff_out_dir))
                        return False
                else:
                    raise PoracleException('diff_out directory does not exist')
                logger.debug("{} is not empty".format(target_hit_dir))
                return True
        else:
            if isdir(diff_out_dir):
                if len(os.listdir(diff_out_dir)) != 0:
                    logger.debug("{} is not empty".format(diff_out_dir))
                    return True
            logger.info('target_hit directory does not exist')
            return False

        # check whether diff_out directory is empty or not
        # If the directory is not empty, we return true.
        # if isdir(diff_out_dir):
        #     if len(os.listdir(diff_out_dir)) == 0:
        #         logger.debug("{} is empty".format(diff_out_dir))
        #         return False
        #     else:
        #         logger.debug("{} is not empty".format(diff_out_dir))
        #         return True
        # else:
        #     raise PoracleException('diff_out directory does not exist')

    def zest_repro(self, project_cp, test_unit, input_file, log_dir=None,
                   run_buggy_version=False):
        if exists(log_dir):
            shutil.rmtree(log_dir)
        os.mkdir(log_dir)

        if not exists(input_file):
            raise PoracleException('File does not exist: {}'.format(input_file))

        args = '{} {} {}'.format(test_unit.get_cls(), test_unit.get_mthd(), input_file)
        if log_dir is not None:
            args = '--logdir {} {}'.format(log_dir, args)
        if run_buggy_version:
            args = '--run-buggy-version {}'.format(args)

        cmd = 'zest-repro --cp {} {}'.format(project_cp, args)
        if not self.config['verbose']:
            cmd = cmd + ' &>/dev/null'

        if log_dir is not None:
            file_created = threading.Event()
            event_handler = FileSystemEventHandler()

            def on_created(event):
                logger.debug('created: {}'.format(event.src_path))
                if event.src_path.endswith('OUT.log'):
                    file_created.set()

            event_handler.on_created = on_created

        code = self.run(cmd)

        #  wait until OUT.log becomes ready
        if log_dir is not None:
            out_file = join(log_dir, 'OUT.log')
            if not exists(out_file):
                observer = Observer()
                observer.schedule(event_handler, log_dir)
                observer.start()
                file_created.wait(60)
                observer.stop()
                logger.debug('wait for 1 second to make sure file gets ready: {}'.format(out_file))
                time.sleep(1)
                if not exists(out_file):
                    logger.warning('{} does not exist'.format(out_file))
            if self.file_empty(out_file):
                logger.warning('{} is empty'.format(out_file))

        if code != 0:
            raise PoracleException('failed to zest-repro with {}'.format(input_file))

    def run(self, cmd, env=os.environ):
        logger.debug('cmd: {}'.format(cmd))
        proc = subprocess.Popen(cmd,
                                env=env,
                                stdout=self.subproc_output,
                                stderr=self.subproc_output,
                                shell=True)
        try:
            code = proc.wait()
            # logger.debug('code: {}'.format(code))
            return code
        except subprocess.TimeoutExpired:
            logger.warning('timeout for: {}'.format(cmd))
            return 1

    def check_output(self, cmd, env=os.environ):
        logger.debug('cmd: {}'.format(cmd))
        out = subprocess.run(cmd, env=env,
                             stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE,
                             shell=True).stdout
        out = out.strip()
        logger.debug('out: {}'.format(out))
        return out

    def add_prefix(self, prefix, path):
        return join(prefix, path)

    def corpus_files(self, fuzz_out_dir):
        corpus_dir = join(fuzz_out_dir, 'corpus')
        corpus_files = [f for f in os.listdir(corpus_dir)
                        if isfile(join(corpus_dir, f))]
        return corpus_files

    def all_files(self, fuzz_out_dir):
        d = join(fuzz_out_dir, 'all')
        files = [f for f in os.listdir(d)
                 if isfile(join(d, f))]
        return files

    def failures_files(self, fuzz_out_dir):
        failures_dir = join(fuzz_out_dir, 'failures')
        failures_files = [f for f in os.listdir(failures_dir)
                          if isfile(join(failures_dir, f))]
        return failures_files

    def prep_dir(self, d):
        if not exists(d):
            pathlib.Path(d).mkdir(parents=True, exist_ok=True)
        return d

    def sanitize_data(self, data_dir, can_be_empty=False):
        data_file = join(data_dir, 'data.csv')
        if not exists(data_file):
            raise PoracleException('File not existing: {}'.format(data_file))

        data_san_file = join(data_dir, 'data_san.csv')
        if exists(data_san_file):
            os.remove(data_san_file)
        dsf_lines = 0

        with open(data_san_file, 'a+') as dsf:
            with open(data_file, 'r') as df:
                lines = df.readlines()
                if len(lines) > 0:
                    lines_wo_row_idx = list(map(lambda line: line.split(':')[1], lines))
                    max_num = max(map(lambda line: line.count(';'),
                                      lines_wo_row_idx))
                else:
                    raise PoracleException('An empty file: {}'.format(data_file))

                for line in lines:
                    # logger.debug('line: {}'.format(line))
                    line = line.replace('NaN', '0')
                    row_idx, line = line.split(':', 1)
                    if line.count(';') == max_num:
                        in_data, out_data = line.split(',')
                        # Ignore Infinity in the output
                        # We assume that Infinity is already replaced with
                        # a proper value beforehand
                        if len([s for s in in_data.split(';')
                                if not self.is_non_inf_number(s)]) > 0:
                            continue
                        if len([s for s in out_data.split(';')
                                if not self.is_non_inf_number(s)]) > 0:
                            continue
                        dsf_lines += 1
                        dsf.write('{}:{}'.format(row_idx, line))
        if not can_be_empty and dsf_lines <= 0:
            raise PoracleException('{} seems empty'.format(data_san_file))

    def is_non_inf_number(self, s: str):
        s = s.replace('\n', '')
        s = s.replace('-', '')
        if s in ['Infinity']:
            return False
        if s.isnumeric():
            return True
        try:
            float(s)
            return True
        except ValueError:
            return False

    def train_model(self, test_unit):
        data_san_file = join(self.data_buggy_dir, test_unit.get_test_id(), 'data_san.csv')
        with open(data_san_file, 'r') as f:
            line = f.readline().split(':', 1)[1]
            in_data, out_data = line.split(',')
            in_size = len(in_data.split(';'))
            out_size = len(out_data.split(';'))
            logger.info('in_size: {}'.format(in_size))
            logger.info('out_size: {}'.format(out_size))

        df = pd.read_table(data_san_file, sep=';|,|:', engine='python', header=None)
        dataset = df.values
        x = dataset[:, 1:in_size + 1]
        y = dataset[:, in_size + 1:in_size + out_size + 1]
        # logger.debug('x:\n {}'.format(x))
        # logger.debug('y:\n {}'.format(y))
        scaler_x = MinMaxScaler()
        scaler_y = MinMaxScaler()
        scaler_x.fit(x)
        xscale = scaler_x.transform(x)
        scaler_y.fit(y)
        yscale = scaler_y.transform(y)
        # logger.debug('xscale:\n {}'.format(xscale))
        # logger.debug('yscale:\n {}'.format(yscale))
        X_train, X_test, y_train, y_test = train_test_split(xscale, yscale)

        trial = 1
        num_of_neurons = in_size + out_size
        while True:
            logger.info('num_of_neurons: {}'.format(num_of_neurons))
            model = Sequential()
            model.add(Dense(num_of_neurons,
                            input_dim=in_size,
                            kernel_initializer='normal',
                            activation='relu'))
            # model.add(Dense(num_of_neurons, activation='relu'))
            model.add(Dense(out_size, activation='relu'))
            if self.config['verbose']:
                model.summary()
            model.compile(loss='mse', optimizer='adam', metrics=['mse', 'mae'])
            hist = model.fit(X_train, y_train, epochs=self.config['epochs'],
                             batch_size=self.config['batch_size'],
                             verbose=1 if self.config['verbose'] else 0,
                             validation_split=self.config['validation_split'])
            # logger.debug('hist.history: {}'.format(hist.history))
            loss = hist.history['loss'][-1]
            val_loss = hist.history['val_loss'][-1]
            loss_threshold = self.config['loss_threshold']
            if loss < loss_threshold:
                logger.info('loss: {}'.format(loss))
                break
            elif trial >= self.config['train_trial_threshold']:
                logger.info('Train trial threshold is reached.')
                logger.info('loss: {}'.format(loss))
                break
            else:
                logger.info('loss {} is not smaller than threshold {}'.
                            format(loss, loss_threshold))
                num_of_neurons = num_of_neurons + 10
                trial += 1

        test_id = test_unit.get_test_id().replace('test', '')
        model_stat = {'test_ID': test_id, 'sample_size': len(xscale),
                      'in_size': in_size, 'out_size': out_size,
                      'trial': trial, 'num_of_neurons': num_of_neurons,
                      'loss': loss, 'val_loss': val_loss}
        st.data['model'].append(model_stat)
        return model, scaler_x, scaler_y

    '''
    Get row IDs (the first column)
    '''
    def get_row_ids(self, data_san_file):
        if self.file_empty(data_san_file):
            return np.empty(0)

        df = pd.read_table(data_san_file, sep=';|,|:', engine='python', header=None)
        dataset = df.values
        logger.debug('dataset\n: {}'.format(dataset))
        rows = dataset[:, 0]  # the row ID is in the first column
        return rows

    def repro_for_validate(self, test_unit, judge):

        def repro(subroot, input_file):
            log_dir = self.prep_dir(join(self.val_fix_dir,
                                         test_unit.get_test_id(), input_file))
            self.zest_repro(self.fix_project_cp, test_unit, join(subroot, input_file),
                            log_dir=log_dir)

            ## No need to run the patched version again. We already ran it.
            # log_dir = self.prep_dir(join(self.val_patch_dir,
            #                              test_unit.get_test_id(), input_file))
            # self.zest_repro(self.patch_project_cp, test_unit, join(subroot, input_file),
            #                 log_dir=log_dir)

        fuzz_out_dir = join(self.fuzz_out_dir, test_unit.get_test_id())
        thread_dirs = os.listdir(fuzz_out_dir)

        for thread_dir in thread_dirs:
            diff_out_dir = join(fuzz_out_dir, thread_dir, 'diff_out')
            input_files = os.listdir(diff_out_dir)
            if len(input_files) < 1:
                logger.info('diff_out directory is empty')
                return
            assert len(input_files) == 1
            input_file = input_files[0]
            repro(diff_out_dir, input_file)

    def validate(self, test_unit, judge, val_stat: Dict):
        result = None
        file_modified = threading.Event()
        event_handler = FileSystemEventHandler()

        def on_modified(event):
            file_modified.set()

        event_handler.on_modified = on_modified

        diff_count = 0

        # obtain input_file
        fuzz_out_dir = join(self.fuzz_out_dir, test_unit.get_test_id())
        thread_dirs = os.listdir(fuzz_out_dir)
        for thread_dir in thread_dirs:
            diff_out_dir = join(fuzz_out_dir, thread_dir, 'diff_out')
            input_files = os.listdir(diff_out_dir)
            if len(input_files) < 1:
                raise PoracleException('diff_out directory is empty')
            assert len(input_files) == 1
            input_file = input_files[0]

        val_fix_out = join(self.val_fix_dir, test_unit.get_test_id(), input_file, 'OUT.log')
        val_patch_out = join(self.log_dir, test_unit.get_test_id(), '1', 'PATCH',
                             input_file, 'OUT.log')
        val_org_out = join(self.log_dir, test_unit.get_test_id(), '1', 'ORG',
                           input_file, 'OUT.log')
        in_file = join(self.log_dir, test_unit.get_test_id(), '1', 'ORG',
                       input_file, 'IN.log')

        cmd = 'diff {} {}'.format(val_fix_out, val_patch_out)
        # diff returns exit code 1 when inputs are different
        if self.run(cmd) == 1:
            diff_count = 1

        logger.info('diff_count (validate): {}'.format(diff_count))
        if judge == Judge.ACCEPT:
            result = Validate.MATCH if diff_count == 0 else Validate.MISMATCH
        elif judge == Judge.REJECT:
            result = Validate.MATCH if diff_count > 0 else Validate.MISMATCH

        if judge is not None:
            logger.info('Validation: {}'.format(result))

        if val_stat is not None:
            val_stat = {**val_stat,
                        'diff_count': diff_count,
                        'in': in_file,
                        'org_out': val_org_out,
                        'patch_out': val_patch_out,
                        'fix_out': val_fix_out}
        else:
            val_stat = {'diff_count': diff_count,
                        'in': in_file,
                        'org_out': val_org_out,
                        'patch_out': val_patch_out,
                        'fix_out': val_fix_out}
        return result, val_stat

    def equals(self, val1, val2):
        if val1 == val2:
            return True, None
        elif val1 == 'NaN':
            return val2 == 'NaN', None
        else:
            try:
                val1 = float(val1)
                val2 = float(val2)
                delta = abs(val1 - val2)
                return delta <= self.delta_allowed, delta
            except ValueError:
                return str(val1) == str(val2), None

    '''
    Return True if file is empty
    '''

    def file_empty(self, f):
        # return os.stat(f).st_size == 0

        # the following code seems more reliable
        with open(f, 'r') as of:
            lines = of.readlines()
            return len(lines) <= 0

    def max_row_idx(self, arr: np.ndarray):
        if arr.size == 0:
            return 1
        else:
            return max(arr)

    def min_row_idx(self, arr: np.ndarray):
        if arr.size == 0:
            return 1
        else:
            return min(arr)

    def diff(self, file1, file2):
        cmd = 'diff {} {}'.format(file1, file2)
        code = self.run(cmd)
        if code == 0:
            return False
        else:
            return True
