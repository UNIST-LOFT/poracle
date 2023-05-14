import os
import stat
from os.path import join, exists, isdir
import shutil
from shutil import copyfile
import subprocess
import sys
import logging
import argparse
import json
from poracle import Poracle, PoracleException
import statistics
import time
import tensorflow as tf


logger = logging.getLogger("main")
subproc_output = sys.stdout


def copy(src_dir_root, dst_dir_root):
    if not isdir(dst_dir_root):
        os.mkdir(dst_dir_root)
    for src_dir, dirs, files in os.walk(src_dir_root):
        # visit only the current (.) directory
        if src_dir == src_dir_root:
            for file in files:
                logger.debug('copy {} to {}'.
                             format(join(src_dir_root, file),
                                    join(dst_dir_root, file)))
                copyfile(join(src_dir_root, file), join(dst_dir_root, file))
            for dir in dirs:
                copy(join(src_dir_root, dir),
                     join(dst_dir_root, dir))


if __name__ == '__main__':
    start_time = time.time()

    def rm_force(action, name, exc):
        os.chmod(name, stat.S_IREAD)
        shutil.rmtree(name)

    parser = argparse.ArgumentParser('poracle')
    parser.add_argument('config_file', metavar='CONFIG_FILE', help='config file')

    parser.add_argument('--epsilon-for-same', metavar='NUM', type=float,
                        default=1e-02,
                        help='two values whose differeince is withion epsilon is \
                        considered the same (default: %(default)s)')
    parser.add_argument('--timeout', metavar='NUM', type=int,
                        default=3600,
                        help='timeout (default: %(default)s)')
    parser.add_argument('--aop', metavar='path', type=str,
                        default='/poracle/modules/JQF/aspect/aop.xml',
                        help='aop (default: %(default)s)')
    parser.add_argument('--tracing-jar', metavar='tracing-jar', type=str,
                        default='/poracle/modules/JQF/aspect/tracing.jar',
                        help='tracing.jar (default: %(default)s)')
    parser.add_argument('--duration', metavar='NUM', type=str,
                        default='1m',
                        help='duration (default: %(default)s)')
    parser.add_argument('--exploreDuration', metavar='NUM', type=str,
                        default='5s',
                        help='explore duration (default: %(default)s)')
    parser.add_argument('--max-corpus-size', metavar='NUM', type=str,
                        default="10",
                        help='Maximum corpus size (default: %(default)s)')
    parser.add_argument('--max-mutations', metavar='NUM', type=str,
                        default="50",
                        help='(default: %(default)s)')
    parser.add_argument('--widening-plateau-threshold', metavar='NUM', type=str,
                        default="10",
                        help='(default: %(default)s)')
    parser.add_argument('--deviation-threshold', metavar='NUM', type=float,
                        default=0.3,
                        help='deviation threshold between 0 and 1')
    parser.add_argument('--inconsistency-threshold', metavar='NUM', type=float,
                        default=0.5,
                        help='deviation threshold between 0 and 1')
    parser.add_argument('--progress-ratio-threshold', metavar='NUM', type=float,
                        default=10,
                        help='progress ratio threshold')
    parser.add_argument('--num-of-iter', metavar='NUM', type=int,
                        default=1,
                        help='''
                        The number of iterations to run zest
                        (default: %(default)s)
                        ''')
    parser.add_argument('--max-find-diff-trial', metavar='NUM', type=int,
                        default=1,
                        help='''
                        The maximum number of trials to find change-revealing input
                        (default: %(default)s)
                        ''')
    parser.add_argument('--train-trial-threshold', metavar='NUM', type=int,
                        default=10,
                        help='Train trial threshold (defulat: %(default)s')
    parser.add_argument('--num-of-threads', metavar='NUM', type=int,
                        default=1,
                        help='The number of threads (default: %(default)s)')
    parser.add_argument('--epochs', metavar='NUM', type=int,
                        default=150,
                        help='Kera\'s epochs (default: %(default)s)')
    parser.add_argument('--batch-size', metavar='NUM', type=int,
                        default=32,
                        help='Kera\'s batch-size (default: %(default)s)')
    parser.add_argument('--validation-split', metavar='NUM', type=float,
                        default=0.2,
                        help='Kera\'s validation-split (default: %(default)s)')
    parser.add_argument('--keep-workdir', action='store_true',
                        help='keep .poracle dir (default: %(default)s)')
    parser.add_argument('--log', metavar='LOG', default=None,
                        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
                        help='set the logging level')
    parser.add_argument('--skip', metavar='SKIP STEPS', nargs='+', default=[],
                        choices=['CHECKOUT_BUGGY', 'DELTA_BUGGY', 'COMPILE_BUGGY', 'CHECKOUT_PATCH', 'PATCH', 'DELTA_PATCH', 'COMPILE_PATCH', 'PATCH_TEST', 'FIND_DIFF', 'CHECKOUT_FIX', 'DELTA_FIX', 'COMPILE_FIX', 'REPRO_FOR_VALIDATE', 'VALIDATE'],
                        help='skip steps')
    parser.add_argument('--skip-tests', metavar='SKIP TEST IDs', nargs='+', default=[],
                        help='skip tests')
    parser.add_argument('--fuzz-results-dir', metavar='FUZZ_RESULTS', type=str,
                        default=None, help='fuzz results dir')
    parser.add_argument('--work-dir', metavar='WORKDIR', type=str,
                        default='.poracle', help='work dir')
    parser.add_argument('--deltas-dir', metavar='DELTAS_DIR', type=str,
                        default='deltas', help='deltas dir')
    parser.add_argument('--verbose', action='store_true',
                        help='verbose printout (default: %(default)s)')
    parser.add_argument('--quiet', action='store_true',
                        help='print only errors (default: %(default)s)')
    parser.add_argument('--use-opad', action='store_true',
                        help='use opad mode (default: %(default)s)')

    args = parser.parse_args()

    poracle_config = dict()
    poracle_config['epsilon_for_same'] = args.epsilon_for_same
    poracle_config['verbose'] = args.verbose
    poracle_config['timeout'] = args.timeout
    poracle_config['aop'] = args.aop
    poracle_config['tracing_jar'] = args.tracing_jar
    poracle_config['duration'] = args.duration
    poracle_config['exploreDuration'] = args.exploreDuration
    poracle_config['max_corpus_size'] = args.max_corpus_size
    poracle_config['max_mutations'] = args.max_mutations
    poracle_config['widening_plateau_threshold'] = args.widening_plateau_threshold
    poracle_config['num_of_iter'] = args.num_of_iter
    poracle_config['num_of_threads'] = args.num_of_threads
    poracle_config['max_find_diff_trial'] = args.max_find_diff_trial
    poracle_config['epochs'] = args.epochs
    poracle_config['batch_size'] = args.batch_size
    poracle_config['validation_split'] = args.validation_split
    poracle_config['skip'] = args.skip
    poracle_config['skip_tests'] = args.skip_tests
    poracle_config['DEV_THRESHOLD'] = args.deviation_threshold
    poracle_config['INCONS_THRESHOLD'] = args.inconsistency_threshold
    poracle_config['PROGRESS_THRESHOLD'] = args.progress_ratio_threshold
    poracle_config['use_opad'] = args.use_opad

    if len(args.skip) > 0:
        args.keep_workdir = True

    poracle_workdir = join(os.getcwd(), args.work_dir)
    if args.keep_workdir:
        assert exists(poracle_workdir)
    else:
        if exists(poracle_workdir):
            shutil.rmtree(poracle_workdir, onerror=rm_force)
        os.mkdir(poracle_workdir)

    deltas_dir = join(os.getcwd(), args.deltas_dir)

    rootLogger = logging.getLogger()
    FORMAT = logging.Formatter('%(levelname)-8s %(name)-15s %(message)s')
    if args.quiet:
        rootLogger.setLevel(logging.ERROR)
        tf.get_logger().setLevel(logging.ERROR)
    elif args.log is not None:
        log_level = getattr(logging, args.log, None)
        rootLogger.setLevel(log_level)
    else:
        rootLogger.setLevel(logging.INFO)
    fileHandler = logging.FileHandler("{0}/{1}.log".format(poracle_workdir, 'poracle'))
    fileHandler.setFormatter(FORMAT)
    rootLogger.addHandler(fileHandler)
    consoleHandler = logging.StreamHandler()
    consoleHandler.setFormatter(FORMAT)
    rootLogger.addHandler(consoleHandler)

    if args.verbose:
        subproc_output = sys.stderr
    else:
        subproc_output = subprocess.DEVNULL

    if args.fuzz_results_dir is not None:
        copy(args.fuzz_results_dir, join(poracle_workdir, 'fuzz-results'))

    with open(args.config_file, "r") as read_file:
        data = json.load(read_file)
        target = data['target'] if 'target' in data else None
        delta_allowed = float(data['delta_allowed']) if 'delta_allowed' in data else 0
        data['delta_allowed'] = delta_allowed
        threadName = data['threadName'] if 'threadName' in data else 'main'

        statistics.init(poracle_workdir, args.config_file, poracle_config, data)

        try:
            poracle = Poracle(poracle_config, poracle_workdir, deltas_dir,
                              data['project'], data['bug_id'],
                              data['ID'], target, delta_allowed,
                              data['correctness'], threadName)
            code = None
            code = poracle()
        except PoracleException as e:
            logger.error(e.get_msg())
        end_time = time.time()
        elapsed_time = end_time - start_time
        statistics.data['total_time'] = elapsed_time
        statistics.save()
        if code is not None:
            exit(0)
        else:
            exit(1)
