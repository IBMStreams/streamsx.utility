from __future__ import print_function
import json
import logging
import subprocess
import time
import unittest
from test_operators import DelayedTupleSourceWithLastTuple

from streamsx import rest
from streamsx.topology import topology, context

credentials_file_name = 'sws_credentials.json'
vcap_service_config_file_name = 'vcap_service_config.json'

logger = logging.getLogger('streamsx.test.rest_test')

class CommonTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        """
        Initialize the logger and get the SWS username, password, and REST URL.
        :return: None
        """
        if cls is CommonTests:
            raise unittest.SkipTest("Skip CommonTests, it's a base class")

    def test_ensure_correct_rest_module(self):
        self.logger.debug("Beginning test: test_ensure_correct_rest_module.")
        # Ensure that the rest module being tested is from streamsx.utility
        self.assertTrue('streamsx.utility' in rest.__file__)

    def test_username_and_password(self):
        self.logger.debug("Beginning test: test_username_and_password.")
        # Ensure, at minimum, that the StreamsContext can connect and retrieve valid data from the SWS resources path
        ctxt = rest.StreamsContext(self.sws_username, self.sws_password, self.sws_rest_api_url)
        resources = ctxt.get_resources()
        self.logger.debug("Number of retrieved resources is: " + str(len(resources)))
        self.assertGreater(len(resources), 0, msg="Returned zero resources from the \"resources\" endpoint.")

    def test_basic_view_support(self):
        self.logger.debug("Beginning test: test_basic_view_support.")
        top = topology.Topology('basicViewTest')
        # Send only one tuple
        view = top.source(DelayedTupleSourceWithLastTuple(['hello'], 20)).view()
        self.logger.debug("Begging compilation and submission of basic_view_support topology.")

        self._submit(top)

        time.sleep(10)

        queue = view.start_data_fetch()

        try:
            view_tuple_value = queue.get(block=True, timeout=20.0)
        except:
            view.stop_data_fetch()
            logger.exception("Timed out while waiting for tuple.")
            raise

        self.logger.debug("Returned view value in basic_view_support is " + view_tuple_value)
        self.assertTrue(view_tuple_value.startswith('hello'))