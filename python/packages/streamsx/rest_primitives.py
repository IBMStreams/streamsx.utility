# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016
import logging
import requests
import queue
import threading
import time
import json

from pprint import pprint, pformat
from requests.packages.urllib3.exceptions import InsecureRequestWarning

requests.packages.urllib3.disable_warnings(InsecureRequestWarning)

logger = logging.getLogger('streamsx.rest')

class StreamsRestClient(object):
    def __init__(self, username, password, resource_url):
        self.resource_url = resource_url
        # Create session to reuse TCP connection
        # https authentication
        self._username = username
        self._password = password

        session = requests.Session()
        session.auth = (username, password)
        session.verify = False

        self.session = session

    def make_request(self, url):
        logger.debug('Beginning a REST request to: ' + url)
        return self.session.get(url).json()

    def __str__(self):
        return pformat(self.__dict__)


class ViewThread(threading.Thread):
    """
    A thread which, when invoked, begins fetching data from the supplied view and populates the View.items queue.
    """
    def __init__(self, view):
        super(ViewThread, self).__init__()
        self.view = view
        self.stop = threading.Event()
        self.items = queue.Queue()

        self._last_collection_time = -1
        self._last_collection_time_count = 0

    def __call__(self):
        while not self._stopped():
            time.sleep(1)
            _items = self._get_deduplicated_view_items()
            if _items is not None:
                for itm in _items:
                    self.items.put(itm)

    def get_view_items(self):
        view_items = []
        for json_view_items in self.view.rest_client.make_request(self.view.viewItems)['viewItems']:
            view_items.append(ViewItem(json_view_items, self.view.rest_client))
        logger.debug("Retrieved " + str(len(view_items)) + " items from view " + self.view.name)
        return view_items

    def _get_deduplicated_view_items(self):
        # Retrieve the view object
        data_name = self.view.attributes[0]['name']
        items = self.get_view_items()
        data = []

        # The number of already seen tuples to ignore on the last millisecond time boundary
        ignore_last_collection_time_count = self._last_collection_time_count

        for item in items:
            # Ignore tuples from milliseconds we've already seen
            if item.collectionTime < self._last_collection_time:
                continue
            elif item.collectionTime == self._last_collection_time:
                # Ignore tuples within the millisecond which we've already seen.
                if ignore_last_collection_time_count > 0:
                    ignore_last_collection_time_count -= 1
                    continue

                # If we haven't seen it, continue
                data.append(json.loads(item.data[data_name]))
            else:
                data.append(json.loads(item.data[data_name]))

        if len(items) > 0:
            # Record the current millisecond time boundary.
            _last_collection_time = items[-1].collectionTime
            _last_collection_time_count = 0
            backwards_counter = len(items) - 1
            while backwards_counter >= 0 and items[backwards_counter].collectionTime == _last_collection_time:
                _last_collection_time_count += 1
                backwards_counter -= 1

            self._last_collection_time = _last_collection_time
            self._last_collection_time_count = _last_collection_time_count

        return data

    def _stopped(self):
        return self.stop.isSet()

class View(object):
    def __init__(self, json_view, rest_client):
        self.rest_client = rest_client
        for key in json_view:
            if key == 'self':
                self.__dict__["rest_self"] = json_view['self']
            else:
                self.__dict__[key] = json_view[key]

        self.view_thread = ViewThread(self)

    def get_domain(self):
        return Domain(self.rest_client.make_request(self.domain), self.rest_client)

    def get_instance(self):
        return Instance(self.rest_client.make_request(self.instance), self.rest_client)

    def get_job(self):
        return Job(self.rest_client.make_request(self.job), self.rest_client)

    def stop_data_fetch(self):
        self.view_thread.stop.set()

    def start_data_fetch(self):
        self.view_thread.stop.clear()
        t = threading.Thread(target=self.view_thread)
        t.start()
        return self.view_thread.items

    def get_view_items(self):
        view_items = []
        for json_view_items in self.rest_client.make_request(self.viewItems)['viewItems']:
            view_items.append(ViewItem(json_view_items, self.rest_client))
        logger.debug("Retrieved " + str(len(view_items)) + " items from view " + self.name)
        return view_items

    def __str__(self):
        return pformat(self.__dict__)

