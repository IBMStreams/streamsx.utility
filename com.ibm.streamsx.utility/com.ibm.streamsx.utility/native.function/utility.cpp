/*******************************************************************************
 * * Copyright (C) 2015, International Business Machines Corporation
 * * All Rights Reserved
 * *******************************************************************************/

#include "utility.h"

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
}}}}
