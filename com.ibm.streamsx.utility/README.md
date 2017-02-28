Streams Performance & Other Utilities Toolkit
Copyright (C) 2015-2017, International Business Machines Corporation
All rights reserved.

US Government Users Restricted Rights -
Use, duplication or disclosure restricted
by GSA ADP Schedule Contract with IBM Corp.


Utilities Toolkit
=================

Description
-----------

Provides some helper functions to help SPL applications control CPU and NUMA
thread affinities, and other pthread control.

Also adds some performance helper and monitoring operators.

Dependencies
------------

libnuma and glibc version 2.12 or newer must be available.

In particular, SuSE Linux Enterprise Server 11 SP3 and earlier do not have
a new enough version of glibc.

Documentation and Sample Applications
-------------------------------------

See the sample application at samples/affinity.spl for simple usage of this
toolkit.

