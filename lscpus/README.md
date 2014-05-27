## lscpus

Displays CPU utilization for PEs in a Streams instance.

# Synopsis
    lscpus [ -i instance_name] [ -s sort_by_key] [ -t ]

# Installation
Put the `lscpus` program anywhere in your `$PATH`.

# Description
Lscpus correlates information from `streamtool lspes` and the CPU utilization
from each host that those PEs are running on, as reported by `top`. The PE 
with the highest CPU utilization tends to be the bottleneck of the entire 
application. This information is useful when optimizing an application's 
performance.

The output is sorted by CPU utilization by default.

# Options
The options are:

 * `-i instance_name`
   Specifies the Streams instance to use. The default is the default for 
   `streamtool`.
 
 * `-s sort_by_key`
   Specifies how to sort the output. The default is `cpu`. The options are:
    * `id`
    * `state`
    * `rc`
    * `healthy`
    * `host`
    * `pid`
    * `jid`
    * `job`
    * `pes`
    * `cpu`
    * `virt`
    * `res`
    * `shr`

 * `-t`
   Turns on per-thread CPU utilization. By default, the CPU utilization for 
   each PE information for is aggregated into one number, which can be 
   larger than 100% if multiple threads are present. With this option on, 
   it lists a separate entry for each thread that has non-trivial CPU 
   utilization.

# Notes
The environment variable `$STREAMS_INSTALL` must be set.

If you have a `/etc/toprc` file which changes the default output format for 
`top`, then the parsing of the `top` output will likely be wrong, and this 
program will fail in unknown ways.

On the name: `lscpus` is a bit of a misnomer. If we were to follow the pattern 
of `lspes`, then it would be `unix_utility` + `streams_object` = `toppes`. But 
I don't like the look of that, and informal use of the name has caught on 
enough that I hesitate to change it.

# Author
Scott Schneider, scott.a.s@us.ibm.com

