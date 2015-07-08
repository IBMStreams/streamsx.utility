/* begin_generated_IBM_copyright_prolog                             */
/*                                                                  */
/* This is an automatically generated copyright prolog.             */
/* After initializing,  DO NOT MODIFY OR MOVE                       */
/* **************************************************************** */
/* THIS SAMPLE CODE IS PROVIDED ON AN "AS IS" BASIS. IBM MAKES NO   */
/* REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, CONCERNING    */
/* USE OF THE SAMPLE CODE, OR THE COMPLETENESS OR ACCURACY OF THE   */
/* SAMPLE CODE. IBM DOES NOT WARRANT UNINTERRUPTED OR ERROR-FREE    */
/* OPERATION OF THIS SAMPLE CODE. IBM IS NOT RESPONSIBLE FOR THE    */
/* RESULTS OBTAINED FROM THE USE OF THE SAMPLE CODE OR ANY PORTION  */
/* OF THIS SAMPLE CODE.                                             */
/*                                                                  */
/* LIMITATION OF LIABILITY. IN NO EVENT WILL IBM BE LIABLE TO ANY   */
/* PARTY FOR ANY DIRECT, INDIRECT, SPECIAL OR OTHER CONSEQUENTIAL   */
/* DAMAGES FOR ANY USE OF THIS SAMPLE CODE, THE USE OF CODE FROM    */
/* THIS [ SAMPLE PACKAGE,] INCLUDING, WITHOUT LIMITATION, ANY LOST  */
/* PROFITS, BUSINESS INTERRUPTION, LOSS OF PROGRAMS OR OTHER DATA   */
/* ON YOUR INFORMATION HANDLING SYSTEM OR OTHERWISE.                */
/*                                                                  */
/* (C) Copyright IBM Corp. 2015  All Rights reserved.               */
/*                                                                  */
/* end_generated_IBM_copyright_prolog                               */

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
