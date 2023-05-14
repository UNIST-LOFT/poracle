from subprocess import Popen, DEVNULL, TimeoutExpired
import logging
import argparse
import json
import os
from os.path import join, exists
import shutil
import pathlib
import tempfile

root_dir = os.getcwd()
workdir = join(root_dir, ".prep_dev_patch")

logger = logging.getLogger("prep_dev_patch")
prog_config = dict()


class CustomException(Exception):
    def __init__(self, msg):
        self.msg = msg

    def get_msg(self):
        return self.msg


class PatchGen:
    def __init__(self, project, bug_id, src_patch_id):
        self.project = project
        self.bug_id = bug_id
        self.src_patch_id = src_patch_id

        self.buggy_dir_name = project + bug_id + 'b'
        self.dfj4_buggy_dir = join(workdir, self.buggy_dir_name)

        self.fix_dir_name = project + bug_id + 'f'
        self.dfj4_fix_dir = join(workdir, self.fix_dir_name)

        self.dev_configs_dir = join(root_dir, 'dev_configs')
        self.prep_dir(self.dev_configs_dir)

        self.dev_patches_dir = join(root_dir, 'dev_patches')
        self.prep_dir(self.dev_patches_dir)

    def __call__(self):
        self.prep_dir(workdir)

        patch_file = join(self.dev_patches_dir,
                          self.patch_name(self.project, self.bug_id))
        patch_file_copy = join('patches',
                               self.patch_name(self.project, self.bug_id))
        if exists(patch_file):
            logger.info('The file already exists: {}'.format(patch_file))
        elif exists(patch_file_copy):
            logger.info('The file already exists: {}'.format(patch_file_copy))
        else:
            if 'CHECKOUT_BUGGY' not in prog_config['skip']:
                code = self.checkout_buggy(self.project, self.bug_id)
                if code != 0:
                    raise CustomException('check-out-buggy failed')

            if 'CHECKOUT_FIX' not in prog_config['skip']:
                code = self.checkout_fix(self.project, self.bug_id)
                if code != 0:
                    raise CustomException('check-out-fix failed')

            self.set_modified_classes()

            if 'GEN_PATCH' not in prog_config['skip']:
                code = self.gen_patch(self.project, self.bug_id)
                if code != 0:
                    raise CustomException('gen-patch failed')

        config_file = join(self.dev_configs_dir,
                           self.config_name(self.project, self.bug_id))
        config_file_copy = join('configs',
                                self.config_name(self.project, self.bug_id))
        if exists(config_file):
            logger.info('The file already exists: {}'.format(config_file))
        elif exists(config_file_copy):
            logger.info('The file already exists: {}'.format(config_file_copy))
        else:
            if not exists(self.dfj4_fix_dir):
                code = self.checkout_fix(self.project, self.bug_id)
                if code != 0:
                    raise CustomException('check-out-fix failed')

            self.set_modified_classes()

            if 'GEN_CONFIG' not in prog_config['skip']:
                code = self.gen_config(self.project, self.bug_id)
                if code != 0:
                    raise CustomException('gen-config failed')

        src_dir = join(root_dir, 'deltas', self.project,
                       self.src_patch_id)
        delta_dir = join(root_dir, 'deltas', self.project,
                         self.patch_id(self.project, self.bug_id))
        if not exists(src_dir):
            raise CustomException('{} must exist'.format(src_dir))
        if exists(delta_dir):
            logger.info('The dir already exists: {}'.format(delta_dir))
        else:
            shutil.copytree(src_dir, delta_dir)
            logger.info('copied {} to {}'.format(src_dir, delta_dir))

    def checkout_buggy(self, project, bug_id):
        if exists(self.dfj4_buggy_dir):
            shutil.rmtree(self.dfj4_buggy_dir)
        cmd = 'defects4j checkout -p ' + project + ' -v ' + bug_id + 'b' + \
            ' -w ' + self.dfj4_buggy_dir
        return self.run(cmd)

    def checkout_fix(self, project, bug_id):
        if exists(self.dfj4_fix_dir):
            shutil.rmtree(self.dfj4_fix_dir)
        cmd = 'defects4j checkout -p ' + project + ' -v ' + bug_id + 'f' + \
            ' -w ' + self.dfj4_fix_dir
        return self.run(cmd)

    def set_modified_classes(self):
        logger.info('Retrieving a modified class')
        with tempfile.NamedTemporaryFile() as fp:
            cmd = 'defects4j export -p classes.modified -w ' + self.dfj4_fix_dir + \
                ' -o ' + fp.name
            code = self.run(cmd, quiet=True)
            if code == 0:
                with open(fp.name, mode='r') as f:
                    modified_classes = f.read().splitlines()
                    self.modified_classes = modified_classes
            return code

    def gen_patch(self, project, bug_id):
        def src_dir(project):
            if project in ['Chart']:
                return 'source'
            elif project in ['Math', 'Time', 'Lang']:
                return 'src'
            else:
                raise CustomException('Unsupported project: {}'.format(project))

        old_dir = os.getcwd()
        os.chdir(workdir)
        patch_file = join(self.dev_patches_dir,
                          self.patch_name(project, bug_id))

        if not exists(self.buggy_dir_name):
            raise CustomException('Dir does not exist: {}'.format(self.buggy_dir_name))

        cmd = 'diff -w -r -u {} {} > {}'.format(join(self.buggy_dir_name, src_dir(project)),
                                                join(self.fix_dir_name, src_dir(project)),
                                                patch_file)
        code = self.run(cmd)
        os.chdir(old_dir)
        if code == 0:
            raise CustomException('No diff observed between {} and {}'.
                                  format(join(self.dfj4_buggy_dir, src_dir(project)),
                                         join(self.dfj4_fix_dir, src_dir(project))))
        return 0

    def patch_id(self, project, bug_id):
        return '{}_bug{}'.format(project, bug_id)

    def gen_config(self, project, bug_id):
        def file_name(cls):
            return join(cls.replace('.', '/') + '.java')

        data = dict()
        data['ID'] = self.patch_id(project, bug_id)
        data['project'] = project
        data['bug_id'] = bug_id
        data['correctness'] = 'Correct'
        data['tool'] = 'developer'
        data['target'] = [file_name(c) for c in self.modified_classes]
        config_file = join(self.dev_configs_dir,
                           self.config_name(self.project, self.bug_id))
        with open(config_file, 'w') as f:
            json.dump(data, f)
        return 0

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

    def prep_dir(self, d):
        if not exists(d):
            pathlib.Path(d).mkdir(parents=True, exist_ok=True)
        return d

    def patch_name(self, project, bug_id):
        return '{}_bug{}'.format(project, bug_id)

    def config_name(self, project, bug_id):
        return '{}_bug{}.json'.format(project, bug_id)


if __name__ == '__main__':
    parser = argparse.ArgumentParser('prep_dev_patch')
    parser.add_argument('config_file', metavar='CONFIG_FILE', help='config file')
    parser.add_argument('--log', metavar='LOG', default=None,
                        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
                        help='set the logging level')
    parser.add_argument('--quiet', action='store_true',
                        help='print only errors (default: %(default)s)')
    parser.add_argument('--skip', metavar='SKIP STEPS', nargs='+', default=[],
                        choices=['CHECKOUT_BUGGY', 'CHECKOUT_FIX'],
                        help='skip steps')

    args = parser.parse_args()

    prog_config['quiet'] = args.quiet
    prog_config['skip'] = args.skip

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
            patch_gen = PatchGen(project, bug_id, src_patch_id)
            patch_gen()
        except CustomException as e:
            logger.error(e.get_msg())
