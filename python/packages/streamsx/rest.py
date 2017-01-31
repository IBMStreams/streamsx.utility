# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016
import requests
import time
import numpy as np
import matplotlib.pyplot as plt
import json

from .rest_primitives import Domain, Instance, Installation, Resource, StreamsRestClient
from pprint import pformat
from requests.packages.urllib3.exceptions import InsecureRequestWarning

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)


class StreamsContext:
    def __init__(self, username, password, resource_url):
        self.rest_client = StreamsRestClient(username, password, resource_url)
        self.resource_url = resource_url

    def get_domains(self):
        domains = []
        for resource in self.get_resources():
            # Get list of domains
            if resource.name == "domains":
                for json_domain in resource.get_resource()['domains']:
                    domains.append(Domain(json_domain, self.rest_client))
        return domains

    def get_instances(self):
        instances = []
        for resource in self.get_resources():
            # Get list of domains
            if resource.name == "instances":
                for json_rep in resource.get_resource()['instances']:
                    instances.append(Instance(json_rep, self.rest_client))
        return instances

    def get_installations(self):
        installations = []
        for resource in self.get_resources():
            # Get list of domains
            if resource.name == "installations":
                for json_rep in resource.get_resource()['installations']:
                    installations.append(Installation(json_rep, self.rest_client))
        return installations

    def get_resources(self):
        resources = []
        json_resources = self.rest_client.make_request(self.resource_url)['resources']
        for json_resource in json_resources:
            resources.append(Resource(json_resource, self.rest_client))
        return resources

    def __str__(self):
        return pformat(self.__dict__)

def get_view_obj(_view, rc):
    for domain in rc.get_domains():
        for instance in domain.get_instances():
            for view in instance.get_views():
                if view.name == _view.name:
                    return view
    return None


