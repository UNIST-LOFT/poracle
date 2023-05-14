from subprocess import Popen, DEVNULL, TimeoutExpired
import logging
import argparse
import json
import os
from os.path import join, exists
import subprocess
import shutil
import pathlib
import tempfile

root_dir = os.getcwd()

logger = logging.getLogger("add_target")
prog_config = dict()


class CustomException(Exception):
    def __init__(self, msg):
        self.msg = msg

    def get_msg(self):
        return self.msg


class AddTarget:

    def __init__(self, project, bug_id, src_patch_id, patch_file):
        self.project = project
        self.bug_id = bug_id
        self.src_patch_id = src_patch_id
        self.patch_file = patch_file

    def __call__(self):
        if not exists(self.patch_file):
            raise CustomException('Patch file does not exists: {}'.format(self.patch_file))

        target_file = self.get_target_file()
        logger.info('target file: {}'.format(target_file))
        
    def get_target_file(self):
        def get_root():
            if self.project in ['Chart']:
                return 'org'
            else:
                raise CustomException('Unsupported project: {}'.format(self.project))

        cmd = "awk '/\+\+\+/ {{print $2}}' {}".format(self.patch_file)
        out = self.check_output(cmd).decode('utf-8')
        cmpts = out.split(os.sep)
        start_idx = out.split(os.sep).index(get_root())
        return os.sep.join(cmpts[start_idx:])

    def run(self, cmd, env=os.environ, quiet=False):
        logger.debug('cmd: {}'.format(cmd))
        if prog_config['quiet'] or quiet:
            proc = Popen(cmd, env=env, stdout=DEVNULL, stderr=DEVNULL,
                         shell=True)
        else:
            proc = Popen(cmd, env=env, shell=True)
        try:
            code = proc.wait()
            return code
        except TimeoutExpired:
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

    def prep_dir(self, d):
        if not exists(d):
            pathlib.Path(d).mkdir(parents=True, exist_ok=True)
        return d


if __name__ == '__main__':
    parser = argparse.ArgumentParser('add_target')
    parser.add_argument('config_file', metavar='CONFIG_FILE', help='config file')
    parser.add_argument('patch_file', metavar='PATCH_FILE', help='patch file')
    parser.add_argument('--log', metavar='LOG', default=None,
                        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
                        help='set the logging level')
    parser.add_argument('--quiet', action='store_true',
                        help='print only errors (default: %(default)s)')

    args = parser.parse_args()

    prog_config['quiet'] = args.quiet

    rootLogger = logging.getLogger()
    FORMAT = logging.Formatter('%(levelname)-8s %(name)-15s %(message)s')
    if args.quiet:
        rootLogger.setLevel(logging.ERROR)
    elif args.log is not None:
        log_level = getattr(logging, args.log, None)
        rootLogger.setLevel(log_level)
    else:
        rootLogger.setLevel(logging.INFO)
    fileHandler = logging.FileHandler("{0}/{1}.log".format(os.getcwd(), 'runexp'))
    fileHandler.setFormatter(FORMAT)
    rootLogger.addHandler(fileHandler)
    consoleHandler = logging.StreamHandler()
    consoleHandler.setFormatter(FORMAT)
    rootLogger.addHandler(consoleHandler)

    with open(args.config_file, "r") as read_file:
        data = json.load(read_file)
        project = data['project']
        bug_id = data['bug_id']
        src_patch_id = data['ID']

        try:
            add_target = AddTarget(project, bug_id, src_patch_id, args.patch_file)
            add_target()
        except CustomException as e:
            logger.error(e.get_msg())