class ActiveView:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ViewItem:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ConfiguredView:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class Host:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class Job:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def get_views(self):
        views = []
        for json_view in self.rest_client.make_request(self.views)['views']:
            views.append(View(json_view, self.rest_client))
        return views

    def get_active_views(self):
        views = []
        for json_view in self.rest_client.make_request(self.activeViews)['activeViews']:
            views.append(ActiveView(json_view, self.rest_client))
        return views

    def get_domain(self):
        return Domain(self.rest_client.make_request(self.domain), self.rest_client)

    def get_instance(self):
        return Instance(self.rest_client.make_request(self.instance), self.rest_client)

    def get_hosts(self):
        hosts = []
        for json_rep in self.rest_client.make_request(self.hosts)['hosts']:
            hosts.append(Host(json_rep, self.rest_client))
        return hosts

    def get_operator_connections(self):
        operators_connections = []
        for json_rep in self.rest_client.make_request(self.operatorConnections)['operatorConnections']:
            operators_connections.append(OperatorConnection(json_rep, self.rest_client))
        return operators_connections

    def get_operators(self):
        operators = []
        for json_rep in self.rest_client.make_request(self.operators)['operators']:
            operators.append(Operator(json_rep, self.rest_client))
        return operators

    def get_pes(self):
        pes = []
        for json_rep in self.rest_client.make_request(self.pes)['pes']:
            pes.append(PE(json_rep, self.rest_client))
        return pes

    def get_pe_connections(self):
        pe_connections = []
        for json_rep in self.rest_client.make_request(self.peConnections)['peConnections']:
            pe_connections.append(PEConnection(json_rep, self.rest_client))
        return pe_connections

    def get_resource_allocations(self):
        resource_allocations = []
        for json_rep in self.rest_client.make_request(self.resourceAllocations)['resourceAllocations']:
            resource_allocations.append(ResourceAllocation(json_rep, self.rest_client))
        return resource_allocations

    def __str__(self):
        return pformat(self.__dict__)


class Operator:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class OperatorConnection:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class PE:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class PEConnection:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ResourceAllocation:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ActiveService:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class Installation:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ImportedStream:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class ExportedStream:
    def __init__(self, json_rep, rest_client):
        self.rest_client=rest_client
        for key in json_rep:
            if key == 'self':
                self.__dict__["rest_self"] = json_rep['self']
            else:
                self.__dict__[key] = json_rep[key]

    def __str__(self):
        return pformat(self.__dict__)


class Instance:
    def __init__(self, json_domain, rest_client):
        self.rest_client=rest_client
        for key in json_domain:
            if key == 'self':
                self.__dict__["rest_self"] = json_domain['self']
            else:
                self.__dict__[key] = json_domain[key]

        self.active_version = ActiveVersion(json_domain['activeVersion'])

    def get_operators(self):
        operators = []
        for json_rep in self.rest_client.make_request(self.operators)['operators']:
            operators.append(Operator(json_rep, self.rest_client))
        return operators

    def get_operator_connections(self):
        operators_connections = []
        for json_rep in self.rest_client.make_request(self.operatorConnections)['operatorConnections']:
            operators_connections.append(OperatorConnection(json_rep, self.rest_client))
        return operators_connections

    def get_pes(self):
        pes = []
        for json_rep in self.rest_client.make_request(self.pes)['pes']:
            pes.append(PE(json_rep, self.rest_client))
        return pes

    def get_pe_connections(self):
        pe_connections = []
        for json_rep in self.rest_client.make_request(self.peConnections)['peConnections']:
            pe_connections.append(PEConnection(json_rep, self.rest_client))
        return pe_connections

    def get_views(self):
        views = []
        for json_view in self.rest_client.make_request(self.views)['views']:
            views.append(View(json_view, self.rest_client))
        return views

    def get_hosts(self):
        hosts = []
        for json_rep in self.rest_client.make_request(self.hosts)['hosts']:
            hosts.append(Host(json_rep, self.rest_client))
        return hosts

    def get_domain(self):
        return Domain(self.rest_client.make_request(self.domain), self.rest_client)

    def get_active_views(self):
        views = []
        for json_view in self.rest_client.make_request(self.activeViews)['activeViews']:
            views.append(ActiveView(json_view, self.rest_client))
        return views

    def get_configured_views(self):
        views = []
        for json_view in self.rest_client.make_request(self.configuredViews)['configuredViews']:
            views.append(ConfiguredView(json_view, self.rest_client))
        return views

    def get_jobs(self):
        jobs = []
        for json_rep in self.rest_client.make_request(self.jobs)['jobs']:
            jobs.append(Job(json_rep, self.rest_client))
        return jobs

    def get_imported_streams(self):
        imported_streams = []
        for json_rep in self.rest_client.make_request(self.importedStreams)['importedStreams']:
            imported_streams.append(ImportedStream(json_rep, self.rest_client))
        return imported_streams

    def get_exported_streams(self):
        exported_streams = []
        for json_rep in self.rest_client.make_request(self.exportedStreams)['exportedStreams']:
            exported_streams.append(ExportedStream(json_rep, self.rest_client))
        return exported_streams

    def get_active_services(self):
        active_services = []
        for json_rep in self.rest_client.make_request(self.activeServices)['activeServices']:
            active_services.append(ActiveService(json_rep, self.rest_client))
        return active_services

    def get_resource_allocations(self):
        resource_allocations = []
        for json_rep in self.rest_client.make_request(self.resourceAllocations)['resourceAllocations']:
            resource_allocations.append(ResourceAllocation(json_rep, self.rest_client))
        return resource_allocations

    def __str__(self):
        return pformat(self.__dict__)


