import os
import logging
import numpy as np
from os.path import join
from pyod.models.lof import LOF
from enum import Enum
from glob import glob


logger = logging.getLogger("predict")


class Result(Enum):
    ACCEPT = 0
    REJECT = 1
    UNCLEAR = 2


class Type(Enum):
    IN = 0
    OUT = 1


class ModelError(Exception):
    pass


class Predict:

    def __init__(self, data_buggy_dir, data_patch_dir, data_orig_dir):
        self.data_buggy_dir = data_buggy_dir
        self.data_patch_dir = data_patch_dir
        self.data_orig_dir = data_orig_dir

    def __call__(self):
        clf = LOF(contamination=0.1)
        buggy_enter_csv = self.get_file(join(self.data_buggy_dir, '*_ENTER.csv'))
        buggy_exit_csv = self.get_file(join(self.data_buggy_dir, '*_EXIT.csv'))
        data = self.get_data(buggy_enter_csv, buggy_exit_csv)

        # extend data with self.data_orig_dir
        for cur_dir, dirs, files in os.walk(self.data_orig_dir):
            for f_dir in dirs:
                enter_csv = self.get_file(join(cur_dir, f_dir, '*_ENTER.csv'))
                exit_csv = self.get_file(join(cur_dir, f_dir, '*_EXIT.csv'))
                ext_data = self.get_data(enter_csv, exit_csv)
                logger.debug('shape of data: {}'.format(data.shape))
                logger.debug('shape of ext_data: {}'.format(ext_data.shape))
                data = np.concatenate((data, ext_data), axis=0)
                logger.debug('shape of data: {}'.format(data.shape))

        clf.fit(data)
        train_pred = clf.labels_  # binary labels (0: inliers, 1: outliers)
        unique, counts = np.unique(train_pred, return_counts=True)
        logger.debug('unique (train): {}'.format(unique))
        logger.debug('counts (train): {}'.format(counts))
        if 0 not in unique:
            raise ModelError('Model contains no inlier')

        inliers_size = counts[0]
        outliers_size = counts[1] if len(counts) > 1 else 0
        logger.debug('num of inliers: {}'.format(inliers_size))
        logger.debug('num of outliers: {}'.format(outliers_size))

        return self.predict(clf)

    def predict(self, clf):
        results = []
        for cur_dir, dirs, files in os.walk(self.data_patch_dir):
            for f_dir in dirs:
                patch_enter_csv = self.get_file(join(cur_dir, f_dir, '*_ENTER.csv'))
                patch_exit_csv = self.get_file(join(cur_dir, f_dir, '*_EXIT.csv'))
                patch_test_pred = clf.predict(
                    self.get_data(patch_enter_csv, patch_exit_csv))
                class_patch = self.classify(patch_test_pred)

                orig_enter_csv = self.get_file(join(self.data_orig_dir, f_dir,
                                                    '*_ENTER.csv'))
                orig_exit_csv = self.get_file(join(self.data_orig_dir, f_dir,
                                                   '*_EXIT.csv'))
                orig_test_pred = clf.predict(
                    self.get_data(orig_enter_csv, orig_exit_csv))
                class_orig = self.classify(orig_test_pred)

                if class_orig == Type.IN and class_patch == Type.IN:
                    logger.debug('IN / IN')
                    results.append(Result.ACCEPT)
                elif class_orig == Type.IN and class_patch == Type.OUT:
                    logger.debug('IN / OUT')
                    results.append(Result.REJECT)
                elif class_orig == Type.OUT and class_patch == Type.IN:
                    logger.debug('OUT / IN')
                    results.append(Result.ACCEPT)
                else:
                    logger.debug('OUT / OUT')
                    results.append(Result.UNCLEAR)
        logger.debug('results: {}'.format(results))
        if Result.REJECT in results:
            return Result.REJECT
        else:
            return Result.ACCEPT

    def classify(self, test_pred):
        # binary labels (0: inliers, 1: outliers)
        unique, counts = np.unique(test_pred, return_counts=True)
        logger.debug('unique (predict): {}'.format(unique))
        logger.debug('counts (predict): {}'.format(counts))

        if 0 in unique:
            inliers_size = counts[0]
            outliers_size = counts[1] if len(counts) > 1 else 0
        else:
            logger.debug('Model predicts no inlier')
            inliers_size = 0
            outliers_size = counts[0]

        logger.debug('num of inliers: {}'.format(inliers_size))
        logger.debug('num of outliers: {}'.format(outliers_size))

        if outliers_size > inliers_size:
            return Type.OUT
        else:
            return Type.IN

    def get_file(self, pattern):
        lst = glob(pattern)
        assert len(lst) == 1
        return lst[0]

    def get_data(self, enter_csv, exit_csv):
        logger.debug('concat {} and {}'.format(enter_csv, exit_csv))
        data_enter = self.csv_to_data(enter_csv)
        data_exit = self.csv_to_data(exit_csv)

        logger.debug('shape of data_enter: {}'.format(data_enter.shape))
        logger.debug('shape of data_exit: {}'.format(data_exit.shape))

        data = np.concatenate((data_enter, data_exit), axis=1)

        logger.debug('shape of data: {}'.format(data.shape))
        return data

    def csv_to_data(self, csv):
        data = np.genfromtxt(csv, delimiter=',', skip_header=1)
        if data.ndim == 1:
            data = data.reshape((1, data.shape[0]))
        return data
