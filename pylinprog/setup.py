#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
==============================================
Pure Python Linear Programming
==============================================

:website: https://github.com/dmishin/pylinprog
:copyright: Copyright 2016 Dmitry Shintyakov
:license: MIT, see LICENSE.MIT for details.
"""

from setuptools import setup
from codecs import open as codec_open
from os import path


HERE = path.abspath(path.dirname(__file__))


# Get the long description from the relevant file
with codec_open(path.join(HERE, 'README.md'), encoding='utf-8') as f:
    LONG_DESCRIPTION = f.read()


setup(
    name='py-linprog',

    version='1.0.0',  # PEP 440 Compliant Semantic Versioning

    keywords='math optimization simplex method linear programming',
    description='Pure Python implementation of the Simplex Method for Linear Programming problems.',
    long_description=LONG_DESCRIPTION,

    author='Dmitry Shintyakov',
    author_email='shintyakov at gmail',

    url='https://github.com/dmishin/pylinprog',

    py_modules=['linprog'],
    install_requires=[],

    license='MIT License',
    classifiers=[
        'Development Status :: 5 - Production/Stable',
        'Environment :: Console',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: MIT License',
        'Operating System :: OS Independent',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.2',
        'Programming Language :: Python :: 3.3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: Implementation :: CPython',
        'Programming Language :: Python :: Implementation :: PyPy',
        'Topic :: Software Development :: Libraries :: Python Modules',
    ],
)
