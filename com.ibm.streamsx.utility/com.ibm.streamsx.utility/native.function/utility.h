/*******************************************************************************
 * * Copyright (C) 2015, International Business Machines Corporation
 * * All Rights Reserved
 * *******************************************************************************/

// Define SPL types and functions
#include "SPL/Runtime/Function/SPLFunctions.h"

namespace com { namespace ibm { namespace streamsx { namespace utility {
    uint64_t setCPUAffinity(uint64_t a); 
    uint64_t setProcessNodeAffinity(uint64_t a);
    uint64_t getNodeCount(); 
    uint64_t setCPUAffinity_add(uint64_t a);
    void setCPUAffinity_list(std::vector<uint64_t> a);
}}}}
