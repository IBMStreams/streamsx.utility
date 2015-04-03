package com.ibm.ssb.ant;
/*
** Licensed Materials - Property of IBM
** Copyright IBM Corp. 2013
** US Government Users Restricted Rights - Use, duplication or
** disclosure restricted by GSA ADP Schedule Contract with
** IBM Corp.
*/

import java.io.File;

public class FileLocation {
    
    private File location;
    
    public void setLocation(File location) {
        this.location = location;
    }

    public File getLocation() {
        return location;
    }

}
