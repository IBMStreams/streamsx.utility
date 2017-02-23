/*******************************************************************************
 * * Copyright (C) 2015, International Business Machines Corporation
 * * All Rights Reserved
 * *******************************************************************************/

#include "utility.h"
#include <sched.h>
#include <numa.h>
#include <pthread.h>

// We are in directory sample.  This translates
// to namespace sample in SPL, and
// to namespace sample in C++
//
namespace com { namespace ibm { namespace streamsx { namespace utility {
    // Define all non-inline bodies here.   This will be put into a shared library

    // Use the SPL namespace for compactness, if you want
    using namespace SPL;

    uint64_t setCPUAffinity(uint64_t a)
    {
        cpu_set_t cpumask; // CPU affinity bit mask
        CPU_ZERO(&cpumask);
        CPU_SET(a, &cpumask);
        const int rc = sched_setaffinity(gettid(), sizeof cpumask, &cpumask);
        if (rc<0) THROW (SPLRuntimeOperator, "could not set processor affinity to " << a 
            << ", " << strerror(errno)); 

        return(a);
    }

    uint64_t setProcessNodeAffinity(uint64_t a)
    {
        // First make sure numa APIs are working
        if (-1 == numa_available()) THROW (SPLRuntimeOperator, "NUMA interfaces unavailable" );
            
        // Get all CPUs in this numa node and add it to the CPU_SET
        cpu_set_t cpuSet;
        CPU_ZERO(&cpuSet); 
        struct bitmask *nMask = numa_allocate_cpumask();
        numa_node_to_cpus(a, nMask);
        unsigned int nbits = 8 * numa_bitmask_nbytes(nMask);
        for(unsigned int i=0; i < nbits; i++) {
            if(numa_bitmask_isbitset(nMask, i)) {
	        CPU_SET(i, &cpuSet);
            }
        }
        numa_free_cpumask(nMask);
        pid_t pid = getpid();
        if (-1  == sched_setaffinity(pid, sizeof(cpu_set_t), &cpuSet)) THROW (SPLRuntimeOperator, "could not set node affinity to " << a << ", " << strerror(errno));
	return(a);
    }

    uint64_t setThreadNodeAffinity(uint64_t a)
    {
        // First make sure numa APIs are working
        if (-1 == numa_available()) THROW (SPLRuntimeOperator, "NUMA interfaces unavailable" );

        // Get all CPUs in this numa node and add it to the CPU_SET
        cpu_set_t cpuSet;
        CPU_ZERO(&cpuSet);
        struct bitmask *nMask = numa_allocate_cpumask();
        numa_node_to_cpus(a, nMask);
        unsigned int nbits = 8 * numa_bitmask_nbytes(nMask);
        for(unsigned int i=0; i < nbits; i++) {
            if(numa_bitmask_isbitset(nMask, i)) {
                CPU_SET(i, &cpuSet);
            }
        }
        numa_free_cpumask(nMask);
        if (-1  == sched_setaffinity(gettid(), sizeof(cpu_set_t), &cpuSet)) THROW (SPLRuntimeOperator, "could not set node affinity to " << a << ", " << strerror(errno));
        return(a);
    }

    uint64_t getNodeCount()
    {
        return(numa_max_node()+1);
    }

    uint64_t addCPUAffinity(uint64_t a)
    {
        cpu_set_t cpumask; // CPU affinity bit mask
        CPU_ZERO(&cpumask);
        const int rc_getaffinity = sched_getaffinity(gettid(), sizeof cpumask, &cpumask);
        if (rc_getaffinity<0) THROW (SPLRuntimeOperator, "could not get processor affinity"
            << ", " << strerror(errno));

        CPU_SET(a, &cpumask);
        const int rc = sched_setaffinity(gettid(), sizeof cpumask, &cpumask);
        if (rc<0) THROW (SPLRuntimeOperator, "could not set processor affinity to " << a
            << ", " << strerror(errno));

        return(a);
    }

    void setCPUAffinity(std::vector<uint64_t> a)
    {
        cpu_set_t cpumask; // CPU affinity bit mask
        CPU_ZERO(&cpumask);
        for (std::vector<uint64_t>::iterator it = a.begin(); it != a.end(); ++it) {
            CPU_SET(*it, &cpumask);
        }
        const int rc = sched_setaffinity(gettid(), sizeof cpumask, &cpumask);
        if (rc<0) THROW (SPLRuntimeOperator, "could not set processor affinity to CPUs"
            << ", " << strerror(errno));
    }

    void setThreadName(SPL::rstring name)
    {
        pthread_setname_np(pthread_self(), name.c_str());
    }

}}}}