class ResourceTag(object):
    def __init__(self, json_resource_tag):
        self.definition_format_properties = json_resource_tag['definitionFormatProperties']
        self.description = json_resource_tag['description']
        self.name = json_resource_tag['name']
        self.properties_definition = json_resource_tag['propertiesDefinition']
        self.reserved = json_resource_tag['reserved']

    def __str__(self):
        return pformat(self.__dict__)


class ActiveVersion(object):
    def __init__(self, json_active_version):
        self.architecture = json_active_version['architecture']
        self.build_version = json_active_version['buildVersion']
        self.edition_name = json_active_version['editionName']
        self.full_product_version = json_active_version['fullProductVersion']
        self.minimum_os_base_version = json_active_version['minimumOSBaseVersion']
        self.minimum_os_patch_version = json_active_version['minimumOSPatchVersion']
        self.minimum_os_version = json_active_version['minimumOSVersion']
        self.product_name = json_active_version['productName']
        self.product_version = json_active_version['productVersion']

    def __str__(self):
        return pformat(self.__dict__)


class Domain:
    def __init__(self, json_domain, rest_client):
        self.rest_client=rest_client
        for key in json_domain:
            if key == 'self':
                self.__dict__["rest_self"] = json_domain['self']
            self.__dict__[key] = json_domain[key]

        self.activeVersion = ActiveVersion(json_domain['activeVersion'])
        self.resourceTags = []
        for _json_resource_tag in json_domain['resourceTags']:
            self.resourceTags.append(ResourceTag(_json_resource_tag))

    def get_instances(self):
        instances = []
        for json_instance in self.rest_client.make_request(self.instances)['instances']:
            instances.append(Instance(json_instance, self.rest_client))
        return instances

    def get_hosts(self):
        hosts = []
        for json_rep in self.rest_client.make_request(self.hosts)['hosts']:
            hosts.append(Host(json_rep, self.rest_client))
        return hosts

    def get_active_services(self):
        active_services = []
        for json_rep in self.rest_client.make_request(self.activeServices)['activeServices']:
            active_services.append(ActiveService(json_rep, self.rest_client))
        return active_services

    def get_resource_allocations(self):
        resource_allocations = []
        for json_rep in self.rest_client.make_request(self.resourceAllocations)['resourceAllocations']:
            resource_allocations.append(ResourceAllocation(json_rep, self.rest_client))
        return resource_allocations

    def get_resources(self):
        resources = []
        json_resources = self.rest_client.make_request(self.resource_url)['resources']
        for json_resource in json_resources:
            resources.append(Resource(json_resource, self.rest_client))
        return resources

    def __str__(self):
        return pformat(self.__dict__)


class Resource:
    def __init__(self, json_resource, rest_client):
        self.rest_client=rest_client
        self.name = json_resource['name']
        self.resource = json_resource['resource']

    def get_resource(self):
        return self.rest_client.make_request(self.resource)

    def __str__(self):
        return pformat(self.__dict__)


def get_view_obj(_view, rc):
    for domain in rc.get_domains():
        for instance in domain.get_instances():
            for view in instance.get_views():
                if view.name == _view.name:
                    return view
    return None

