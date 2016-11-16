from __future__ import print_function
import json
import logging
import subprocess
import unittest
import time

from streamsx import rest
from streamsx.topology import topology, context

credentials_file_name = 'sws_credentials.json'


class DelayedTupleSource:
    """
    The delay is needed, since it can take longer than ten seconds for the REST API to find a particular view, by
    which time the tuple has already passed.
    """
    def __init__(self, val):
        self.val = val

    def __call__(self):
        time.sleep(10)
        return self.val

class TestRestFeatures(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        """
        Initialize the logger and get the SWS username, password, and REST URL.
        :return: None
        """
        cls.logger = logging.getLogger('streamsx.test.rest_test')

        # Get credentials from creds file.
        creds_file = open(credentials_file_name, mode='r')
        try:
            creds_json = json.loads(creds_file.read())
            cls.sws_username = creds_json['username']
            cls.sws_password = creds_json['password']
        except:
            cls.logger.exception("Error while reading and parsing " + credentials_file_name)
            raise

        # Get the SWS REST URL
        try:
            process = subprocess.Popen(['streamtool', 'geturl', '--api'],
                                       stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
            cls.sws_rest_api_url = process.stdout.readline().strip().decode('utf-8')
        except:
            cls.logger.exception("Error getting SWS rest api url via streamtool")
            raise

    def test_ensure_correct_rest_module(self):
        # Ensure that the rest module being tested is from streamsx.utility
        self.assertTrue('streamsx.utility' in rest.__file__)

    def test_username_and_password(self):
        # Ensure, at minimun, that the StreamsContext can connect and retrieve valid data from the SWS resources path
        ctxt = rest.StreamsContext(self.sws_username, self.sws_password, self.sws_rest_api_url)
        resources = ctxt.get_resources()
        self.logger.debug("Number of retrieved resources is: " + str(len(resources)))
        self.assertGreater(len(resources), 0, msg="Returned zero resources from the \"resources\" endpoint.")

    def test_basic_view_support(self):
        top = topology.Topology('basicViewTest')
        # Send only one tuple
        view = top.source(DelayedTupleSource('hello')).view()
        self.logger.debug("Begging compilation and submission of basic_view_support topology.")
        context.submit(context.ContextTypes.DISTRIBUTED, top, username = self.sws_username, password=self.sws_password)

        queue = view.start_data_fetch()
        view_tuple_value = queue.get()['val']
        view.stop_data_fetch()

        self.logger.debug("Returned view value in basic_view_support is " + view_tuple_value)
        self.assertEquals(view_tuple_value, 'hello')