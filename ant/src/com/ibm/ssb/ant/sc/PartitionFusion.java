package com.ibm.ssb.ant.sc;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

public enum PartitionFusion {
    
    manual {
        public String getOption() { return FDEF.getOption(); }
    },
    optimized {
        public String getOption() { return FOPT.getOption(); }
    },
    FDEF,
    FOPT;
    public String getOption() {
        return toString();
    }
}
