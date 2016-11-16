import logging
import unittest
import sys, os
from os.path import dirname

if __name__ == '__main__':
    # Ensure correct rest.py is used. Prepend path to sys.path
    python_dir = dirname(dirname(os.path.realpath(__file__)))
    packages_dir = os.path.join(python_dir, 'packages')
    sys.path.insert(0, packages_dir)

    # Set up a logging framework. Test output from streams.test.* will percolate up to the streamsx.test logger
    # and be output on stdout.
    logging.basicConfig(stream=sys.stderr, level=logging.DEBUG)
    logging.getLogger('streamsx').setLevel(logging.DEBUG)

    # Search for valid test suites.
    suite = unittest.TestLoader().discover('.', pattern='*tests.py')
    unittest.TextTestRunner(verbosity=4).run(suite)
